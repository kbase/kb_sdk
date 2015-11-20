package us.kbase.tools;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.fasterxml.jackson.core.type.TypeReference;

import us.kbase.abstracthandle.AbstractHandleClient;
import us.kbase.abstracthandle.Handle;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.UObject;
import us.kbase.shock.client.BasicShockClient;
import us.kbase.shock.client.ShockNode;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.ProvenanceAction;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.WorkspaceClient;

public class FastX2reads {
    
    public FastX2reads() {
        // Nothing to init.
    }
    
    public void transform(CommandArgs args) throws Exception {
        String token = args.token;
        String wsUrl = args.wsUrl;
        String shockUrl = args.shockUrl;
        String handleUrl = args.handleUrl;
        File inputFile = args.inputFile;
        File inputFile2 = args.inputFile2;
        Boolean pairedEnd = args.pairedEnd;
        String fileType = args.fileType; 
        Integer readCount = args.readCount; 
        Integer readSize = args.readSize;
        String sequencingTech = args.sequencingTech; 
        String sourceProjectId = args.sourceProjectId; 
        String source = args.source; 
        String sourceId = args.sourceId;
        String strainGenus = args.strainGenus;
        String strainSpecies = args.strainSpecies; 
        String strain = args.strain;
        String outputWsName = args.outputWsName;
        String outputObjName = args.outputObjName;
        File provFile = args.provFile;
        //String provService;
        //String provServiceVer; 
        //String provMethod;
        //String provMethodParams;
        if (token == null || token.trim().isEmpty())
            token = System.getenv("KB_AUTH_TOKEN");
        if (token == null || token.trim().isEmpty())
            throw new IllegalStateException("Token is not defined in parameters nor in 'KB_AUTH_TOKEN' environment variable");
        if (handleUrl == null)
            handleUrl = shockUrl.replace("shock-api", "handle_service");
        if (pairedEnd == null) {
            pairedEnd = inputFile2 != null;
        } else if (inputFile2 != null && !pairedEnd) {
            throw new IllegalStateException("Second input file is not allowed in single-end mode");
        }
        AuthToken auth = new AuthToken(token);
        BasicShockClient shockCl = new BasicShockClient(new URL(shockUrl), auth);
        AbstractHandleClient handleCl = new AbstractHandleClient(new URL(handleUrl), auth);
        Map<String, Object> obj = new LinkedHashMap<String,Object>();
        Map<String, Object> lib = new LinkedHashMap<String,Object>();
        obj.put(pairedEnd ? "lib1" : "lib", lib);
        prepareLib(inputFile, fileType, handleCl, shockUrl, shockCl, lib);
        if (inputFile2 != null) {
            Map<String, Object> lib2 = new LinkedHashMap<String,Object>();
            obj.put("lib2", lib2);
            prepareLib(inputFile2, fileType, handleCl, shockUrl, shockCl, lib2);
        }
        if (pairedEnd)
            obj.put("interleaved", inputFile2 == null ? 1 : 0);
        if (readCount != null)
            obj.put("read_count", readCount);
        if (readSize != null)
            obj.put("read_size", readSize);
        obj.put("sequencing_tech", sequencingTech == null ? "unknown" : sequencingTech);
        if (source != null || sourceProjectId != null || sourceId != null) {
            Map<String, Object> sourceObj = new LinkedHashMap<String,Object>();
            obj.put("source", sourceObj);
            if (source != null)
                sourceObj.put("source", source);
            if (sourceProjectId != null)
                sourceObj.put("project_id", sourceProjectId);
            if (sourceId != null) 
                sourceObj.put("source_id", sourceId);
        }
        if (strainGenus != null || strainSpecies != null || strain != null) {
            Map<String, Object> strainObj = new LinkedHashMap<String,Object>();
            obj.put("strain", strainObj);
            strainObj.put("genus", strainGenus == null ? "unknown" : strainGenus);
            strainObj.put("species", strainSpecies == null ? "unknown" : strainSpecies);
            strainObj.put("strain", strain == null ? "unknown" : strain);
        }
        String objType = pairedEnd ? "KBaseFile.PairedEndLibrary" : "KBaseFile.SingleEndLibrary";
        WorkspaceClient wsCl = new WorkspaceClient(new URL(wsUrl), auth);
        ObjectSaveData osd = new ObjectSaveData().withData(new UObject(obj)).withType(objType)
                .withName(outputObjName);
        if (provFile != null) {
            List<ProvenanceAction> provenance = UObject.getMapper().readValue(provFile, 
                    new TypeReference<List<ProvenanceAction>>() {});
            osd.withProvenance(provenance);
        }
        wsCl.saveObjects(new SaveObjectsParams().withWorkspace(outputWsName).withObjects(
                Arrays.asList(osd)));
    }
    
