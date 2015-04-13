package us.kbase.scripts;

import java.io.File;
import java.io.FileReader;
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
import us.kbase.templates.TemplateFormatter;

public class TemplateBasedGenerator {

    public static void generate(File specFile, String defaultUrl, 
            boolean genJs, String jsClientName,
            boolean genPerl, String perlClientName, boolean genPerlServer, 
            String perlServerName, String perlImplName, String perlPsgiName, 
            boolean genPython, String pythonClientName, boolean genPythonServer,
            String pythonServerName, String pythonImplName,
            boolean enableRetries, boolean newStyle, File outDir) throws Exception {
        IncludeProvider ip = new FileIncludeProvider(specFile.getCanonicalFile().getParentFile());
        generate(new FileReader(specFile), defaultUrl, genJs, jsClientName, genPerl, 
                perlClientName, genPerlServer, perlServerName, perlImplName, perlPsgiName, 
                genPython, pythonClientName, genPythonServer, pythonServerName, pythonImplName, 
                enableRetries, newStyle, ip, new DiskFileSaver(outDir));
    }

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
                genPythonServer, pythonServerName, pythonImplName, enableRetries, newStyle, ip, output);
    }
    
    @SuppressWarnings("unchecked")
    public static void generate(List<KbService> srvs, String defaultUrl, 
            boolean genJs, String jsClientName,
            boolean genPerl, String perlClientName, boolean genPerlServer, 
            String perlServerName, String perlImplName, String perlPsgiName, 
            boolean genPython, String pythonClientName, boolean genPythonServer,
            String pythonServerName, String pythonImplName, boolean enableRetries, 
            boolean newStyle, IncludeProvider ip, FileSaver output) throws Exception {
        KbService service = srvs.get(0);
        if (genJs && jsClientName == null)
            jsClientName = service.getName() + "Client";
        if (perlServerName != null || perlImplName != null || perlPsgiName != null)
            genPerlServer = true;
        if (genPerlServer) {
            genPerl = true;
            if (perlServerName == null)
                perlServerName = service.getName() + "Server";
        }
        if (genPerl && perlClientName == null)
            perlClientName = service.getName() + "Client";
        if (pythonServerName != null || pythonImplName != null)
            genPythonServer = true;
        if (genPythonServer) {
            genPython = true;
            if (pythonServerName == null)
                pythonServerName = service.getName() + "Server";
        }
        if (genPython && pythonClientName == null)
            pythonClientName = service.getName() + "Client";
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
        if (perlClientName != null) {
            Writer perlClient = output.openWriter(fixPath(perlClientName, "::") + ".pm");
            TemplateFormatter.formatTemplate("perl_client", context, newStyle, perlClient);
            perlClient.close();
        }
        if (pythonClientName != null) {
            Writer pythonClient = output.openWriter(fixPath(pythonClientName, ".") + ".py");
            TemplateFormatter.formatTemplate("python_client", context, newStyle, pythonClient);
            pythonClient.close();
        }
        if (perlServerName != null) {
            Writer perlServer = output.openWriter(fixPath(perlServerName, "::") + ".pm");
            TemplateFormatter.formatTemplate("perl_server", context, newStyle, perlServer);
            perlServer.close();
        }
        if (pythonServerName != null) {
            Writer pythonServer = output.openWriter(fixPath(pythonServerName, ".") + ".py");
            TemplateFormatter.formatTemplate("python_server", context, newStyle, pythonServer);
            pythonServer.close();
        }
        if (genPerlServer || genPythonServer) {
            List<Map<String, Object>> modules = (List<Map<String, Object>>)context.get("modules");
            for (int modulePos = 0; modulePos < modules.size(); modulePos++) {
                Map<String, Object> module = new LinkedHashMap<String, Object>(modules.get(modulePos));
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
            }
        }
        if (perlPsgiName != null) {
            Writer perlPsgi = output.openWriter(perlPsgiName);
            TemplateFormatter.formatTemplate("perl_psgi", context, newStyle, perlPsgi);
            perlPsgi.close();
        }
    }

    private static String fixPath(String path, String div) {
        return path.replace(div, "/");
    }
}
