package us.kbase.mobu.tester;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import us.kbase.auth.AuthToken;
import us.kbase.auth.TokenFormatException;
import us.kbase.common.executionengine.CallbackServer;
import us.kbase.common.executionengine.ModuleMethod;
import us.kbase.common.executionengine.ModuleRunVersion;
import us.kbase.common.executionengine.SubsequentCallRunner;
import us.kbase.common.executionengine.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.UObject;
import us.kbase.workspace.ProvenanceAction;

public class SDKCallbackServer extends CallbackServer {

    private static final long serialVersionUID = 1L;

    public SDKCallbackServer(
            final AuthToken token,
            final CallbackServerConfig config,
            final ModuleRunVersion runver,
            final List<UObject> methodParameters,
            final List<String> inputWorkspaceObjects) {
        super(token, config, runver, methodParameters, inputWorkspaceObjects);
    }

    @Override
    protected SubsequentCallRunner createJobRunner(
            final AuthToken token,
            final CallbackServerConfig config,
            final UUID jobId,
            final ModuleMethod modmeth,
            final String serviceVer)
            throws IOException, JsonClientException, TokenFormatException {
        return new SDKSubsequentCallRunner(token, config,
                jobId, modmeth, serviceVer);
    }

    @JsonServerMethod(rpc = "CallbackServer.set_provenance")
    public List<ProvenanceAction> getProvenance(ProvenanceAction pa)
            throws IOException, JsonClientException {
        if (pa == null) {
            throw new NullPointerException("provenance cannot be null");
        }
        synchronized (this) {
            prov = pa;
        }
        return new LinkedList<ProvenanceAction>(Arrays.asList(prov));
    }
    
}
