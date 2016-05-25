package us.kbase.common.executionengine;

public interface LineLogger {
    
    //TODO NJS_SDK move to shared repo
    public void logNextLine(String line, boolean isError);
}
