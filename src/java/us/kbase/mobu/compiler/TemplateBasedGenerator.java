package us.kbase.mobu.compiler;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import us.kbase.jkidl.FileIncludeProvider;
import us.kbase.jkidl.IncludeProvider;
import us.kbase.kidl.KbService;
import us.kbase.kidl.KidlParser;
import us.kbase.mobu.util.FileSaver;
import us.kbase.templates.TemplateFormatter;

public class TemplateBasedGenerator {

    public static void generate(Reader specReader, String defaultUrl, 
            boolean genJs, String jsClientName,
            boolean genPerl, String perlClientName, boolean genPerlServer, 
            String perlServerName, String perlImplName, String perlPsgiName, 
            boolean genPython, String pythonClientName, boolean genPythonServer,
            String pythonServerName, String pythonImplName, boolean enableRetries, 
            boolean newStyle, IncludeProvider ip, FileSaver output) throws Exception {
        if (ip == null)
            ip = new FileIncludeProvider(new File("."));
        List<KbService> srvs = KidlParser.parseSpec(KidlParser.parseSpecInt(specReader, null, ip));
        generate(srvs, defaultUrl, genJs, jsClientName, genPerl, perlClientName, genPerlServer, 
                perlServerName, perlImplName, perlPsgiName, genPython, pythonClientName, 
                genPythonServer, pythonServerName, pythonImplName, false, null, false, null, null, 
                enableRetries, newStyle, ip, output, null, null, false);
    }
    
    public static boolean genPerlServer(boolean genPerlServer, 
            String perlServerName, String perlImplName, String perlPsgiName) {
        return genPerlServer || perlServerName != null || perlImplName != null || perlPsgiName != null;
    }
    
    public static boolean genPythonServer(boolean genPythonServer,
            String pythonServerName, String pythonImplName) {
        return genPythonServer || pythonServerName != null || pythonImplName != null;
    }

    public static boolean genRServer(boolean genRServer,
            String rServerName, String rImplName) {
        return genRServer || rServerName != null || rImplName != null;
    }

