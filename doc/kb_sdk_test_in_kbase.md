# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

[\[6. Register Module\]](kb_sdk_register_module.md) \[7. Test in KBase\] [\[8. Complete Module Info\]](kb_sdk_complete_module_info.md)


### 7. Test in KBase

#### 7A. Start a Narrative Session

Go to https://narrative-ci.kbase.us and start a new Narrative.

Click on the 'R' in the method panel list until it switches to 'D' for methods still in development.  Find your new method by searching for your module, and run it to count some contigs.

Explore the other SDK methods in the Narrative method panel.  For finer-grain control of the KBase Catalog registration process, use a code cell:

    from biokbase.catalog.Client import Catalog
    catalog = Catalog(url="https://ci.kbase.us/services/catalog")
    catalog.version()

The KBase Catalog API is defined here: https://github.com/kbase/catalog/blob/master/catalog.spec

[Back to top](#top)<br>
[Back to steps](../README.md#steps)
