# How to work with reference data

## Adding Reference Data to a KBase App

## Motivation

Some tools and applications make use of large reference data collections.  For
example, a QC app may have a reference data with common contaminants.  Adding
data such as this to the Git repo for the module can make it very large.
In addition, the generated Docker image would also be very large.  KBase App
reference data is designed to address this scenario.

## How Reference Data works

To minimize the image size, this feature works by saving reference data in a
special volume space and then making that volume available through a read-only
mount in /data at app execution time.  An init mode for the module is run at registration
time to initialize the reference data volume.  In addition, the reference data
is separately versioned, so code-only updates to a module can be made without
having to re-initialize the reference data.  

## Usage

To make use of this feature, the app developer needs to do a few simple things.

1. Add the data-version tag to the kbase.yaml file and give it a semantic version
tag.
2. Add any downloading and data preparation steps to the init section of the
entrypoint.sh script in the scripts directory.  The init script should place the data in
/data and any apps that use the data should be configured to look for the data
in this location.
3. Create a \_\_READY\_\_ file in /data to indicate that the reference data volume
was successfully created.  Ideally, some sanity tests should be performed to confirm
things ran correctly by testing for the presence of expected files.  If the
\_\_READY\_\_ file is not present, the registration will fail and the reference data
area will be removed.

You can see an example in the RAST application:
https://github.com/kbaseapps/RAST_SDK/blob/a975436d9c0af4f772bd7235b467180860f64060/scripts/entrypoint.sh#L18-L28


## Updating Reference Data
If a new version of reference data is required, the
developer can increase the version number for the reference data in kbase.yaml, make any updates to the init section, and re-register
the app.  This will trigger the registration to initialize the reference data
for the new data version.  Older versions of the app will continue to use the previous
reference data specified in that versions kbase.yaml file.  This helps to ensure reproducibility.

## Gotchas

There a few things to watch out for with reference data.

* The reference data area (/data) is mounted during initialization (module registration) and durinng app execution and replaces /data from the Docker image.  Any modifications made by the
Dockerfile to this space will not be visible.  Changes must be done in the init block in entrypoint.sh.
* The reference data is only writeable at registration time.  This is to ensure that the data is not accidentally
changed during execution which could break reproducibility.  If the app requires the reference data to be writeable when it executes,
then you add code to the execution that copies the data into the writeable work area prior to running the underlying application.