    @SuppressWarnings("unchecked")
    public static void generate(List<KbService> srvs, String defaultUrl, 
            boolean genJs, String jsClientName,
            boolean genPerl, String perlClientName, boolean genPerlServer, 
            String perlServerName, String perlImplName, String perlPsgiName, 
            boolean genPython, String pythonClientName, boolean genPythonServer,
            String pythonServerName, String pythonImplName, boolean genR, 
            String rClientName, boolean genRServer, String rServerName, String rImplName, 
            boolean enableRetries, boolean newStyle, IncludeProvider ip, FileSaver output,
            FileSaver perlMakefile, FileSaver pyMakefile, boolean asyncByDefault) throws Exception {
        KbService service = srvs.get(0);
        if (genJs && jsClientName == null)
            jsClientName = service.getName() + "Client";
        genPerlServer = genPerlServer(genPerlServer, perlServerName, perlImplName, perlPsgiName);
        if (genPerlServer) {
            genPerl = true;
            if (perlServerName == null)
                perlServerName = service.getName() + "Server";
            if (perlPsgiName == null) {
            	perlPsgiName = service.getName() + ".psgi";
            }
        }
        if (genPerl && perlClientName == null)
            perlClientName = service.getName() + "Client";
        genPythonServer = genPythonServer(genPythonServer, pythonServerName, pythonImplName);
        if (genPythonServer) {
            genPython = true;
            if (pythonServerName == null)
                pythonServerName = service.getName() + "Server";
        }
        if (genPython && pythonClientName == null)
            pythonClientName = service.getName() + "Client";
        genRServer = genPythonServer(genRServer, rServerName, rImplName);
        if (genRServer) {
            genR = true;
            if (rServerName == null)
                rServerName = service.getName() + "Server";
        }
        if (genR && rClientName == null)
            rClientName = service.getName() + "Client";
        Map<String, Object> context = service.forTemplates(perlImplName, pythonImplName);
        if (defaultUrl != null)
            context.put("default_service_url", defaultUrl);
        context.put("client_package_name", perlClientName);
        context.put("server_package_name", perlServerName);
        if (enableRetries)
            context.put("enable_client_retry", true);
        context.put("empty_escaper", "");  // ${empty_escaper}
        context.put("display", new StringUtils());
        if (jsClientName != null) {
            Writer jsClient = output.openWriter(jsClientName + ".js");
            TemplateFormatter.formatTemplate("javascript_client", context, newStyle, jsClient);
            jsClient.close();
        }
        Map<String, Object> perlMakefileContext = new LinkedHashMap<String, Object>(context);
        Map<String, Object> pyMakefileContext = new LinkedHashMap<String, Object>(context);
        if (perlClientName != null) {
            String perlClientPath = fixPath(perlClientName, "::") + ".pm";
            Writer perlClient = output.openWriter(perlClientPath);
            TemplateFormatter.formatTemplate("perl_client", context, newStyle, perlClient);
            perlClient.close();
            perlMakefileContext.put("client_package_name", perlClientName);
            perlMakefileContext.put("client_file", perlClientPath);
        }
        if (pythonClientName != null) {
            String pythonClientPath = fixPath(pythonClientName, ".") + ".py";
            initPyhtonPackages(pythonClientPath, output);
            Writer pythonClient = output.openWriter(pythonClientPath);
            TemplateFormatter.formatTemplate("python_client", context, newStyle, pythonClient);
            pythonClient.close();
            pyMakefileContext.put("client_package_name", pythonClientName);
            pyMakefileContext.put("client_file", pythonClientPath);
        }
        if (rClientName != null) {
            String rClientPath = rClientName + ".r";
            Writer rClient = output.openWriter(rClientPath);
            TemplateFormatter.formatTemplate("r_client", context, newStyle, rClient);
            rClient.close();
        }
        //////////////////////////////////////// Servers /////////////////////////////////////////
        if (asyncByDefault) {
            // Make all methods async and mark methods being async already as couldn't be sync
            context.put("any_async", true);
            List<Map<String, Object>> modules = (List<Map<String, Object>>)context.get("modules");
            for (int modulePos = 0; modulePos < modules.size(); modulePos++) {
                Map<String, Object> module = modules.get(modulePos);
                module.put("any_async", true);
                List<Map<String, Object>> methods = (List<Map<String, Object>>)module.get("methods");
                if (methods == null)
                    continue;
                for (int methodPos = 0; methodPos < methods.size(); methodPos++) {
                    Map<String, Object> method = methods.get(methodPos);
                    Boolean async = (Boolean)method.get("async");
                    method.put("async", true);
                    if (async == null || !async)
                        method.put("could_be_sync", true);
                }
            }
        }
        if (perlServerName != null) {
            String perlServerPath = fixPath(perlServerName, "::") + ".pm";
            Writer perlServer = output.openWriter(perlServerPath);
            TemplateFormatter.formatTemplate("perl_server", context, newStyle, perlServer);
            perlServer.close();
            perlMakefileContext.put("server_package_name", perlServerName);
            perlMakefileContext.put("server_file", perlServerPath);
        }
        if (pythonServerName != null) {
            String pythonServerPath = fixPath(pythonServerName, ".") + ".py";
            initPyhtonPackages(pythonServerPath, output);
            Writer pythonServer = output.openWriter(pythonServerPath);
            TemplateFormatter.formatTemplate("python_server", context, newStyle, pythonServer);
            pythonServer.close();
            pyMakefileContext.put("server_package_name", pythonServerName);
            pyMakefileContext.put("server_file", pythonServerPath);
        }
        if (rServerName != null) {
            String rServerPath = rServerName + ".r";
            Writer rServer = output.openWriter(rServerPath);
            TemplateFormatter.formatTemplate("r_server", context, newStyle, rServer);
            rServer.close();
        }
        if (genPerlServer || genPythonServer || genRServer) {
            List<Map<String, Object>> modules = (List<Map<String, Object>>)context.get("modules");
            for (int modulePos = 0; modulePos < modules.size(); modulePos++) {
                Map<String, Object> module = new LinkedHashMap<String, Object>(modules.get(modulePos));
                perlMakefileContext.put("module", module);
                pyMakefileContext.put("module", module);
                List<Map<String, Object>> methods = (List<Map<String, Object>>)module.get("methods");
                List<String> methodNames = new ArrayList<String>();
                for (Map<String, Object> method : methods)
                    methodNames.add(method.get("name").toString());
                String perlImplPath = null;
                if (genPerlServer) {
                    String perlModuleImplName = (String)module.get("impl_package_name");
                    perlImplPath = fixPath(perlModuleImplName, "::") + ".pm";
                    Map<String, String> prevCode = PrevCodeParser.parsePrevCode(
                            output.getAsFileOrNull(perlImplPath), "#", methodNames, false);
                    module.put("module_header", prevCode.get(PrevCodeParser.HEADER));
                    module.put("module_constructor", prevCode.get(PrevCodeParser.CONSTRUCTOR));
                    for (Map<String, Object> method : methods) {
                        String code = prevCode.get(PrevCodeParser.METHOD + method.get("name"));
                        method.put("user_code", code == null ? "" : code);
                    }
                    perlMakefileContext.put("impl_package_name", perlModuleImplName);
                    perlMakefileContext.put("impl_file", perlImplPath);
                }
                String pythonImplPath = null;
                if (genPythonServer) {
                    String pythonModuleImplName = (String)module.get("pymodule");
                    pythonImplPath = fixPath(pythonModuleImplName, ".") + ".py";
                    Map<String, String> prevCode = PrevCodeParser.parsePrevCode(
                            output.getAsFileOrNull(pythonImplPath), "#", methodNames, true);
                    module.put("py_module_header", prevCode.get(PrevCodeParser.HEADER));
                    module.put("py_module_class_header", prevCode.get(PrevCodeParser.CLSHEADER));
                    module.put("py_module_constructor", prevCode.get(PrevCodeParser.CONSTRUCTOR));
                    for (Map<String, Object> method : methods) {
                        String code = prevCode.get(PrevCodeParser.METHOD + method.get("name"));
                        method.put("py_user_code", code == null ? "" : code);
                    }
                    pyMakefileContext.put("impl_package_name", pythonModuleImplName);
                    pyMakefileContext.put("impl_file", pythonImplPath);
                }
                String rImplPath = null;
                if (genRServer) {
                    rImplPath = rImplName + ".r";
                    Map<String, String> prevCode = PrevCodeParser.parsePrevCode(
                            output.getAsFileOrNull(rImplPath), "#", methodNames, false);
                    module.put("r_module_header", prevCode.get(PrevCodeParser.HEADER));
                    module.put("r_module_constructor", prevCode.get(PrevCodeParser.CONSTRUCTOR));
                    for (Map<String, Object> method : methods) {
                        String code = prevCode.get(PrevCodeParser.METHOD + method.get("name"));
                        method.put("r_user_code", code == null ? "" : code);
                    }
                }
                Map<String, Object> moduleContext = new LinkedHashMap<String, Object>();
                moduleContext.put("module", module);
                moduleContext.put("server_package_name", perlServerName);
                moduleContext.put("empty_escaper", "");  // ${empty_escaper}
                moduleContext.put("display", new StringUtils());
                if (genPerlServer) {
                    Writer perlImpl = output.openWriter(perlImplPath);
                    TemplateFormatter.formatTemplate("perl_impl", moduleContext, newStyle, perlImpl);
                    perlImpl.close();
                }
                if (genPythonServer) {
                    Writer pythonImpl = output.openWriter(pythonImplPath);
                    TemplateFormatter.formatTemplate("python_impl", moduleContext, newStyle, pythonImpl);
                    pythonImpl.close();
                }
                if (genRServer) {
                    Writer rImpl = output.openWriter(rImplPath);
                    TemplateFormatter.formatTemplate("r_impl", moduleContext, newStyle, rImpl);
                    rImpl.close();
                }
            }
        }
        if (perlPsgiName != null) {
            Writer perlPsgi = output.openWriter(perlPsgiName);
            TemplateFormatter.formatTemplate("perl_psgi", context, newStyle, perlPsgi);
            perlPsgi.close();
            perlMakefileContext.put("psgi_file", perlPsgiName);
        }
        if (newStyle && perlMakefile != null) {
            Writer perlMakefileWr = perlMakefile.openWriter(".");
            TemplateFormatter.formatTemplate("perl_makefile", perlMakefileContext, true, perlMakefileWr);
            perlMakefileWr.close();
        }
        if (newStyle && pyMakefile != null) {
            Writer pyMakefileWr = pyMakefile.openWriter(".");
            TemplateFormatter.formatTemplate("python_makefile", pyMakefileContext, true, pyMakefileWr);
            pyMakefileWr.close();
        }
    }
    
    private static void initPyhtonPackages(String relativePyPath, FileSaver output) throws Exception {
        String path = relativePyPath;
        while (true) {
            int pos = path.lastIndexOf("/");
            if (pos < 0)
                break;
            path = path.substring(0, pos);
            if (path.isEmpty())
                break;
            String initPath = path + "/__init__.py";
            File prevFile = output.getAsFileOrNull(initPath);
            if (prevFile == null || !prevFile.exists())
                output.openWriter(initPath).close();
        }
    }

    private static String fixPath(String path, String div) {
        return path.replace(div, "/");
    }
}
