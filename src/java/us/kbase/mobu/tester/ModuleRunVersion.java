package us.kbase.mobu.tester;

import java.net.URL;
import java.util.Set;

import us.kbase.common.utils.ModuleMethod;


/** The version of a KBase SDK method run during an NJS Execution Engine job.
 * @author gaprice@lbl.gov
 *
 */
public class ModuleRunVersion {
  //TODO NJS_SDK move to shared repo

    final private Set<String> RELEASE_TAGS =
            JobRunnerConstants.RELEASE_TAGS;

    //TODO unit tests

    final private URL gitURL;
    final private ModuleMethod modmeth;
    final private String gitHash;
    final private String version;
    final private String release;

    /** Create a version specification for a method to be run.
     * @param gitURL the Git URL of the module
     * @param modmeth the module / method
     * @param method the name of the method
     * @param gitHash the Git hash of the module commit
     * @param version the version of the module
     * @param release the release tag for the module
     */
    public ModuleRunVersion(
            final URL gitURL,
            final ModuleMethod modmeth,
            final String gitHash,
            final String version,
            final String release) {
        if (gitURL == null) {
            throw new NullPointerException("Git URL may not be null");
        }
        if (release != null && !RELEASE_TAGS.contains(release)) {
            throw new IllegalArgumentException(
                    "Invalid release value: " + release);
        }
        if (modmeth == null) {
            throw new NullPointerException("mod meth may not be null");
        }
        notNullOrEmpty(gitHash);
        notNullOrEmpty(version);
        this.gitURL = gitURL;
        this.modmeth = modmeth;
        this.gitHash = gitHash;
        this.version = version;
        this.release = release;
    }
    
    private void notNullOrEmpty(final String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException(
                    "No arguments may be null or the empty string");
        }
    }

    /**
     * @return the gitURL
     */
    public URL getGitURL() {
        return gitURL;
    }

    /**
     * @return the gitHash
     */
    public String getGitHash() {
        return gitHash;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the release
     */
    public String getRelease() {
        return release;
    }

    /**
     * @return the module and method
     */
    public ModuleMethod getModuleMethod() {
        return modmeth;
    }

    /** Returns the version concatenated with the release as version-release.
     * If no release tag is provided, returns the version.
     * @return the version with release tag.
     */
    public String getVersionAndRelease() {
        if (release != null) {
            return version + "-" + release;
        }
        return version;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ModuleRunVersion [gitURL=");
        builder.append(gitURL);
        builder.append(", modmeth=");
        builder.append(modmeth);
        builder.append(", gitHash=");
        builder.append(gitHash);
        builder.append(", version=");
        builder.append(version);
        builder.append(", release=");
        builder.append(release);
        builder.append("]");
        return builder.toString();
    }
    
}