    private static void prepareLib(File inputFile, String fileType, AbstractHandleClient handleCl,
            String shockUrl, BasicShockClient shockCl, Map<String, Object> lib) throws Exception {
        if (fileType == null) {
            String fileName = inputFile.getName().toLowerCase();
            if (fileName.endsWith(".fastq")) {
                fileType = "fastq";
            } else if (fileName.endsWith(".fastq.gz")) {
                fileType = "fastq.gz";
            } else if (fileName.endsWith(".fasta")) {
                fileType = "fasta";
            } else if (fileName.endsWith(".fasta.gz")) {
                fileType = "fasta.gz";
            } else {
                boolean gz = false;
                if (fileName.endsWith(".gz")) {
                    gz = true;
                    fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                }
                fileType = fileName.indexOf('.') > 0 ? fileName.substring(fileName.lastIndexOf('.') + 1) : "unknown";
                if (gz)
                    fileType += ".gz";
            }
        }
        lib.put("encoding", "UTF8");
        lib.put("size", inputFile.length());
        lib.put("type", fileType);
        Map<String, Object> file = new LinkedHashMap<String,Object>();
        lib.put("file", file);
        file.put("type", "shock");
        file.put("url", shockUrl);
        FileInputStream is = new FileInputStream(inputFile);
        ShockNode node = shockCl.addNode(is, inputFile.getName(), fileType);
        String shockNodeId = node.getId().getId();
        file.put("id", shockNodeId);
        List<Handle> hl = handleCl.idsToHandles(Arrays.asList(shockNodeId));
        String hid;
        if (hl.size() > 0) {
            hid = hl.get(0).getHid();
        } else {
            hid = handleCl.persistHandle(new Handle().withId(shockNodeId).withType("shock").withUrl(shockUrl)
                    .withFileName(node.getFileInformation().getName()).withRemoteMd5(node.getFileInformation().getChecksum("md5")));
        }
        file.put("hid", hid);
    }

    @Parameters(commandDescription = "Transforms fasta/fastq files into Reads object in workspace")
    public static class CommandArgs {
     
        @Parameter(names="--token", description="Optinal token alternative to one defined in KB_AUTH_TOKEN environment variable")
        String token = null;
        
        @Parameter(names="--wsurl", required=true, description="Workspace Service URL")
        String wsUrl = null;

        @Parameter(names="--shockurl", required=true, description="Shock Service URL")
        String shockUrl = null;

        @Parameter(names="--handleurl", description="Handle Service URL (optional, if not set shockurl.replace{shock-api->handle_service} is used)")
        String handleUrl = null;

        @Parameter(names="--inputfile", required=true, description="Input file")
        File inputFile = null;

        @Parameter(names="--inputfile2", description="Second optional input file")
        File inputFile2 = null;

        @Parameter(names="--pairedend", description="Optional flag defining which output type should be used (if not set it's detected based on number of input files)")
        Boolean pairedEnd = null;

        @Parameter(names="--fileType", description="Optional type of input files (like fastq.gz, if not set guessed from file names)")
        String fileType = null;

        @Parameter(names="--readcount", description="Optional count of reads")
        Integer readCount = null; 

        @Parameter(names="--readsize", description="Optional size of read")
        Integer readSize = null;

        @Parameter(names="--sequencingtech", description="Optional sequencing technology (like Illumina, ...)")
        String sequencingTech = null; 

        @Parameter(names="--projectid", description="Optional project_id in source block")
        String sourceProjectId = null; 

        @Parameter(names="--source", description="Optional source in source block")
        String source = null; 

        @Parameter(names="--sourceid", description="Optional source_id in source block")
        String sourceId = null;

        @Parameter(names="--genus", description="Optional genus in strain block")
        String strainGenus = null;

        @Parameter(names="--species", description="Optional species in strain block")
        String strainSpecies = null; 

        @Parameter(names="--strain", description="Optional strain in strain block")
        String strain = null;

        @Parameter(names="--outws", required=true, description="Output workspace name")
        String outputWsName;
        
        @Parameter(names="--outobj", required=true, description="Output object name")
        String outputObjName; 
        
        @Parameter(names="--prov", description="Provenance JSON file with outer array block listing provenance actions")
        File provFile = null;
    }
}
