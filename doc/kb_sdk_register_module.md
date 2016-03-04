# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install and Build SDK](kb_sdk_install_and_build.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Edit Module and Method(s)](kb_sdk_edit_module.md)
5. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
6. **Register Module**
7. [Test in KBase](kb_sdk_test_in_kbase.md)
8. [Complete Module Info](kb_sdk_complete_module_info.md)
9. [Deploy](kb_sdk_deploy.md)


### 6. Register Module


#### 6A. Create Git Repo

If you haven't already, add your repo to [GitHub](http://github.com) (or any other public git repository), from the ContigCount base directory:

    cd ContigFilter
    git init
    git add .
    git commit -m 'initial commit'
    # go to github and create a new repo that is not initialized
    git remote add origin https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME].git
    git push -u origin master


#### 6B. Register with KBase

Now go to https://appdev.kbase.us/#appcatalog/register.  Enter your public git repo url:

    https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME]
    
and submit.  Wait for the registration to complete (it may take awhile on the first registration as it has to build the Docker image that is specific to your Module from scratch).  Note that you must be an approved developer to register a new module (to become an approved developer, please contact us at http://kbase.us/contact-us )

Your method is now available in the AppDev environment in KBase. Go to https://appdev.kbase.us and start a new narrative.  Click on the 'R' in the method panel list until it switches to 'D' for methods still in development.  Find your new method by searching for your module, and run it to filter some contigs.

Your method will now also be visible in the App Catalog when displaying Apps in development:

    https://appdev.kbase.us/#appcatalog/browse/dev
    https://narrative.kbase.us/#appcatalog/browse/dev
    
From your module page

    https://narrative.kbase.us/#appcatalog/module/[MODULE_NAME]
    
you'll be able to register any update and manage release of your module to the production KBase environment for anyone to use.

Open

    Module Admin Tools
    
and then click on the **REGISTER** button (you will not need to add the URL for your repo after the first time).

As you make changes to your Module, **you will need to re-commit those changes to the git repo, and then re-register**.  The KBase SDK Catalog service will automatically pull the most recent version.  If for some reason you wish to revert to an older version, you can add the checksum of that old version, available by typing

    git log
    
    
[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
