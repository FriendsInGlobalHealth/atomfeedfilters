openmrs-module-atomfeedfilters
==========================

Description
-----------
The module providing feed filters to be used with the Atom feed module. The filter strategies are applied
by the client to filter data pulled from parent.

Installation
------------
1. Build the module to produce the .omod file.
2. Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file.

If uploads are not allowed from the web (changable via a runtime property), you can drop the omod
into the ~/.OpenMRS/modules folder.  (Where ~/.OpenMRS is assumed to be the Application 
Data Directory that the running openmrs is currently using.)  After putting the file in there 
simply restart OpenMRS/tomcat and the module will be loaded and started.

How to Use
----------
Include the filter strategy component name in the ﻿feedFilterBeans array of the atomfeed configuration on
both the parent and child instances. Available filter strategy names are listed below.

* _atomfeedFilters.LocationBasedFilterStrategy_ : Filters visits, encounters, and obs based on configured
location table fields with default fields being county_district and name.
    * Provide the desired location table fields/column names as a % separated list using the global property
    _atomfeedfilters.location.fields_; for example _province%county_district%name_
    * Provide the location name as th eglobal property _atomfeedfilters.location.value_ value which takes the form 
    _\<field value>%\<field value>%\<field value>_. _\<field value>_ is the actual value of the field to filter against,
    it can also be _*_ to indicate that any value is accepted. For example, for the default _county_district%name_ fields,
    the value could be something like _Quelimane%pebane_ or _Quelimane%*_

####Note
The values to filter against need be specified only the the child instance. The choice of fields to be used for filtering
need to be configured on both parent and child instances.