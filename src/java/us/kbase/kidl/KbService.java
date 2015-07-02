package us.kbase.kidl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class represents group of modules with the same service name 
 * (first optional part of module name before colon). If module
 * name defined by one keyword without colon then service name
 * is supposed to be equal to module name.
 */
public class KbService {
	private String name;
	private List<KbModule> modules;
	
	public KbService(String name) {
		this.name = name;
	}
		
	public void loadFromList(List<?> data) throws KidlParseException {
		List<KbModule> modules = new ArrayList<KbModule>();
		for (Object item : data) {
			KbModule mod = new KbModule();
			mod.loadFromList((List<?>)item);
			modules.add(mod);
		}
		this.modules = Collections.unmodifiableList(modules);
	}
	
	public static List<KbService> loadFromMap(Map<?,?> data) throws KidlParseException {
		List<KbService> ret = new ArrayList<KbService>();
		for (Map.Entry<?,?> entry : data.entrySet()) {
			KbService srv = new KbService("" + entry.getKey());
			srv.loadFromList((List<?>)entry.getValue());
			ret.add(srv);
		}
		return ret;
	}

	public String getName() {
		return name;
	}
	
	public List<KbModule> getModules() {
		return modules;
	}
	
	
    public Map<String, Object> forTemplates(String perlImplName, String pythonImplName) {
        Map<String, Object> ret = new LinkedHashMap<String, Object>();
        List<Map<String, Object>> modules = new ArrayList<Map<String, Object>>();
        boolean psbl = false;
        boolean only = true;
        int funcCount = 0;
        boolean anyAsync = false;
        for (KbModule m : getModules()) {
            Map<String, Object> map = m.forTemplates();
            modules.add(map);
            if (map.get("any_async") != null && map.get("any_async").toString().equals("true"))
                anyAsync = true;
            for (KbModuleComp mc : m.getModuleComponents())
                if (mc instanceof KbFuncdef) {
                    KbFuncdef func = (KbFuncdef)mc;
                    funcCount++;
                    boolean req = func.isAuthenticationRequired();
                    boolean opt = func.isAuthenticationOptional();
                    psbl |= req || opt;
                    only &= req;
                }
        }
        only &= funcCount > 0;
        for (Map<String, Object> module : modules) {
            String moduleName = (String)module.get("module_name");
            module.put("impl_package_name", perlImplName == null ? (moduleName + "Impl") : perlImplName);
            module.put("pymodule", pythonImplName == null ? (moduleName + "Impl") : pythonImplName);
        }
        ret.put("modules", modules);
        if (psbl)
            ret.put("authenticated", true);
        if (only)
            ret.put("authenticated_only", true);
        ret.put("service_name", getName());
        if (anyAsync)
            ret.put("any_async", anyAsync);
        return ret;
    }
}
