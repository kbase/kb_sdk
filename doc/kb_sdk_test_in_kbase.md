# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install and Build SDK](kb_sdk_install_and_build.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Edit Module and Method(s)](kb_sdk_edit_module.md)
5. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
6. [Register Module](kb_sdk_register_module.md)
7. **Test in KBase**
8. [Complete Module Info](kb_sdk_complete_module_info.md)
9. [Deploy](kb_sdk_deploy.md)


### 7. Test in KBase

#### 7A. Start a Narrative Session

Go to https://appdev.kbase.us, log in, and start a new Narrative.

Click on the 'R' in the method panel list until it switches to 'D' for methods still in development.  Find your new method by searching for your module, and run it.  If you encounter errors, you should make your edits/debugging statements to your code, commit those changes, push it to your SDK Module git (or other open-source) repo, reregister with the "SDK Register" App, and rerun to see if your fixes did the trick.  You must push your edits to your repo and reregister for each test for the Docker image to contain those changes.  We recommend getting as many bugs out in the [Local Testing](kb_sdk_local_test_module.md) stage as possible.

Explore the other SDK methods in the Narrative method panel

    https://appdev.kbase.us/#appcatalog/
    
For finer-grain control of the KBase Catalog registration process, use a code cell:

    from biokbase.catalog.Client import Catalog
    catalog = Catalog(url="https://appdev.kbase.us/services/catalog")
    catalog.version()

The KBase Catalog API is defined here: https://github.com/kbase/catalog/blob/master/catalog.spec

[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
