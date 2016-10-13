package us.kbase.mobu.tester;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class DockerMountPoints {

    private static final String DOCKER_MOUNT_SWITCH = " -v ";

    private final Path dockerRoot;
    private final Path defaultMount;
    
    private final Map<Path, Path> mounts = new HashMap<>();

    public DockerMountPoints(
            final Path dockerRoot,
            final Path defaultMount) {
        if (dockerRoot == null) {
            throw new NullPointerException("dockerRoot");
        }
        if (!dockerRoot.isAbsolute()) {
            throw new IllegalArgumentException(
                    "dockerRoot mount path must be an absolute path");
        }
        if (defaultMount == null) {
            throw new NullPointerException("defaultMount");
        }
        if (defaultMount.isAbsolute()) {
            throw new IllegalArgumentException(
                    "defaultMount cannot be an absolute path");
        }
        this.dockerRoot = dockerRoot.normalize();
        this.defaultMount = dockerRoot.resolve(defaultMount).normalize();
    }

    public void addMount(final String mountSpec) {
        final String[] hostDocker = mountSpec.split(Pattern.quote(":"));
        final Path docker;
        if (hostDocker.length != 2) {
            if (hostDocker.length == 1) {
                docker = defaultMount;
            } else {
                throw new IllegalArgumentException(
                        "Unexpected mount point format: " + mountSpec);
            }
        } else {
            docker = Paths.get(hostDocker[1]);
        }
        addMount(Paths.get(hostDocker[0]), docker);
    }

    public void addMount(final Path host, Path docker) {
        if (!Files.isDirectory(host)) {
            throw new IllegalArgumentException(
                    "Host mount point directory doesn't exist: " + host);
        }
        if (docker.toString().trim().isEmpty()) {
            docker = defaultMount;
        }
        if (!docker.isAbsolute()) {
            docker = dockerRoot.resolve(docker);
        }
        
        mounts.put(host.normalize().toAbsolutePath(), docker);
    }

    public String getDockerCommand() {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<Path, Path> e: mounts.entrySet()) {
            sb.append(DOCKER_MOUNT_SWITCH);
            sb.append(e.getKey());
            sb.append(":");
            sb.append(e.getValue());
        }
        return sb.toString();
    }

}
