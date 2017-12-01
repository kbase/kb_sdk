# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install SDK with Docker](kb_sdk_dockerized_install.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Specify Module and Method(s)](kb_sdk_edit_module.md)
5. [Implement Method(s)](kb_sdk_impl_methods.md)
6. [Specify User Interface](kb_sdk_make_ui.md)
7. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
8. [Register Module](kb_sdk_register_module.md)
9. **Test in KBase**
10. [Complete Module Info](kb_sdk_complete_module_info.md)
11. [Deploy](kb_sdk_deploy.md)


### 9. Test in KBase

#### 9A. Start a Narrative Session

Go to https://appdev.kbase.us, log in, and start a new Narrative.

Click on the 'R' in the Apps Panel until it switches to 'D' to show apps still in development.  Find your new app by searching for your module, add it to your Narrative by clicking on it, and run it. If you encounter errors, make the appropriate edits or add debugging statements to your code, commit those changes, push it to your SDK Module git (or other open-source) repo, reregister your app via the catalog service (https://narrative.kbase.us/#catalog/modules -- see [Register Module](kb_sdk_register_module.md) more info), and rerun to see if your fixes did the trick.  You must push your edits to your repo and reregister for each test for the Docker image to contain those changes.  We recommend getting as many bugs out in the [Local Testing](kb_sdk_local_test_module.md) stage as possible.

[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
