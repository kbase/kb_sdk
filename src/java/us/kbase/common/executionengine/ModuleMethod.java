package us.kbase.common.executionengine;

import us.kbase.common.utils.StringUtils;


/** A specifier for a module and a method. If the specifier string ends with
 * _async or _check the method is treated as asynchronous.
 * @author gaprice@lbl.gov
 *
 */
public class ModuleMethod {
    //TODO NJS_SDK move to common repo
    //TODO unit tests
    
    public static final String CHECK_SUFFIX = "_check";
    public static final String ASYNC_SUFFIX = "_async";
    final private String module;
    final private String method;
    final private boolean asyncrun;
    final private boolean checkrun;
    
    /** Create module / method specification.
     * @param moduleDotMethod the string specifying the module, method, and
     * whether the method is asynchronous or is checking the status of an
     * asynchronous job.
     */
    public ModuleMethod(final String moduleDotMethod) {
        final String[] modMeth = moduleDotMethod.split("\\.");
        if (modMeth.length != 2) {
            throw new IllegalArgumentException("Illegal method name: " +
                    moduleDotMethod);
        }
        StringUtils.checkString(modMeth[0], "Module");
        module = modMeth[0];
        final String method = modMeth[1];
        // TODO this seems fragile, consider a different implementation
        if (method.endsWith(ASYNC_SUFFIX)) {
            this.method = method.replace(ASYNC_SUFFIX, "");
            asyncrun = true;
            checkrun = false;
        } else if (method.endsWith(CHECK_SUFFIX)) {
            this.method = method.replace(CHECK_SUFFIX, "");
            asyncrun = false;
            checkrun = true;
        } else {
            this.method = method;
            asyncrun = false;
            checkrun = false;
        }
        StringUtils.checkString("Method", this.method);
    }
    
    /** Returns the full method name, including the module.
     * @return the full method name, including the module.
     */
    public String getModuleDotMethod() {
        return module + "." + method;
    }
    
    /** Returns the module name.
     * @return the module name.
     */
    public String getModule() {
        return module;
    }
    
    /** Returns the method name stripped of modifiers (e.g. _async and _check).
     * @return the method name.
     */
    public String getMethod() {
        return method;
    }
    
    /** Check if the method call should be asynchronous (e.g. the method was
     * appended with _async).
     * @return true if the method should be asynchronous.
     */
    public boolean isAsync() {
        return asyncrun;
    }
    
    /** Check if the method call should check on the state of the asynchronous
     * job (e.g. the method was appended with _check).
     * @return true if the method should check the asynchronous state.
     */
    public boolean isCheck() {
        return checkrun;
    }
    
    /** Check if this is a standard synchronous method.
     * @return true if this is a standard synchronous method.
     */
    public boolean isStandard() {
        return !asyncrun && !checkrun;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ModuleMethod [module=");
        builder.append(module);
        builder.append(", method=");
        builder.append(method);
        builder.append(", asyncrun=");
        builder.append(asyncrun);
        builder.append(", checkrun=");
        builder.append(checkrun);
        builder.append("]");
        return builder.toString();
    }
}
