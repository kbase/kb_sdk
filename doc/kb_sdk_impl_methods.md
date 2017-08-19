# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install SDK with Docker](kb_sdk_dockerized_install.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Specify Module and Method(s)](kb_sdk_edit_module.md)
5. **Implement Method(s)**
6. [Specify User Interface](kb_sdk_make_ui.md)
7. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
8. [Register Module](kb_sdk_register_module.md)
9. [Test in KBase](kb_sdk_test_in_kbase.md)
10. [Complete Module Info](kb_sdk_complete_module_info.md)
11. [Deploy](kb_sdk_deploy.md)

### <A NAME="impl"></A>5. Implement Method(s)

In the lib/\<MyModule> directory, edit the <MyModule>Impl.py (or *Impl.pl or *Server.java) "Implementation" file that 
defines the methods available in the module. The example module is very simple and implemented directly in this file
but it is better practice and more readable to separate implementation logic into multiple files, especially for modules 
containing more than one method. 

The workflow of most modules involves obtaining data from the KBase data stores, operating on that data (possibly with 3rd party 
code or executables), storing resulting data in the data stores, and producing a report of the work accomplished for the user.
This step of the guide will walk through this process and present some of the utility modules that help facilitate this work.

- A. [Install Other KBase Modules](#install)
- B. [Import and Initialise](#import)
- C. [Validating user input](#validate)
- D. [Adding Reference Data to Your Method](#impl-adding-data)
- E. [Interacting with KBase data stores (Workspaces)](#get-save-data)
- F. [Invoking Shell Tool](#impl-shell-tool)
- G. [Building Output Report](#impl-report)

#### <A NAME="install"></A>A. Install Other KBase Modules

If you begin by altering an existing app (as this walkthough demonstrates) you will already have some KBase utility 
modules in your lib directory. To install additional packages run `kb-sdk install <module name>` from the terminal.
Here's an sample of some of the modules that might be helpful for your app:

* [KBaseReport](https://appdev.kbase.us/#catalog/modules/KBaseReport) - Allows the creation of KBase 
reports which can present text, html, and downloadable files to the user as output to your app.
* [GenomeFileUtil](https://appdev.kbase.us/#catalog/modules/ReadsUtils) - Import/Export and Upload/Download 
of Genome data
*[AssemblyUtils](https://appdev.kbase.us/#catalog/modules/ReadsAlignmentUtils) - Provides tooling for interacting 
with sequence assembly data in KBase.
* [ReadsUtils](https://appdev.kbase.us/#catalog/modules/ReadsUtils) - Utilities for validating, uploading, and 
downloading reads files. Includes FASTA and FASTQ validators. 
*[ExpressionUtils](https://appdev.kbase.us/#catalog/modules/ExpressionUtils) - A module to upload, download and 
export RNASeq Expression data obtained by either StringTie or Cufflinks Apps.
*[ReadsAlignmentUtils](https://appdev.kbase.us/#catalog/modules/ReadsAlignmentUtils) - Functions for uploading and 
downloading KBase reads alignment files.
*[DifferentialExpressionUtils](https://appdev.kbase.us/#catalog/modules/DifferentialExpressionUtils) - Module to 
upload/download/export differential expression object and other related processing
*[FeatureSetUtils](https://appdev.kbase.us/#catalog/modules/FeatureSetUtils) - A module to upload, download and 
export Genomic Feature Set data
*[CompoundSetUtils](https://appdev.kbase.us/#catalog/modules/CompoundSetUtils) - Module to upload/download/export 
chemical compound sets
* [DataFileUtil](https://appdev.kbase.us/#catalog/modules/DataFileUtil) - A collection of tools to 
get data directly from the web, from the user's computer (via the staging area) and KBase workspaces.

[\[Back to top\]](#top)

#### <A NAME="import"></A>B. Import and Initialise
In python, you can import these installed modules like any other python package. In the header, two important properties
are defined. The first is the SDK_CALLBACK_URL which is passed to modules invoked by this modules so they can report 
their status and results to the callback server which coordinates module execution. This is demonstrated by 
the instantiation of a AssemblyUtil client in the following example. The other parameter commonly defined in 
the module constructor is the path to the scratch directory. This directory is a common space accessible by not only the 
current module but also every module called by this module. Therefore files from other modules (for example 
AssemblyUtil) will be written to the scratch folder and files to be used by other modules (such as KBaseReport) are 
read from the scratch folder.

```python
import os
from AssemblyUtil.AssemblyUtilClient import AssemblyUtil
from KBaseReport.KBaseReportClient import KBaseReport

class <ModuleName>:
    """Module Docs"""
    def __init__(self, config):
        #BEGIN_CONSTRUCTOR
        self.config = config
        self.scratch = config['scratch']
        self.callback_url = os.environ['SDK_CALLBACK_URL']
        self.dfu = AssemblyUtil(self.callback_url)
        #END_CONSTRUCTOR
        pass
```

[\[Back to top\]](#top)

#### <A NAME="validate"></A>C. Validating user input

While user interfaces for narrative apps are able to able to validate input to your app, it's wise not to rely on 
this functionality because other developers may call your module directly (and incorrectly). The following function
can be placed in the header to your method and called to verify the correct keys are present in an input parameter
object. You also should consider type checking and or deeper input validation, especially for long running apps.
```python
    @staticmethod
    def _check_param(in_params, req_param, opt_param=list()):
        """
        Check if each of the params in the 'req_params' list are in 'in_params' and warn about unexpected params
        """
        for param in req_param:
            if param not in in_params:
                raise ValueError('{} parameter is required'.format(param))
        defined_param = set(req_param+opt_param)
        for param in in_params:
            if param not in defined_param:
                print("WARNING: received unexpected parameter {}".format(param))
```

[\[Back to top\]](#top)

#### <A NAME="impl-adding-data"></A>D. Adding Reference Data To Your Method

Reference data that is modest in size should be added to the github repository in the /data folder. At runtime, this
data will be accessible at `/kb/module/data`. Data sets that exceed GitHub's file size limits (> 100 MB) should be 
added to a shared mount point.  This can be accomplished by contacting kbase administrators at http://kbase.us. 

[\[Back to top\]](#top)

#### <A NAME="get-save-data"></A>E. Interacting with KBase data stores (Workspaces)

Your method may use one or more data objects the user has available in a Narrative (visible in then data panel
on the right) or data objects in public Narratives. Your method can access these objects by reference(preferred) or 
name using the [DataFileUtils](https://narrative.kbase.us/#catalog/modules/DataFileUtil) (DFU) module or a type 
specific module that uses DFU under the hood (such as [AssemblyUtil](https://narrative.kbase.us/#catalog/modules/AssemblyUtil))
to handle input and output of objects/files. While any object can be downloaded or uploaded in JSON form with DFU, 
the specialized modules often are able to write or read data in type-specific formats like FASTA or SBML as the
example module demonstrates:

```
fasta_file = assemblyUtil.get_assembly_as_fasta({'ref': assembly_input_ref})

...

new_assembly = assemblyUtil.save_assembly_from_fasta({'file': {'path': filtered_fasta_file},
                                                      'workspace_name': workspace_name,
                                                      'assembly_name': fasta_file['assembly_name']
                                                      })
```

In order to support open and reproducible science, KBase data objects store a record of the source and methods applied 
to data in an accompanying provenance object. You may find examples of older KBase modules which generate 
[provenance objects](https://ci.kbase.us/services/ws/docs/Workspace.html#typedefWorkspace.ProvenanceAction) directly and
use the workspace service to save objects but this approach has been deprecated in favor of the DFU or type-specific modules
which handle provenance data automatically.

[\[Back to top\]](#top)

#### <A NAME="impl-shell-tool"></A>F. Invoking Shell Tool

Many KBase apps wrap tools with commandline interfaces. The following example from the [kb_quast](https://github.com/kbaseapps/kb_quast) 
repository demonstrates how to do (and document) this well with Python.

```python
    def run_quast_exec(self, outdir, filepaths, labels):
        threads = psutil.cpu_count() * self.THREADS_PER_CORE
        # DO NOT use genemark instead of glimmer, not open source
        # DO NOT use metaQUAST, uses SILVA DB which is not open source
        cmd = ['quast.py', '--threads', str(threads), '-o', outdir, '--labels', ','.join(labels),
               '--glimmer', '--contig-thresholds', '0,1000,10000,100000,1000000'] + filepaths
        self.log('running QUAST with command line ' + str(cmd))
        retcode = subprocess.call(cmd)
        self.log('QUAST return code: ' + str(retcode))
        if retcode:
            # can't actually figure out how to test this. Give quast garbage it skips the file.
            # Give quast a file with a missing sequence it acts completely normally.
            raise ValueError('QUAST reported an error, return code was ' + str(retcode))
        # quast will ignore bad files and keep going, which is a disaster for
        # reproducibility and accuracy if you're not watching the logs like a hawk.
        # for now use this hack to check that all files were processed. Maybe there's a better way.
        files_proc = len(os.listdir(os.path.join(outdir, 'predicted_genes'))) / 2
        files_exp = len(filepaths)
        if files_proc != files_exp:
            err = ('QUAST skipped some files - {} expected, {} processed.'
                   .format(files_exp, files_proc))
            self.log(err)
            raise ValueError(err)
 ```

[\[Back to top\]](#top)

#### <A NAME="impl-report"></A>G. Building Output Report

The KBaseReports module is the preferred way to communicate results to users. The example module contains a minimal use
case where a string is displayed to the user (supplied in the 'text_message' field) along with links to new objects 
created by the app (supplied in the 'objects_created' field). However this module also contains functionality to create 
and display HTML webpages and serve files directly for user download. A more typical and feature rich example can be
found in KBase's implementation of [Ballgown](https://github.com/kbaseapps/kb_ballgown/blob/1b77c87/lib/kb_ballgown/core/ballgown_util.py#L66-L167)

To save space, an annotated copy of the master function is copied below:
```python
def _generate_report(self, params, result_directory, diff_expression_matrix_set_ref):
        """
        _generate_report: generate summary report
        """
        print('creating report')
        # Files to be presented directly to the user are copied to a output directory, zipped and returned as a list of paths
        output_files = self._generate_output_file_list(result_directory)
        
        # A folder for the display web page is created(L74 in ballgown repo linked above), reference data (could also be 
        # embedded images) for the page are copied in(L78), custom html is generated(L81-95), template is updated with 
        # custom html(L98-104), the complete web page is saved to SHOCK which will serve the data(L106) and an object with 
        # that reference is returned(L109)
        output_html_files = self._generate_html_report(result_directory, params, diff_expression_matrix_set_ref)

        report_params = {
              'message': '',
              'workspace_name': params.get('workspace_name'),
              'file_links': output_files,
              'html_links': output_html_files,
              'direct_html_link_index': 0,
              'html_window_height': 333,
              'report_object_name': 'kb_ballgown_report_' + str(uuid.uuid4())}
        
        # Make the client, generate the report
        kbase_report_client = KBaseReport(self.callback_url)
        output = kbase_report_client.create_extended_report(report_params)
        # Return references which will allow inline display of the report in the Narrative
        report_output = {'report_name': output['name'], 'report_ref': output['ref']}

        return report_output
```

[\[Back to top\]](#top)