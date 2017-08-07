# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install SDK with Docker](kb_sdk_dockerized_install.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Edit Module and Method(s)](kb_sdk_edit_module.md)
5. **Implement Method(s)**
6. [Specify User Interface](kb_sdk_make_ui.md)
7. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
8. [Register Module](kb_sdk_register_module.md)
9. [Test in KBase](kb_sdk_test_in_kbase.md)
10. [Complete Module Info](kb_sdk_complete_module_info.md)
11. [Deploy](kb_sdk_deploy.md)

#### <A NAME="impl"></A>5. Edit Impl file

In the lib/\<MyModule\>/ directory, edit the <MyModule>Impl.py (or *.pl) "Implementation" file that defines the methods 
available in the module.  You can follow this guide for interacting with [KBase Data Types](doc/kb_sdk_data_types.md).  
Basically, the process consists of obtaining data objects from the KBase workspace, and either operating on them 
directly in code or writing them to scratch files that the tool you are wrapping will operate on.  Result data should 
be collected into KBase data objects and stored back in the workspace.

- 5.1. [Install Other KBase Packages](#install)
- 5.2. [Using Data Types](#impl-data-types)
- 5.3. [Logging](#impl-logging)
- 5.4. [Provenance](#impl-provenance)
- 5.5. [Building Output Report](#impl-report)
- 5.6. [Invoking Shell Tool](#impl-shell-tool)
- 5.7. [Adding Data to Your Method](#impl-adding-data)

##### <A NAME="install"></A>5.1. Install Other KBase Packages

If you begin by altering an existing app (as this walkthough demonstrates) you will already have some KBase utility 
modules in your lib directory. To install additional packages run `kb-sdk install <module name>` from the terminal.
Here's an sample of some of the modules that might be helpful for you app:
* [Workspace](https://github.com/kbase/workspace_deluxe/blob/master/workspace.spec) - Contains functions to get data 
from, and store data in the user's workspace
* [DataFileUtil](https://github.com/kbaseapps/DataFileUtil/blob/master/DataFileUtil.spec) - A collection of tools to 
get data directly from the web, from the user's computer (via the staging 
area) and [SHOCK](https://github.com/MG-RAST/Shock)
* [KBaseReport](https://github.com/kbaseapps/KBaseReport/blob/master/KBaseReport.spec) - Allows the creation of KBase 
reports which can present text, html, and downloadable files to the user as output to your app.

In python, you can import these installed modules like any other python package. Generally to access functionality, a
client in initialized with the SDK_CALLBACK_URL. The module constructor is a good place to do this as in the following
example. 

```python
import os
from KBaseReport.KBaseReportClient import KBaseReport
from Workspace.WorkspaceClient import Workspace
from DataFileUtil.DataFileUtilClient import DataFileUtil

class <ModuleName>:
    """Module Docs"""
    def __init__(self, config):
        #BEGIN_CONSTRUCTOR
        self.config = config
        self.scratch = config['scratch']
        self.callback_url = os.environ['SDK_CALLBACK_URL']
        self.ws_url = config['workspace-url']
        self.ws_client = Workspace(self.ws_url)
        self.dfu = DataFileUtil(self.callback_url)
        #END_CONSTRUCTOR
        pass
```
Another important parameter commonly defined in the module constructor is the path to the scratch directory. Each called
KBase module exists within it's own docker container and have distinct file systems but critically share a common
scratch space. Therefore files written by other modules (for example DataFileUtils) or to be passed to other modules
(such as KBaseReport) must be in the scratch folder.

[\[Back to Edit Impl\]](#impl)


##### <A NAME="impl-data-types"></A>5.2. Using Data Types

Data objects are typed and structured in KBase.  You may write code that takes advantage of these structures, or extract the data from them to create files that the external tool you are wrapping requires (e.g. FASTA).  Please take advantage of the code snippets in the [KBase Data Types](kb_sdk_data_types.md), you can also look at the [Examples](#examples) for syntax and style guidance.

Please see:

    https://github.com/kbase/kb_sdk/blob/master/doc/kb_sdk_data_types.md

[\[Back to Edit Impl\]](#impl)


##### <A NAME="impl-logging"></A>5.3. Logging

Logging where you are is key to tracking progress and debugging.  Our recommended style is to log to a "console" list.  Here is some example code for accomplishing this.

```python
    from pprint import pprint, pformat

    # target is a list for collecting log messages
    def log(self, target, message):
        # we should do something better here...
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()

    def run_<MyMethod>(self, ctx, params):
        console = []
        self.log(console,'Running run_<MyMethod> with params=')
        self.log(console, pformat(params))
```    

[\[Back to Edit Impl\]](#impl)

##### <A NAME="impl-provenance"></A>5.4. Provenance

Data objects in KBase contain provenance (historical information of their creation and objects from which they are derived).  When you create new objects, you must carry forward and add provenance information to them.  Additionally, Report objects should receive that provenance data (see below).  Examples of adding provenance to objects can be found in the [KBase Data Types](docs/kb_sdk_data_types.md).

```python
        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference
        provenance[0]['input_ws_objects']=[]
        provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['read_library_name'])
```

[\[Back to Edit Impl\]](#impl)

##### <A NAME="impl-report"></A>5.5. Building Output Report

```python
        # create a Report
        report = ''
        report += 'ContigSet saved to: '+params['workspace_name']+'/'+params['output_contigset_name']+'\n'
        report += 'Assembled into '+str(len(contigset_data['contigs'])) + ' contigs.\n'
        report += 'Avg Length: '+str(sum(lengths)/float(len(lengths))) + ' bp.\n'

        # compute a simple contig length distribution
        bins = 10
        counts, edges = np.histogram(lengths, bins)
        report += 'Contig Length Distribution (# of contigs -- min to max basepairs):\n'
        for c in range(bins):
            report += '   '+str(counts[c]) + '\t--\t' + str(edges[c]) + ' to ' + str(edges[c+1]) + ' bp\n'

        reportObj = {
            'objects_created':[{'ref':params['workspace_name']+'/'+params['output_contigset_name'], 'description':'Assembled contigs'}],
            'text_message':report
        }

        reportName = 'megahit_report_'+str(uuid.uuid4())   # uuid4() instead of getnode() to ensure uniqueness
        report_obj_info = ws.save_objects({
                'id':info[6],
                'objects':[
                    {
                        'type':'KBaseReport.Report',
                        'data':reportObj,
                        'name':reportName,
                        'meta':{},
                        'hidden':1,
                        'provenance':provenance
                    }
                ]
            })[0]

        output = { 'report_name': reportName, 'report_ref': str(report_obj_info[6]) + '/' + str(report_obj_info[0]) + '/' + str(report_obj_info[4]) }
```

[\[Back to Edit Impl\]](#impl)

##### <A NAME="impl-shell-tool"></A>5.6. Invoking Shell Tool

```python
        command_line_tool_params_str = " ".join(command_line_tool_params)
        command_line_tool_cmd_str = " ".join(command_line_tool_path, command_line_tool_params_str)
        
        # run <command_line_tool>, capture output as it happens
        self.log(console, 'running <command_line_tool>:')
        self.log(console, '    '+command_line_tool_cmd_str))
        p = subprocess.Popen(command_line_tool_cmd_str,
                    cwd = self.scratch,
                    stdout = subprocess.PIPE, 
                    stderr = subprocess.STDOUT, shell = False)

        while True:
            line = p.stdout.readline()
            if not line: break
            self.log(console, line.replace('\n', ''))

        p.stdout.close()
        p.wait()
        self.log(console, 'return code: ' + str(p.returncode))
        if p.returncode != 0:
            raise ValueError('Error running <command_line_tool>, return code: '+str(p.returncode) + 
                '\n\n'+ '\n'.join(console))
 ```

[\[Back to Edit Impl\]](#impl)

##### <A NAME="impl-adding-data"></A>5.7. Adding Data To Your Method

Data that is supported by [KBase Data Types](doc/kb_sdk_data_types_table.md) should be added as a workspace object.  Other data that is used to configure a method may be added to the repo with the code.  Large data sets that exceed a reasonable limit (> 1 GB) should be added to a shared mount point.  This can be accomplished by contacting kbase administrators at http://kbase.us.

[\[Back to Edit Impl\]](#impl)

[\[Back to top\]](#top)