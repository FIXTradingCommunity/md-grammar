## Content ownership and history

### Provenance

The `<metadata>` element is used to identify a particular Orchestra file
and the issuer of that file. It can contain any of the elements defined
by the Dublin Core XML schema. Recommended elements include title,
publisher, date, and rights.

**Example:** Metadata

```xml import md2orchestra-proto.xml from "<fixr:metadata>" to "</fixr:metadata>"
```

### Pedigree

Most message elements in the schema support a complete history of
creation, change and potentially deprecation with support of attribute group
entityAttribGrp. Each historical event should be qualified by its extension pack (EP). In the past, they were also qualified by protocol version. However, each EP now produces FIX Latest; protocol versions will no longer change.