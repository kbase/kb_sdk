package us.kbase.common.executionengine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public class CallbackServerConfigBuilder {
    //TODO NJS_SDK move to shared repo
    
    //TODO unit tests
    
    private static final  String EP_WS = "ws";
    private static final  String EP_SHOCK = "shock-api";
    private static final  String EP_CAT = "catalog";
    private static final  String EP_UJS = "userandjobstate";
    
    private static final String KBASE_EP = "kbase_endpoint";
    private static final String UJS_URL = "job_service_url";
    private static final String WS_URL = "workspace_url";
    private static final String SHOCK_URL = "shock_url";
    
    final private URL kbaseEndpointURL;
    private URL workspaceURL;
    private URL shockURL;
    private URL userJobStateURL;
    private URL catalogURL;
    private URI dockerURI = null;
    final private URL callbackURL;
    final private Path workDir;
    final private LineLogger logger;
    
    public CallbackServerConfigBuilder(
            final URL kbaseEndpointURL,
            final URL callbackURL,
            final Path workDir,
            final LineLogger logger) {
        super();
        checkNulls(kbaseEndpointURL, callbackURL, workDir, logger);
        this.kbaseEndpointURL = kbaseEndpointURL;
        
        this.workspaceURL = resolveURL(kbaseEndpointURL, EP_WS);
        this.shockURL = resolveURL(kbaseEndpointURL, EP_SHOCK);
        this.userJobStateURL = resolveURL(kbaseEndpointURL, EP_UJS);
        this.catalogURL = resolveURL(kbaseEndpointURL, EP_CAT);
        
        this.callbackURL = callbackURL;
        this.workDir = workDir;
        this.logger = logger;
    }

    public CallbackServerConfigBuilder(
            final Map<String, String> config,
            final URL callbackURL,
            final Path workDir,
            final LineLogger logger) {
        super();
        checkNulls(config, callbackURL, workDir, logger);
        this.kbaseEndpointURL = getURL(config,
                JobRunnerConstants.CFG_PROP_KBASE_ENDPOINT);
        
        this.workspaceURL = resolveURL(kbaseEndpointURL, EP_WS);
        this.shockURL = resolveURL(kbaseEndpointURL, EP_SHOCK);
        this.userJobStateURL = resolveURL(kbaseEndpointURL, EP_UJS);
        this.catalogURL = resolveURL(kbaseEndpointURL, EP_CAT);
        
        this.workspaceURL = optionallyGetURLfromConfig(config,
                JobRunnerConstants.CFG_PROP_WORKSPACE_SRV_URL,
                this.workspaceURL);
        this.shockURL = optionallyGetURLfromConfig(config,
                JobRunnerConstants.CFG_PROP_SHOCK_URL,
                this.shockURL);
        this.userJobStateURL = optionallyGetURLfromConfig(config,
                JobRunnerConstants.CFG_PROP_JOBSTATUS_SRV_URL,
                this.userJobStateURL);
        this.catalogURL = optionallyGetURLfromConfig(config,
                JobRunnerConstants.CFG_PROP_CATALOG_SRV_URL,
                this.catalogURL);
        
        this.dockerURI = getURI(config,
                JobRunnerConstants.CFG_PROP_AWE_CLIENT_DOCKER_URI,
                true);
        this.callbackURL = callbackURL;
        this.workDir = workDir;
        this.logger = logger;
    }
    
    public CallbackServerConfigBuilder withWorkspaceURL(
            final URL workspaceURL) {
        checkNulls(workspaceURL);
        this.workspaceURL = workspaceURL;
        return this;
    }

    public CallbackServerConfigBuilder withShockURL(final URL shockURL) {
        checkNulls(shockURL);
        this.shockURL = shockURL;
        return this;
    }

    public CallbackServerConfigBuilder withUserJobStateURL(
            final URL userJobStateURL) {
        checkNulls(userJobStateURL);
        this.userJobStateURL = userJobStateURL;
        return this;
    }

    public CallbackServerConfigBuilder withCatalogURL(final URL catalogURL) {
        checkNulls(catalogURL);
        this.catalogURL = catalogURL;
        return this;
    }
    
    public CallbackServerConfigBuilder withDockerURI(final URI dockerURI) {
        checkNulls(dockerURI);
        this.dockerURI = dockerURI;
        return this;
    }

    public CallbackServerConfig build() {
        return new CallbackServerConfig(
                kbaseEndpointURL, workspaceURL, shockURL, userJobStateURL,
                catalogURL, dockerURI, callbackURL, workDir, logger);
    }


    private static URL optionallyGetURLfromConfig(
            final Map<String, String> config,
            final String param,
            final URL default_) {
        final String pval = config.get(param);
        if (pval == null || pval.isEmpty()) {
            return default_;
        }
        return getURL(config, param);
    }

    private static void checkNulls(Object... objs) {
        for (int i = 0; i < objs.length; i++) {
            if (objs[i] == null) {
                throw new NullPointerException(
                        "Null constructor arguments are not allowed");
            }
        }
    }

    
    private static URL resolveURL(URL url, final String ext) {
        try {
            if (!url.getPath().endsWith("/")) {
                url = new URL(url.toExternalForm() + "/");
            }
            return new URL(url, ext);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e); //something is really messed up
        }
    }

    private static URL getURL(
            final Map<String, String> config,
            final String param) {
        final String urlStr = getParam(config, param, false);
        try {
            return new URL(urlStr);
        } catch (MalformedURLException mal) {
            throw new IllegalStateException("The configuration parameter '" +
                    param + " = " + urlStr + "' is not a valid URL");
        }
    }

    private static String getParam(
            final Map<String, String> config,
            final String param,
            final boolean allowMissing) {
        final String pval = config.get(param);
        if (pval == null || pval.isEmpty()) {
            if (allowMissing) {
                return null;
            }
            throw new IllegalStateException("Parameter '" + param +
                    "' is not defined in configuration");
        }
        return pval;
    }
    
    private static URI getURI(
            final Map<String, String> config,
            final String param,
            final boolean allowMissing) {
        final String urlStr = getParam(config, param, allowMissing);
        if (urlStr == null) {
            return null;
        }
        try {
            return new URI(urlStr);
        } catch (URISyntaxException use) {
            throw new IllegalStateException("The configuration parameter '" +
                    param + " = " + urlStr + "' is not a valid URI");
        }
    }
    
    public class CallbackServerConfig {
        final private URL kbaseEndpointURL;
        final private URL workspaceURL;
        final private URL shockURL;
        final private URL userJobStateURL;
        final private URL catalogURL;
        final private URI dockerURI;
        final private URL callbackURL;
        final private Path workDir;
        final private LineLogger logger;

        private CallbackServerConfig(URL kbaseEndpointURL, URL workspaceURL,
                URL shockURL, URL userJobStateURL, URL catalogURL,
                URI dockerURI, URL callbackURL, Path workDir,
                LineLogger logger) {
            super();
            this.kbaseEndpointURL = kbaseEndpointURL;
            this.workspaceURL = workspaceURL;
            this.shockURL = shockURL;
            this.userJobStateURL = userJobStateURL;
            this.catalogURL = catalogURL;
            this.dockerURI = dockerURI;
            this.callbackURL = callbackURL;
            this.workDir = workDir;
            this.logger = logger;
        }

        /**
         * @return the kbaseEndpointURL
         */
        public URL getKbaseEndpointURL() {
            return kbaseEndpointURL;
        }

        /**
         * @return the workspaceURL
         */
        public URL getWorkspaceURL() {
            return workspaceURL;
        }

        /**
         * @return the shockURL
         */
        public URL getShockURL() {
            return shockURL;
        }

        /**
         * @return the userJobStateURL
         */
        public URL getUserJobStateURL() {
            return userJobStateURL;
        }

        /**
         * @return the catalogURL
         */
        public URL getCatalogURL() {
            return catalogURL;
        }

        /**
         * @return the dockerURI
         */
        public URI getDockerURI() {
            return dockerURI;
        }

        /**
         * @return the callbackURL
         */
        public URL getCallbackURL() {
            return callbackURL;
        }

        /**
         * @return the workDir
         */
        public Path getWorkDir() {
            return workDir;
        }

        /**
         * @return the logger
         */
        public LineLogger getLogger() {
            return logger;
        }
        
        public void writeJobConfigToFile(final Path configFile)
                throws IOException {
            checkNulls(configFile);
            Files.write(configFile, Arrays.asList(
                    "[global]",
                    KBASE_EP  + "=" + kbaseEndpointURL,
                    UJS_URL   + "=" + userJobStateURL,
                    WS_URL    + "=" + workspaceURL,
                    SHOCK_URL + "=" + shockURL),
                    StandardCharsets.UTF_8);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("CallbackServerConfig [kbaseEndpointURL=");
            builder.append(kbaseEndpointURL);
            builder.append(", workspaceURL=");
            builder.append(workspaceURL);
            builder.append(", shockURL=");
            builder.append(shockURL);
            builder.append(", userJobStateURL=");
            builder.append(userJobStateURL);
            builder.append(", catalogURL=");
            builder.append(catalogURL);
            builder.append(", dockerURI=");
            builder.append(dockerURI);
            builder.append(", callbackURL=");
            builder.append(callbackURL);
            builder.append(", workDir=");
            builder.append(workDir);
            builder.append(", logger=");
            builder.append(logger);
            builder.append("]");
            return builder.toString();
        }
    }
}
