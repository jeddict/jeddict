<#-- Package Name definition -->
<#if classDef.getPackageName()?has_content>
package ${classDef.getPackageName()};
</#if>
<#-- import statements -->
import java.io.Serializable;
<#foreach importStatement in classDef.getImportSnippets()![]>
${importStatement}
</#foreach>
<#-- java doc -->
<#if classDef.isJavaDocExist()>
${classDef.getJavaDoc()}
</#if>
<#if classDef.getManagedType()?has_content>
${classDef.getManagedType()}
</#if>
public class ${classDef.getClassName()} implements Serializable {
<#-- member variables -->
<#foreach varDef in classDef.getVariableDefs()![]>
<#-- $varDef.type => $varDef.getTypeIdentifier().getVariableType() ## to resolve problem Collection => Collection<Entity> -->
    private ${varDef.type} ${varDef.name};
</#foreach>
<#foreach constructor in classDef.getConstructors()![]>
    ${constructor.getSnippet()}
</#foreach>
<#-- getter/setter -->
<#foreach varDef in classDef.getVariableDefs()![]>
<#assign method = varDef.getMethodName()>

<#assign methodPrefix = varDef.getGetterMethodPrefix()>
   public ${varDef.type} ${methodPrefix}${method}() {
        return ${varDef.name};
    }

<#assign methodPrefix = varDef.getSetterMethodPrefix()>
    public void ${methodPrefix}${method} (${varDef.type} ${varDef.name}) {
        this.${varDef.name} = ${varDef.name};
    }
</#foreach>
    @Override
    public boolean equals(Object obj) {
        ${classDef.getEqualsMethod().getSnippet()}
    }

    @Override
    public int hashCode() {
        ${classDef.getHashcodeMethod().getSnippet()}
    }

    @Override
    public String toString() {
        return ${classDef.getToStringMethod().getSnippet()};
    }

}
