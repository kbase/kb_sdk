package us.kbase.mobu.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import us.kbase.catalog.CatalogClient;
import us.kbase.catalog.ModuleInfo;
import us.kbase.catalog.ModuleVersionInfo;
import us.kbase.catalog.SelectModuleVersionParams;
import us.kbase.catalog.SelectOneModuleParams;
import us.kbase.common.service.UObject;
import us.kbase.mobu.util.DirUtils;

public class SubsequentCallRunner {
    private static final Set<String> asyncVersionTags = Collections.unmodifiableSet(
            new LinkedHashSet<String>(Arrays.asList("dev", "beta", "release")));

    private String jobId;
    private File moduleDir;
    private File testLocalDir;
    private File sharedScratchDir;
    private File jobDir;
    private File jobWorkDir;
    private String methodName;
    private String imageName;
    private List<UObject> params;
    
    public SubsequentCallRunner(String method, String serviceVer, 
            List<UObject> params) throws Exception {
        this(DirUtils.findModuleDir(), method, serviceVer, params);
    }
    
    public SubsequentCallRunner(File moduleDir, String methodName, 
            String serviceVer, List<UObject> params) throws Exception {
        this.moduleDir = moduleDir;
        this.testLocalDir = new File(this.moduleDir, "test_local");
        Properties props = new Properties();
        InputStream is = new FileInputStream(new File(testLocalDir, "test.cfg"));
        try {
            props.load(is);
        } finally {
            is.close();
        }
        String kbaseEndpoint = props.getProperty("kbase_endpoint");
        String catalogUrl = kbaseEndpoint + "/catalog";
        System.out.println("Catalog URL: " + catalogUrl);
        CatalogClient catClient = new CatalogClient(new URL(catalogUrl));
        String moduleName = methodName.substring(0, methodName.indexOf('.'));
        //BasicModuleVersionInfo mvi = catClient.moduleVersionLookup(
        //        new ModuleVersionLookupParams().withModuleName(moduleName).withLookup(serviceVer));
        String imageVersion = serviceVer;
        ModuleVersionInfo mvi = null;
        if (imageVersion == null || asyncVersionTags.contains(imageVersion)) {
            ModuleInfo mi = catClient.getModuleInfo(new SelectOneModuleParams().withModuleName(moduleName));
            if (imageVersion == null) {
                mvi = mi.getRelease();
            } else if (imageVersion.equals("dev")) {
                mvi = mi.getDev();
            } else if (imageVersion.equals("beta")) {
                mvi = mi.getBeta();
            } else {
                mvi = mi.getRelease();
            }
            if (mvi == null)
                throw new IllegalStateException("Cannot extract " + imageVersion + " version for module: " + moduleName);
            imageVersion = mvi.getGitCommitHash();
        } else {
            try {
                mvi = catClient.getVersionInfo(new SelectModuleVersionParams()
                        .withModuleName(moduleName).withGitCommitHash(imageVersion));
            } catch (Exception ex) {
                throw new IllegalStateException("Error retrieving module version info about image " +
                        moduleName + " with version " + imageVersion, ex);
            }
        }
        System.out.println("Catalog returned: " + mvi.toString());
        imageName = mvi.getDockerImgName();
        jobId = imageName.replace(':', '_').replace('/', '_');
        this.sharedScratchDir = new File(this.testLocalDir, "scratch");
        File subjobsDir = new File(testLocalDir, "subjobs");
        this.jobDir = new File(subjobsDir, jobId);
        this.jobWorkDir = new File(jobDir, "workdir");
    }
    
    public Map<String, Object> run() throws Exception {
        return null;
    }
}
