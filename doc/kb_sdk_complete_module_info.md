# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install and Build SDK](kb_sdk_install_and_build.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Edit Module and Method(s)](kb_sdk_edit_module.md)
5. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
6. [Register Module](kb_sdk_register_module.md)
7. [Test in KBase](kb_sdk_test_in_kbase.md)
8. **Complete Module Info**
9. [Deploy](kb_sdk_deploy.md)


### 8. Complete Module Info


Icons, Publications, Original tool authors, Institutional Affiliations, Contact Information, and most importantly, Method Documentation must be added to your module before it can be deployed.  This information will show up in the App Catalog Browser.

    https://appdev.kbase.us/#appcatalog/
    
Please be aware that your module implementation and information must conform to our [Policies](https://github.com/kbase/project_guides/blob/master/SDK_Guidelines.md) before it will be accepted for public deployment.  Fortunately, most of these are common sense (sufficient content on the App Info page for a user to run your method and understand what it's really doing, proper arguments and explanation of their use on the input widget, etc.), but please take the time to familiarize yourself with these requirements before requesting public deployment.


#### 8A. Adding an Icon

You can make a custom icon for each method in your module, or use an existing one that corresponds the tool that you have wrapped.  Feel free to repurpose the examples from KBase methods, or make your own.  Your icon can be PNG, GIF, or JPEG (the KBase ones are PNG) and should fit in a square 200x200 pixels.  We think rounded corner squares look best and ours used a 40 pixel rounding on the corner on the 200x200 image (you can do this in Adobe Illustrator).  If you want to match our font, it's Futura Medium Condensed, 72 point height and line spacing, white face with a 1pt white border.  If you use Adobe Illustrator, export as PDF and convert to a 72dpi PNG with Adobe Photoshop.

This image should then be added to your KBase SDK module github repo in the image folder for each method at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/img/

Then edit the *display.yaml* file found at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/display.yaml

and add an "icon:" configuration (NOTE: just add the name of the image file and do not include the path, so *not* "img/foo.png").  For example, add the line:

    icon: trimmomatic-orange.png


#### 8B. Naming and Categorizing

Each method should be have a unique display name.  The display name is configured in the *display.yaml* file at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/display.yaml

and add a "name:" configuration.  If you are wrapping an existing tool with a known name, please include that name in all CAPS to make it easier for people to find it, such as

    name: TRIMMOMATIC - Read Trimming

You should tag your method with one (or multiple) "categories" to your method.  This is done in the *spec.json* file at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/spec.json

Currently, we offer the following categories (please contact us via http://kbase.us if you have suggestions for more... we expect to at least be adding subcategories in the near future):

- assembly
- annotation
- comparative_genomics
- metabolic_modeling
- expression
- communities
- util

An example of a category line is:

	"categories": ["active","assembly","util"],

Please leave the category "active" at the beginning of the tags as this is a special category that indicates whether your method should be shown at all in the App Catalog.  The rest of the categories are treated as normal tags.


#### 8C. Writing your App Info page

Information for the App Info page is configured in the *display.yaml* file at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/display.yaml

The fields that should be configured are

- name
- icon
- tooltip
- description
- publications
- screenshots

*name:* and *icon:* are explained above.  You must also add a "tooltip:" as a secondary short description, as well as a *description:* for a more detailed description.  If you are wrapping an existing tool, please add links to the open-source repo for that tool in both the *description:* field and the *publications:* field.  If there is a home page for the tool, please also add a link to that.

Relevant publications, especially if you are wrapping an existing tool, are added with subfields.  Each publication should start with a "-" on a blank line, followed by the fields *pmid:* for PubMed ID, *display-text:* for the publication (please include DOI), and a *link:* to the publication.  Additional entries for tool home pages and open source repos should also be included as *link:* fields.

An example:

```
publications :
    -
        pmid: 24695404
        display-text : |
            'Bolger AM, Lohse M, Usadel B., (2014) Trimmomatic: a flexible trimmer for Illumina sequence data. Bioinformatics. 2014 Aug 1;30(15):2114-20. doi: 10.1093/bioinformatics/btu170.'
        link: http://www.ncbi.nlm.nih.gov/pubmed/24695404
    -
    	link: http://www.usadellab.org/cms/?page=trimmomatic
```

For an example of a complete App Info page, please see:

    https://appdev.kbase.us/#appcatalog/app/kb_trimmomatic/run_trimmomatic/dev
    https://github.com/psdehal/kb_trimmomatic/blob/master/ui/narrative/methods/run_trimmomatic/display.yaml
    

[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
