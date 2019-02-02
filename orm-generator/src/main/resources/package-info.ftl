<#-- JAXB -->
<#if classDef.isJaxbMetadataExist()>
@XmlSchema(namespace = "${classDef.getNamespace()}", elementFormDefault = XmlNsForm.QUALIFIED)
</#if>
<#-- JSONB -->
<#foreach snippet in classDef.getJSONBSnippets()![]>
    ${snippet.getSnippet()}
</#foreach>
<#-- Package Name definition -->
<#if classDef.getPackageName()?has_content>
package ${classDef.getPackageName()};
</#if>
<#foreach importStatement in classDef.getImportSnippets()![]>
${importStatement}
</#foreach>