<#-- Package Name definition -->
<#if classDef.getPackageName()?has_content>
package ${classDef.getPackageName()};
</#if>
<#-- import statements -->
import javax.persistence.metamodel.StaticMetamodel;
<#foreach importStatement in classDef.getImportSnippets()![]>
${importStatement}
</#foreach>
/**
 *
 * @author ${author}
 */
<#assign class = ".class">
@StaticMetamodel(${classDef.getValue()}${class})
public class ${classDef.getClassName()}<#if classDef.getSuperClassName()?has_content> extends ${classDef.getSuperClassName()}</#if>  {

<#foreach varDef in classDef.getVariableDefs()![]>
  public static volatile ${varDef.attributeType}<${classDef.getValue()},${varDef.type}> ${varDef.name};
</#foreach>

}

