Description
===========

Android application for systems information lookup.
Reads the information from assets/overview.csv. Generate it with:

```
quattor_generate -s > app/src/main/assets/overview.csv
```

Or provide it manually. The columns are:
 - hostname / FQDN
 - Vendor
 - Serial Number
 - Server Type
 - Server Model
 - Site / Room
 - Rack
 - Location in the rack
 - Support end date
