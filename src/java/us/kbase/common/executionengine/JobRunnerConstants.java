package us.kbase.common.executionengine;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;


public class JobRunnerConstants {

    //TODO NJS_SDK move to shared repo
    
    public static final String JOB_CONFIG_FILE = "config.properties";

    //TODO consider an enum here
    public static final String DEV = "dev";
    public static final String BETA = "beta";
    public static final String RELEASE = "release";
    public static final Set<String> RELEASE_TAGS =
            Collections.unmodifiableSet(new LinkedHashSet<String>(
                    Arrays.asList(DEV, BETA, RELEASE)));
    
    public static final String CFG_PROP_KBASE_ENDPOINT = "kbase.endpoint";
    public static final String CFG_PROP_WORKSPACE_SRV_URL =
            "workspace.srv.url";
    public static final String CFG_PROP_SHOCK_URL = "shock.url";
    public static final String CFG_PROP_JOBSTATUS_SRV_URL =
            "jobstatus.srv.url";
    public static final String CFG_PROP_CATALOG_SRV_URL = "catalog.srv.url";
    public static final String CFG_PROP_AWE_CLIENT_DOCKER_URI =
            "awe.client.docker.uri";
    public static final String CFG_PROP_EE_SERVER_VERSION = 
            "ee.server.version";

}
