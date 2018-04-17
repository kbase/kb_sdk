# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](/README.md)

0. [Overview and Concepts](overview.md)
1. [Install SDK Dependencies](dependencies.md)
2. [Install SDK with Docker](dockerized_install.md)
3. [Create Module](create_module.md)
4. [Specify Module and App(s)](edit_module.md)
5. **Implement Method(s)**
6. [Publish and Test on Appdev](publish.md)

### Implement your app

The actual code for your app will live in the python package under `lib/MyContigFilter`. The entry point, where your code is initially called, lives in the file: `lib/MyContigFilter/MyContigFilterImpl.py`. This is the file where you will start entering your own Python code.

This "Implementation" file defines the methods available in the module. Since we defined a method named `filter_contigs` in our KIDL .spec file and our `spec.json` file, then we will have a `filter_contigs` method in the class inside `MyContigFilterImpl.py`.

In order to keep your codebase organized, you can also create sub-modules inside the above folder, such as in a directory like `lib/MyContigFilter/utils`.

#### Set up your developer credentials

To use KBase's file storage services, we need to generate a dev token for authentication.

Go to [https://narrative.kbase.us/#auth2/account](https://narrative.kbase.us/#auth2/account), click Developer Tokens, and generate a new token.

Copy and paste that token into `test_local/test.cfg` in the value for `test_token`

#### Receive some parameters

Our first goal is to receive and print the method's parameters. Open `MyContigFilterImpl.py` and find the `filter_contigs` method, which should have some auto-generated boilerplate code and docstrings.

You want to edit code between the comments `#BEGIN filter_contigs` and `#END filter_contigs`. These are special SDK-generated annotations that we have to keep in the code to get everything to compile correctly. If you run `make` again in the future, it will edit code outside these comments, but will not change the code you put between the comments.

Between the above comments, let's add a simple print statement; something like: `print(params['assembly_ref'], params['min_length'])` so we can see what is getting passed into our method.


```py
def filter_contigs(self, ctx, workspace_name, params):
    """
    :param workspace_name: instance of String
    :param params: instance of type "ContigFilterParams" (Input
       parameters) -> structure: parameter "min_length" of Long,
       parameter "assembly_ref" of String
    :returns: instance of type "ContigFilterResults" (Output results) ->
       structure:
    """
    # ctx is the context object
    # return variables are: returnVal
    #BEGIN filter_contigs
    print(params['min_length'], params['assembly_ref'])
    returnVal = {}
    #END filter_contigs
    return [returnVal]
```

Don't try to change the docstring, or anything else outside the `BEGIN filter_contigs` and `END filter_contigs` comments, as your change will get overwritten by the `make` command.

#### Initialize a test

Your `MyModuleImpl.py` file is tested using `test/MyModuleImpl_server_test.py`. This file also has a variety of auto-generated boilerplate code; you will want to add your own test by replacing the `test_your_method(self)` method at the bottom of the test class.

```py
def test_filter_contigs(self):
    ref = "14672/2/1"
    result = self.getImpl().filter_contigs(self.getContext(), {
        'assembly_ref': ref,
        'min_length': 100
    })
    print result
    # TODO -- assert some things (later)
```

We need to provide two parameters to our function: an assembly reference string and a min length integer. For the reference string, we can use this sample reference to a Shewanella Oneidensis assembly on AppDev: `"14672/2/1"`.

Run `kb-sdk test` and, if everything works, you'll see the docker container boot up, the `filter_contig` method will get called, and you will see some printed output.

#### Set the callback URL and scratch path


The callback URL points to a server that is used to spin up other SDK apps that we will need to use in our own app. In our case, we want to use [AssemblyUtil](https://github.com/kbaseapps/AssemblyUtil) to download genome data. When we use that app, our app makes a request to the callback server, which spins up a separate docker container that runs AssemblyUtil.

The other parameter commonly defined in the module constructor is the path to the scratch directory. This directory is a common space accessible by not only the 
current app but also every app called by this app. Therefore files from other apps (eg. AssemblyUtil) will be written to the scratch folder and files to be used by other modules (such as KBaseReport) are read from the scratch folder.

Scratch is a temp directory that only stores ephemeral data for your app. Keep in mind that this data disappears when your app stops running.

Add this into your `__init__` method in your `MyModuleImpl.py`, between the `#BEGIN_CONSTRUCTOR` and `#END_CONSTRUCTOR` comments:

```py
   ...
   # Inside your __init__ function:
   #BEGIN_CONSTRUCTOR
   self.callback_url = os.environ['SDK_CALLBACK_URL']
   self.scratch = config['scratch']
   #END_CONSTRUCTOR
   ...
```

In order to use `os` package, add this import line at the top of your `MyModuleImpl.py`, between the `#BEGIN_HEADER` and `#END_HEADER` comments:

```py
import os
```

Run the `kb-sdk test` command again to make sure you have no errors.

#### Download the FASTA file

We need to convert the reference to a bacterial genome -- `14672/2/1` -- into an actual FASTA file of genome data that our app can access. For that, we can use the [AssemblyUtil](https://github.com/kbaseapps/AssemblyUtil) app.

Install the app with:

```sh
$ kb-sdk install AssemblyUtil
```

That will add an entry for `AssemblyUtil` in your `dependencies.json` file. It also adds a python package under `lib/AssemblyUtil`.

Import the module at the top of your `MyModuleImpl.py` file

```py
from AssemblyUtil.AssemblyUtilClient import AssemblyUtil
```

Inside your `filter_contigs` method, initialize the utility and use it to download `assembly_ref`:

```py
    ...
    # Inside filter_contigs()
    assembly_util = AssemblyUtil(self.callback_url)
    file = assembly_util.get_assembly_as_fasta({'ref': params['assembly_ref']})
    print(file)
    ...
```

* We have to initialize AssemblyUtil and pass in `self.callback_url`
* The `get_assembly_as_fasta` method downloads a file from a ref

Run `kb-sdk test` again and you should see the file download along with its path in the container.

#### Add some basic param validations

It's good practice to make some run-time checks of the parameters passed into your `MyModuleImpl#filter_contigs` method. While params will get checked in the Narrative UI, if your app ever gets called from another codebase, it will bypass any UI typechecks.

Make sure your user passes in a workspace, an assembly reference, and a minimum length greater than zero:

```py
  ...
  # Inside filter_contigs(), after #BEGIN fast_ani, before any other code
  # Check that the parameters are valid
  for name in ['min_length', 'assembly_ref']:
      if name not in params:
          raise ValueError('Parameter "' + name + '" is required but missing')
  if not isinstance(params['min_length'], int) or (params['min_length'] < 0):
      raise ValueError('Min length must be a non-negative integer')
  if not isinstance(params['assembly_ref'], basestring) or not len(params['assembly_ref']):
      raise ValueError('Pass in a valid assembly reference string')
  ...
```

Re-run `kb-sdk test` to make sure everything still works.

We can add some additional tests to make sure we raise ValueErrors for invalid parameters:

```py
...
# Inside test/MyModuleImpl_server_test.py
# At the end of the test class
def test_invalid_params(self):
    impl = self.getImpl()
    ctx = self.getContext()
    ws = self.getWsName()
    # Missing assembly ref
    with self.assertRaises(ValueError):
        impl.filter_contigs(ctx, ws, {'min_length': 100})
    # Missing min length
    with self.assertRaises(ValueError):
        impl.filter_contigs(ctx, ws, {'assembly_ref': 'x'})
    # Min length is negative
    with self.assertRaises(ValueError):
        impl.filter_contigs(ctx, ws, {'assembly_ref': 'x', 'min_length': -1})
    # Min length is wrong type
    with self.assertRaises(ValueError):
        impl.filter_contigs(ctx, ws, {'assembly_ref': 'x', 'min_length': 'x'})
    # Assembly ref is wrong type
    with self.assertRaises(ValueError):
        impl.filter_contigs(ctx, ws, {'assembly_ref': 1, 'min_length': 1})
...
```

#### Filter out contigs based on length

Now we can finally start to implement the real functionality of the app!

The biopython package (called [`Bio`](http://biopython.org/)), included in the SDK build, has a module called [`SeqIO`](http://biopython.org/wiki/SeqIO) that can help us read and filter genome sequence data.

Import this module in your `MyModuleImpl.py` between the header comments like so:

```py
... # other imports
from Bio import SeqIO
...
```

Now, inside `filter_contigs`, enter code to filter out contigs less than the given min_length:

```py
...
# Inside MyModuleImpl#filter_contigs, after you have fetched the fasta file:
# Parse the downloaded file in FASTA format
parsed_assembly = SeqIO.parse(file['path'], 'fasta')
min_length = params['min_length']
# Keep a list of contigs greater than min_length
good_contigs = []
# total contigs regardless of length
n_total = 0
# total contigs over the min_length
n_remaining = 0
for record in parsed_assembly:
    n_total += 1
    if len(record.seq) >= min_length:
        good_contigs.append(record)
        n_remaining += 1
returnVal = {
    'n_total': n_total,
    'n_remaining': n_remaining
}
...
```

Run `kb-sdk test` again and check the output.

#### Add real tests

Return to `test/MyModuleImpl_server_test.py` and add tests for the functionality we just added above.

Set `min_length` to a value that filters out some contigs but not others. In our case, our FASTA only has 2 sequences of lenths 4969811 and 161613. An in-between minimum could be 200000.

We would expect to keep 1 contig and filter out the other.

```py
...
# Inside MyModuleImpl_server_test:
def test_filter_contigs(self):
    ref = "14672/2/1"
    params = {
        'assembly_ref': ref,
        'min_length': 200000
    }
    result = self.getImpl().filter_contigs(self.getContext(), self.getWsName(), params)
    self.assertEqual(result[0]['n_total'], 2)
    self.assertEqual(result[0]['n_remaining'], 1)
...
```

Run `kb-sdk test` again to make sure it all passes.

#### Output the filtered assembly

Next, we want to save and upload a new version of our genome assembly data with the contigs filtered out.

Beneath the code that we wrote to filter the assembly, add this file saving and uploading code.

```py
...
# Underneath your loop that filters contigs:
# Create a file to hold the filtered data
filtered_path = os.path.join(self.scratch, 'filtered.fasta')
SeqIO.write(good_contigs, filtered_path, 'fasta')
# Upload the filtered data to the workspace
new_ref = assembly_util.save_assembly_from_fasta({
    'file': {'path': filtered_path},
    'workspace_name': workspace_name,
    'assembly_name': file['assembly_name']
})
returnVal = {
    'n_total': n_total,
    'n_remaining': n_remaining,
    'filtered_assembly_ref': new_ref
}
#END filter_contigs
...
```

Add a simple assertion into your `test_fast_ani` method to check for the `filtered_assembly_ref`. Something like:

```py
self.assertTrue(len(result[0]['filtered_assembly_ref']))
```

Run `kb-sdk test` again to make sure you have no errors

#### Build a report object

In order to output data into the UI inside a narrative, your app needs to build and return a [KBaseReport](https://github.com/kbaseapps/KBaseReport).

Install the KBaseReport app with:

```sh
$ kb-sdk install KBaseReport
```

Import the report module between the `#BEGIN_HEADER` and `#END_HEADER` section of your `MyModuleImpl.py` file:

```py
from KBaseReport.KBaseReportClient import KBaseReport
```

The KBaseReport takes a series of dictionary objects that can have text messages, object references, and more. Add the report initialization code inside your `filter_contigs` method:

```py
# Inside the filter_contigs method, below where we uploaded the new file:
# Create an output summary message for the report
text_message = "".join([
    'Filtered assembly to ',
    str(n_remaining),
    ' contigs out of ',
    str(n_total)
])
# Data for creating the report, referencing the assembly we uploaded
report_data = {
    'objects_created': [
        {'ref': new_ref, 'description': 'Filtered contigs'}
    ],
    'text_message': text_message
}
# Initialize the report
kbase_report = KBaseReport(self.callback_url)
report = kbase_report.create({
    'report': report_data,
    'workspace_name': workspace_name
})
# Return the report reference and name in our results
returnVal = {
    'report_ref': report['ref'],
    'report_name': report['name'],
    'n_total': n_total,
    'n_remaining': n_remaining,
    'filtered_assembly_ref': new_ref
}
#END filter_contigs
```

Add a couple assertions in our `test_filter_contigs` method inside `test/MyModuleImpl_server_test.py` to check for the report name and ref:

```py
...
self.assertTrue(len(result[0]['report_name']))
self.assertTrue(len(result[0]['report_ref']))
...
```

Run `kb-sdk test` again to make sure it all works.

#### Configure your app's output data

We nearly have a complete app. The last step is to take all the result data we defined in `MyModuleImpl#filter_contigs` and add entries for them in our `MyModule.spec` KIDL type file as well as our `spec.json` UI config file.

Add a type entry for our result data in our KIDL file:

```
    /* Output results */
    typedef structure {
        string report_name;
        string report_ref;
        string filtered_assembly_ref;
        int n_total;
        int n_remaining;
    } ContigFilterResults;
```

Run `make` and `kb-sdk test` again to make sure everything works.

In your `ui/narrative/methods/filter_contigs/spec.json` file, add entries for this output data:

```json
...
"output_mapping": [
    {
        "service_method_output_path": [0,"report_name"],
        "target_property": "report_name"
    },
    {
        "service_method_output_path": [0,"report_ref"],
        "target_property": "report_ref"
    },
    {
        "narrative_system_variable": "workspace",
        "target_property": "workspace_name"
    }
]
...
```

Now we have some output entries that point to our report and workspace, which will show up when the job finishes in the narrative.

Finally, under `widgets/output` in the JSON (around line 10), set `"output"` to `"no-display"`. This prevents our app from creating a separate output cell.

We've added an entry for everything we put in the `returnVal` dictionary that gets returned from `MyModuleImpl#filter_contigs`.

Run `kb-sdk test` a final time to make sure everything runs smoothly. If so, we have a working app!

[\[Next tutorial page\]](publish.md)<br>
[\[Back to top\]](#top)<br>
[\[Back to steps\]](/README.md#steps)
