package us.kbase.scripts.test;

import java.io.File;
import java.net.URL;

import org.ini4j.Ini;

import us.kbase.auth.AuthConfig;
import us.kbase.auth.AuthToken;
import us.kbase.auth.ConfigurableAuthService;

public class TestConfigHelper {
    public static final String TEST_CFG = "kb_sdk_test";

    private static boolean initialized = false;
    private static AuthToken token1 = null;
    private static AuthToken token2 = null;
    
    public static void init() throws Exception {
        final Ini testini = new Ini(new File("test_scripts/test.cfg"));
        for (Object key: testini.get(TEST_CFG).keySet()) {
            String prop = key.toString();
            String value = testini.get(TEST_CFG, key);
            System.setProperty(prop, value);
        }
        initialized = true;
    }
    
    public static String getTestConfigParam(String param, boolean required) throws Exception {
        if (!initialized) {
            init();
        }
        String ret = System.getProperty(param);
        if (required && ret == null) {
            throw new IllegalStateException("Parameter [" + param + "] is not set " +
            		"in test configuraion. Please check test_scripts/test.cfg file");
        }
        return ret;
    }

    public static String getTestConfigParam(String param, String defaultValue) throws Exception {
        String ret = getTestConfigParam(param, false);
        return ret == null ? defaultValue : ret;
    }
    
    public static String getAuthServiceUrl() throws Exception {
        return getTestConfigParam("test.auth.url", true);
    }
    
    public static String getAuthServiceUrlInsecure() throws Exception {
        return getTestConfigParam("test.auth.url.insecure", "false");
    }
    
    public static ConfigurableAuthService getAuthService() throws Exception {
        return new ConfigurableAuthService(
                new AuthConfig().withKBaseAuthServerURL(new URL(getAuthServiceUrl()))
                .withAllowInsecureURLs("true".equals(getAuthServiceUrlInsecure())));
    }
    
    public static AuthToken getToken() throws Exception {
        ConfigurableAuthService authService = getAuthService();
        if (token1 == null) {
            String tokenString = getTestConfigParam("test.token", true);
            token1 = authService.validateToken(tokenString);
        }
        return token1;
    }

    public static AuthToken getToken2() throws Exception {
        ConfigurableAuthService authService = getAuthService();
        if (token2 == null) {
            String tokenString = getTestConfigParam("test.token2", true);
            token2 = authService.validateToken(tokenString);
        }
        return token2;
    }
    
    public String getKBaseEndpoint() throws Exception {
        return getTestConfigParam("test.kbase.endpoint", true);
    }
}
