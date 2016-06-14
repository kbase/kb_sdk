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
    
    public static final String CHECK_METHOD = "_check_job";
    public static final String SUBMIT_SUFFIX = "_submit";
    // methods cannot start with an underscore in the type compiler
    public static final String METH_SPEC_PREFIX = "_";
    final private String module;
    final private String method;
    final private boolean submit;
    final private boolean check;
    
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
        StringUtils.checkString(modMeth[0], "Module name");
        module = modMeth[0];
        final String method = modMeth[1];
        if (method.startsWith(METH_SPEC_PREFIX)) {
            if (method.equals(CHECK_METHOD)) {
                check = true;
                submit = false;
                this.method = null;
                return; // don't check the method name
            } else if (method.endsWith(SUBMIT_SUFFIX)) {
                this.method = method.substring(METH_SPEC_PREFIX.length(),
                        method.lastIndexOf(SUBMIT_SUFFIX));
                submit = true;
                check = false;
            } else {
                throw new IllegalArgumentException("Illegal method name: " +
                        method);
            }
        } else {
            this.method = method;
            submit = false;
            check = false;
        }
        StringUtils.checkString(this.method, "Method name");
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
    
    /** Returns the method name stripped of modifiers (e.g. _ and _submit) or
     * null if the method is a check method.
     * @return the method name.
     */
    public String getMethod() {
        return method;
    }
    
    /** Check if the method call should be asynchronous (e.g. the method was
     * appended with _submit).
     * @return true if the method should be asynchronous.
     */
    public boolean isSubmit() {
        return submit;
    }
    
    /** Check if the method call should check on the state of the asynchronous
     * job.
     * @return true if the method should check the asynchronous state.
     */
    public boolean isCheck() {
        return check;
    }
    
    /** Check if this is a standard synchronous method.
     * @return true if this is a standard synchronous method.
     */
    public boolean isStandard() {
        return !submit && !check;
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
        builder.append(submit);
        builder.append(", checkrun=");
        builder.append(check);
        builder.append("]");
        return builder.toString();
    }
}
