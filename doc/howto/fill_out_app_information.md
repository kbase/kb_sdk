# How to fill out your app's information for publishing

### Complete Module Info

Icons, Publications, Original tool authors, Institutional Affiliations, Contact Information, and most importantly, Method Documentation must be added to your module before it can be deployed.  This information will show up in the App Catalog:
    https://appdev.kbase.us/#appcatalog/
    
Please be aware that your module implementation and information must conform to our [Policies](https://github.com/kbase/project_guides/blob/master/SDK_Guidelines.md) before it will be accepted for public deployment. Fortunately, most of these are common sense (for example, sufficient content on the App Info page for a user to run your app and understand what it's  doing, proper arguments and explanation of their use on the input widget, etc.), but please take the time to familiarize yourself with these requirements before requesting public deployment.


#### Adding an Icon

You can make a custom icon for each app in your module, or use an existing one that corresponds to the tool that you have wrapped. Feel free to repurpose the icons from existing KBase apps, or make your own. Your icon can be PNG, GIF, or JPEG (the KBase ones are PNG) and should fit in a square 200x200 pixels. We think rounded-corner squares look best; ours use a 40 pixel rounding on the corner on the 200x200 image (you can do this in Adobe Illustrator). If you want to match our font, it's Futura Condensed Medium, 72 point height and line spacing, white face with a 1pt white border. If you use Adobe Illustrator, export your icon as PDF and convert to a 72dpi PNG with Adobe Photoshop. PDF vector and PNG bitmap versions that we used for our icons are available at https://github.com/kbase/kb_sdk/img/ in case you would like to use them as a starting point.

Your icons should  be added to your KBase SDK module GitHub repo in the **img** image folder for each app at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/img/

Then edit the **display.yaml** file found at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/display.yaml

and add an **icon:** configuration (NOTE: just add the name of the image file and do not include the path, so *not* "img/foo.png").  For  example, in the file:
    https://github.com/psdehal/kb_trimmomatic/blob/master/ui/narrative/methods/run_trimmomatic/display.yaml

the **icon:** is configured by the line:
    icon: trimmomatic-orange.png


#### Naming and Categorizing

Each app should have a unique display name. The display name is configured in the **display.yaml** file at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/display.yaml

and add a **name:** configuration. If you are wrapping an existing tool with a known name, please include that name to make it easier for people to find it, such as

    name: Trimmomatic - Read Trimming

You should tag your app with one (or multiple) "categories". (If you don't, it will appear in the "Uncategorized" section and users will be less likely to find it.)

The categories are set in the **spec.json** file at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/spec.json
    
The recognized categories can be found in https://github.com/kbase/kbase-ui/blob/master/src/client/modules/plugins/catalog/modules/data/categories.yml.

Currently, the following categories are recognized. The tag before the : on each line is the internal label for the category, used in the spec.json. The phrase after the : is the display name for the category that is used in the App Catalog.

    annotation: Genome Annotation
    assembly: Genome Assembly
    communities: Microbial Communities
    comparative_genomics: Comparative Genomics
    expression: Expression
    metabolic_modeling: Metabolic Modeling
    reads: Read Processing
    sequence: Sequence Analysis
    util: Utilities

(Please contact us via http://kbase.us/contact-us if you have suggestions for more... we expect to add more categories and possibly subcategories in the near future.)
 
An example of a category configuration line is:

    "categories": ["active","assembly","util"],

Please leave the category "active" at the beginning of the list of categories, as this is a special category that indicates whether your app should be shown at all in the App Catalog. The rest of the categories are treated as normal tags.


#### Writing your App Info page

Information for the App Info page is configured in the **display.yaml** file at:

    https://github.com/<MyUserName>/<MyModule>/ui/narrative/methods/<MyMethod>/display.yaml

The fields that should be configured are

- name
- icon
- tooltip
- description
- publications
- screenshots

##### Name, Icon, Tooltip, Description
**name:** and **icon:** are explained above.  You must also add a **tooltip:** as a secondary short description (usually one sentence summarizing the purpose of the app).

The **description:** field is for a more detailed description, which can include several recommended pieces of information. For example, the URL of an exemplar Narrative that demonstrates how to use the app should be included in the description.
If you are wrapping an existing tool, please add links to the open-source repo for that tool in both the **description:** field and the **publications:** field. If there is a home page for the tool, please also add a link to that.

##### Links and Publications
Relevant publications, especially if you are wrapping an existing tool, are added with subfields. Each publication should start with a "-" on a blank line, followed by the fields **pmid:** for PubMed ID, **display-text:** for the publication (please include the DOI), and a **link:** to the publication.  Additional entries for tool home pages and open source repos should also be included as **link:** fields.

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

##### Screenshots
You can add screenshots (or other relevant images) to the "img/" folder in the same fashion as the icon image. These screenshots should be configured in the **display.yaml** file as a list with one filename on each line, preceded by a hyphen, e.g.,

```
screenshots:
    - screenshot_1.png
    - screenshot_2.png
```

If you do not want to have any screenshots, leave the **screenshots:** list blank.

```
screenshots: []
```

##### Example

For an example of a complete App Info page that would be acceptable for public deployment, please see examples in the Trimmomatic app:

* https://appdev.kbase.us/#appcatalog/app/kb_trimmomatic/run_trimmomatic/dev
* https://github.com/psdehal/kb_trimmomatic/blob/master/ui/narrative/methods/run_trimmomatic/display.yaml
    


Please bear in mind that for public release, your Module **MUST** meet all the requirements laid out in the [KBase SDK Policies](https://github.com/kbase/project_guides/blob/master/SDK_Guidelines.md). We reserve the right to delay public release of SDK Modules until all requirements are met. Please take the time to familiarize yourself with these policies to avoid delay in releasing your Module.
