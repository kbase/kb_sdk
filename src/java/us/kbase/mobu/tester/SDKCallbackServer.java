package us.kbase.mobu.tester;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import us.kbase.auth.AuthToken;
import us.kbase.auth.TokenFormatException;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.UObject;
import us.kbase.common.utils.ModuleMethod;
import us.kbase.mobu.tester.CallbackServerConfigBuilder.CallbackServerConfig;

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

}
