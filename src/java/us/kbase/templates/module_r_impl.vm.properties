#BEGIN_HEADER
library(jsonlite)
#END_HEADER

methods <- list()

#BEGIN_CONSTRUCTOR
source("./lib/Workspace/WorkspaceClient.r")
#END_CONSTRUCTOR

methods[["RTest4.count_contigs"]] <- function(workspace_name, contigset_id, context) {
    #BEGIN count_contigs
    token <- context[['token']]
    provenance <- context[['provenance']]
    ws_url <- context[['config']][['workspace-url']]
    ws_client <- WorkspaceClient(ws_url, token)
    ref <- unbox(paste(workspace_name,"/",contigset_id, sep=""))
    object_identity <- list(ref=unbox(ref))
    object_data <- ws_client$get_objects(list(object_identity))[[1]]
    data <- object_data[['data']]
    contigs <- data[['contigs']]
    contig_count <- unbox(length(contigs))
    return(list(contig_count=contig_count, provenance=provenance))
    #END count_contigs
}
