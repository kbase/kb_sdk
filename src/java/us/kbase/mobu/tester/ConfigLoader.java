package us.kbase.mobu.tester;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import us.kbase.auth.AuthConfig;
import us.kbase.auth.AuthToken;
import us.kbase.auth.ConfigurableAuthService;
import us.kbase.common.executionengine.CallbackServerConfigBuilder;
import us.kbase.common.executionengine.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.common.executionengine.LineLogger;

public class ConfigLoader {
    private final String authUrl;
    private final String authAllowInsecure;
    private final AuthToken token;
    private final String endPoint;
    private final String jobSrvUrl;
    private final String wsUrl;
    private final String shockUrl;
    private final String handleUrl;
    private final String srvWizUrl;
    private final String njswUrl;
    private final String catalogUrl;
    private final Map<String, String> secureCfgParams;

    public ConfigLoader(Properties props, boolean testMode, 
            String configPathInfo, boolean tryHomeCfg) throws Exception {
        if (configPathInfo == null) {
            configPathInfo = "test_local/test.cfg";
        }
        authUrl = props.getProperty("auth_service_url");
        if (authUrl == null)
            throw new IllegalStateException("Error: 'auth_service_url' parameter is not set in " +
                    configPathInfo);
        String testPrefix = testMode ? "test_" : "";
        String user = props.getProperty(testPrefix + "user");
        if (user != null && user.trim().isEmpty()) {
            user = null;
        }
        String password = props.getProperty(testPrefix + "password");
        String tokenString = props.getProperty(testPrefix + "token");
        if (tokenString != null && tokenString.trim().isEmpty()) {
            tokenString = null;
        }
        if (user == null && tokenString == null) {
            throw new IllegalStateException("Error: KBase account credentials are not set in " +
                    configPathInfo);
        }
        authAllowInsecure = props.getProperty("auth_service_url_allow_insecure");
        ConfigurableAuthService auth = new ConfigurableAuthService(
                new AuthConfig().withKBaseAuthServerURL(new URL(authUrl))
                .withAllowInsecureURLs("true".equals(authAllowInsecure)));
        if (tokenString != null) {
            token = auth.validateToken(tokenString);
        } else {
            if (password == null || password.trim().isEmpty()) {
                System.out.println("You haven't preset your password in " +configPathInfo + 
                        " file. Please enter it now.");
                password = new String(System.console().readPassword("Password: "));
            }
            token = auth.login(user.trim(), password.trim()).getToken();
        }
        endPoint = props.getProperty("kbase_endpoint");
        if (endPoint == null)
            throw new IllegalStateException("Error: KBase services end-point is not set in " +
                    configPathInfo);
        jobSrvUrl = getConfigUrl(props, "job_service_url", endPoint, "userandjobstate");
        wsUrl = getConfigUrl(props, "workspace_url", endPoint, "ws");
        shockUrl = getConfigUrl(props, "shock_url", endPoint, "shock-api");
        handleUrl = getConfigUrl(props, "handle_url", endPoint, "handle_service");
        srvWizUrl = getConfigUrl(props, "srv_wiz_url", endPoint, "service_wizard");
        njswUrl = getConfigUrl(props, "njsw_url", endPoint, "njs_wrapper");
        catalogUrl = getConfigUrl(props, "catalog_url", endPoint, "catalog");
        secureCfgParams = new TreeMap<String, String>();
        for (Object propObj : props.keySet()) {
            String propName = propObj.toString();
            if (propName.startsWith("secure.")) {
                String paramName = propName.substring(7);
                secureCfgParams.put(paramName, props.getProperty(propName));
            }
        }
    }
    
    public String getAuthUrl() {
        return authUrl;
    }
    
    public String getAuthAllowInsecure() {
        return authAllowInsecure;
    }
    
    public AuthToken getToken() {
        return token;
    }
    
    public String getEndPoint() {
        return endPoint;
    }
    
    public String getCatalogUrl() {
        return catalogUrl;
    }
    
    public String getHandleUrl() {
        return handleUrl;
    }
    
    public String getJobSrvUrl() {
        return jobSrvUrl;
    }
    
    public String getNjswUrl() {
        return njswUrl;
    }
    
    public String getShockUrl() {
        return shockUrl;
    }
    
    public String getSrvWizUrl() {
        return srvWizUrl;
    }
    
    public String getWsUrl() {
        return wsUrl;
    }
    
    public void generateConfigProperties(File configPropsFile) throws Exception {
        PrintWriter pw = new PrintWriter(configPropsFile);
        try {
            pw.println("[global]");
            pw.println("kbase_endpoint = " + endPoint);
            pw.println("job_service_url = " + jobSrvUrl);
            pw.println("workspace_url = " + wsUrl);
            pw.println("shock_url = " + shockUrl);
            pw.println("handle_url = " + handleUrl);
            pw.println("srv_wiz_url = " + srvWizUrl);
            pw.println("njsw_url = " + njswUrl);
            pw.println("auth_service_url = " + authUrl);
            pw.println("auth_service_url_allow_insecure = " + 
                    (authAllowInsecure == null ? "false" : authAllowInsecure));
            for (String param : secureCfgParams.keySet()) {
                pw.println(param + " = " + secureCfgParams.get(param));
            }
        } finally {
            pw.close();
        }
    }
    
    public CallbackServerConfig buildCallbackServerConfig(
            URL callbackUrl, Path workDir, LineLogger logger) throws Exception {
        return new CallbackServerConfigBuilder(
                new URL(endPoint), new URL(wsUrl), new URL(shockUrl), new URL(jobSrvUrl),
                new URL(handleUrl), new URL(srvWizUrl), new URL(njswUrl), new URL(authUrl),
                authAllowInsecure, new URL(catalogUrl), callbackUrl, workDir, logger).build();
    }
    
    private static String getConfigUrl(Properties props, String key, String endPoint, 
            String defaultUrlSuffix) {
        String ret = props.getProperty(key);
        return ret == null ? (endPoint + "/" + defaultUrlSuffix) : ret;
    }
}
