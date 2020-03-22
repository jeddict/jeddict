<#-- custom snippet - before package -->
<#if classDef.getCustomSnippet("BEFORE_PACKAGE")?has_content>
<#foreach snippet in classDef.getCustomSnippet("BEFORE_PACKAGE")![]>
${snippet}
${n}</#foreach>
</#if>
<#-- Package Name definition -->
<#if classDef.getPackageName()?has_content>
    package ${classDef.getPackageName()};
</#if>
<#-- custom snippet - after package -->
<#if classDef.getCustomSnippet("AFTER_PACKAGE")?has_content>
<#foreach snippet in classDef.getCustomSnippet("AFTER_PACKAGE")![]>
${snippet} 
${n}</#foreach>
</#if>
<#-- import statements -->
<#foreach importStatement in classDef.getImportSnippets()![]>
${importStatement}
</#foreach>
<#-- custom class snippet - import -->
<#foreach snippet in classDef.getCustomSnippet("IMPORT")![]>
${snippet} 
</#foreach>
<#-- custom attribute snippet - import -->
<#foreach varDef in classDef.getVariableDefs()>
<#foreach snippet in varDef.getCustomSnippet("IMPORT")![]>
${snippet} 
</#foreach>
</#foreach>
<#-- java doc -->
<#if classDef.isJavaDocExist()>
${classDef.getJavaDoc()}
</#if>
<#-- custom class snippet - before class -->
<#if classDef.getCustomSnippet("BEFORE_CLASS")?has_content>
<#foreach snippet in classDef.getCustomSnippet("BEFORE_CLASS")![]>
${snippet} 
${n}</#foreach>
</#if>
<#-- jaxb annotations -->
<#if classDef.isJaxbSupport()>
@XmlAccessorType(XmlAccessType.FIELD)
<#if classDef.isXmlRootElement()>
@XmlRootElement
</#if>
</#if>
<#-- class annotations -->
<#-- class jpa annotations start -->
<#if classDef.getManagedType()?has_content>
${classDef.getManagedType()}
<#if classDef.getTableDef()??>
${classDef.getTableDef().getSnippet()}
</#if>
<#if (classDef.getInheritance())??>
${classDef.getInheritance().getSnippet()}
</#if>
<#if classDef.getPrimaryKeyJoinColumns()??>
${classDef.getPrimaryKeyJoinColumns().getSnippet()}
</#if>
<#if classDef.getSecondaryTables()??>
${classDef.getSecondaryTables().getSnippet()}
</#if>
<#if classDef.getIdClass()??>
${classDef.getIdClass().getSnippet()}
</#if>
<#if classDef.getAssociationOverrides()??>
${classDef.getAssociationOverrides().getSnippet()}
</#if>
<#if classDef.getAttributeOverrides()??>
${classDef.getAttributeOverrides().getSnippet()}
</#if>
<#if classDef.getSQLResultSetMappings()??>
${classDef.getSQLResultSetMappings().getSnippet()}
</#if>
<#if classDef.getEntityListeners()??>
${classDef.getEntityListeners().getSnippet()}
</#if>
<#if classDef.isDefaultExcludeListener()>
@ExcludeDefaultListeners
</#if>
<#if classDef.isExcludeSuperClassListener()>
@ExcludeSuperclassListeners
</#if>
<#if (classDef.getDiscriminatorValue())??>
${classDef.getDiscriminatorValue().getSnippet()}
</#if>
<#if (classDef.getDiscriminatorColumn())??>
${classDef.getDiscriminatorColumn().getSnippet()}
</#if>
<#if classDef.getCacheableDef()??>
${classDef.getCacheableDef().getSnippet()}
</#if>
<#if classDef.getConverts()??>
${classDef.getConverts().getSnippet()}
</#if>
<#--  named queries -->
<#if classDef.getNamedQueries()??>
${classDef.getNamedQueries().getSnippet()}
</#if>
<#if classDef.getNamedNativeQueries()??>
${classDef.getNamedNativeQueries().getSnippet()}
</#if>
<#--  named entity graphs -->
<#if classDef.getNamedEntityGraphs()??>
${classDef.getNamedEntityGraphs().getSnippet()}
</#if>
<#--  named stored procedure -->
<#if classDef.getNamedStoredProcedureQueries()??>
${classDef.getNamedStoredProcedureQueries().getSnippet()}
</#if>
</#if>
<#-- class jpa annotations end -->
<#--  JSONB -->
<#foreach snippet in classDef.getJSONBSnippets()![]>
${snippet.getSnippet()}
</#foreach>
<#--  custom annotation -->
<#foreach annotation in classDef.getAnnotation("TYPE")![]>
${annotation.getSnippet()}
</#foreach>
public<#if classDef.isAbstractClass()> abstract</#if> class ${classDef.getClassName()}<#if classDef.isTypeParameterExist()><${classDef.getTypeParameterList()}></#if><#if classDef.getSuperClassName()??> extends ${classDef.getSuperClassName()}</#if><#if classDef.isInterfaceExist()> implements ${classDef.getUnqualifiedInterfaceList()}</#if> { 

<#-- custom class snippet - before field -->
<#if classDef.getCustomSnippet("BEFORE_FIELD")?has_content>
<#foreach snippet in classDef.getCustomSnippet("BEFORE_FIELD")![]>
    ${snippet} 
${n}</#foreach>
</#if>
<#-- member variables -->
<#foreach varDef in classDef.getVariableDefs()>
<#-- custom attribute snippet - before field -->
<#if varDef.getCustomSnippet("BEFORE_FIELD")?has_content>
<#foreach snippet in varDef.getCustomSnippet("BEFORE_FIELD")![]>
    ${snippet} 
${n}</#foreach>
</#if>
<#if varDef.isPropertyJavaDocExist()>
${varDef.getPropertyJavaDoc()}
</#if>
<#-- var jpa annotations start -->
<#if classDef.getManagedType()?has_content>
<#if varDef.getTranzient()??>
    ${varDef.getTranzient().getSnippet()}
</#if>
<#if varDef.getId()??>
    ${varDef.getId().getSnippet()}
</#if>
<#if varDef.getRelation()??>
    ${varDef.getRelation().getSnippet()}
</#if>
<#if varDef.getElementCollection()??>
    ${varDef.getElementCollection().getSnippet()}
</#if>
<#if varDef.getVersion()??>
    ${varDef.getVersion().getSnippet()}
</#if>
<#if varDef.isEmbedded()>
    @Embedded
</#if>
<#if varDef.isEmbeddedId()>
    @EmbeddedId
</#if>
<#if varDef.getLob()??>
    ${varDef.getLob().getSnippet()}
</#if>
<#if varDef.getBasic()??>
    ${varDef.getBasic().getSnippet()}
</#if>
<#if varDef.getGeneratedValue()??>
    ${varDef.getGeneratedValue().getSnippet()}
</#if>
<#if varDef.getTableGenerator()??>
    ${varDef.getTableGenerator().getSnippet()}
</#if>
<#if varDef.getSequenceGenerator()??>
    ${varDef.getSequenceGenerator().getSnippet()}
</#if>
<#if varDef.getColumn()??>
    ${varDef.getColumn().getSnippet()}
</#if>
<#if varDef.getOrderBy()??>
    ${varDef.getOrderBy().getSnippet()}
</#if>
<#if varDef.getOrderColumn()??>
    ${varDef.getOrderColumn().getSnippet()}
</#if>
<#if varDef.getMapKey()?has_content>
    ${varDef.getMapKeyString()}
</#if>
<#if varDef.getJoinColumns()??>
    ${varDef.getJoinColumns().getSnippet()}
</#if>
<#if varDef.getJoinTable()?has_content>
    ${varDef.getJoinTable().getSnippet()}
</#if>
<#if varDef.getCollectionTable()?has_content>
    ${varDef.getCollectionTable().getSnippet()}
</#if>
<#if varDef.getEnumerated()??>
    ${varDef.getEnumerated().getSnippet()}
</#if>
<#if varDef.getTemporal()??>
    ${varDef.getTemporal().getSnippet()}
</#if>
<#if varDef.getConverts()??>
    ${varDef.getConverts().getSnippet()}
</#if>
<#if varDef.getAssociationOverrides()??>
${varDef.getAssociationOverrides().getSnippet()}
</#if>
<#if varDef.getAttributeOverrides()??>
${varDef.getAttributeOverrides().getSnippet()}
</#if>
</#if>
<#-- var jpa annotations end -->
<#foreach annotation in varDef.getAnnotation("PROPERTY")![]>
    ${annotation.getSnippet()}
</#foreach>
<#foreach snippet in varDef.getJSONBSnippets()![]>
    ${snippet.getSnippet()}
</#foreach>
<#foreach constraint in varDef.getAttributeConstraints()![]>
    ${constraint.getSnippet()}
</#foreach>
<#-- var - jaxb annotations -->
<#if classDef.isJaxbSupport()>
<#assign jaxbAnnotationSnippet = varDef.getJaxbAnnotationSnippet()>
<#if jaxbAnnotationSnippet != "">
    ${jaxbAnnotationSnippet}
</#if>
</#if>
<#-- $varDef.type => $varDef.getTypeIdentifier().getConstraintVariableType() ## to resolve problem Collection => Collection<Entity> -->    
    ${varDef.accessModifier} ${varDef.constraintType} ${varDef.name}<#if varDef.getDefaultValue()?has_content> = ${varDef.getDefaultValue()}</#if>;
<#-- custom attribute snippet - after field -->
<#if varDef.getCustomSnippet("AFTER_FIELD")?has_content>
<#foreach snippet in varDef.getCustomSnippet("AFTER_FIELD")![]>
    ${snippet} 
${n}</#foreach>
</#if>
</#foreach>
<#-- custom class snippet - after field -->
<#if classDef.getCustomSnippet("AFTER_FIELD")?has_content>
<#foreach snippet in classDef.getCustomSnippet("AFTER_FIELD")![]>
    ${snippet}
${n}</#foreach>
</#if>

<#-- Constructor -->
<#foreach constructor in classDef.getConstructors()>
    ${constructor.getSnippet()}
${n}</#foreach>
<#-- custom class snippet - before method -->
<#if classDef.getCustomSnippet("BEFORE_METHOD")?has_content>
<#foreach snippet in classDef.getCustomSnippet("BEFORE_METHOD")![]>
    ${snippet}
${n}</#foreach>
</#if>
<#-- getter/setter -->
<#foreach varDef in classDef.getVariableDefs()>
<#-- custom attribute snippet - before method -->
<#if varDef.getCustomSnippet("BEFORE_METHOD")?has_content>
<#foreach snippet in varDef.getCustomSnippet("BEFORE_METHOD")![]>
    ${snippet}
${n}</#foreach>
</#if>
<#--  getter -->
<#if varDef.isGetterJavaDocExist()>
${varDef.getGetterJavaDoc()}
</#if>
<#foreach annotation in varDef.getAnnotation("GETTER")![]>
    ${annotation.getSnippet()}
</#foreach>
<#assign method = varDef.getMethodName()>
<#assign methodPrefix = varDef.getGetterMethodPrefix()>
    public ${varDef.returnType} ${methodPrefix}${method}() <#if varDef.isGetterThrows()> ${varDef.getGetterThrowsSnippet()} </#if> {
<#-- custom attribute snippet - pre getter -->
<#if varDef.getCustomSnippet("PRE_GETTER")?has_content>
<#foreach snippet in varDef.getCustomSnippet("PRE_GETTER")![]>
        ${snippet}
</#foreach>
</#if>
<#-- custom attribute snippet - getter -->
<#if varDef.getCustomSnippet("GETTER")?has_content>
<#foreach snippet in varDef.getCustomSnippet("GETTER")![]>
    <#if snippet??>
        ${snippet}
    </#if>
</#foreach>
<#else>
    <#if varDef.getImplementationType()?has_content>
        if(${varDef.name} == null) {
            ${varDef.name} = new ${varDef.getImplementationType()}<>();
        }
    </#if>
        return ${varDef.returnValue};
</#if>
<#-- custom attribute snippet - post getter -->
<#if varDef.getCustomSnippet("POST_GETTER")?has_content>
<#foreach snippet in varDef.getCustomSnippet("POST_GETTER")![]>
        ${snippet}
</#foreach>
</#if>
    }

<#-- setter -->
<#if varDef.isSetterJavaDocExist()>
${varDef.getSetterJavaDoc()}
</#if>
<#foreach annotation in varDef.getAnnotation("SETTER")![]>
    ${annotation.getSnippet()}
</#foreach>
<#assign methodPrefix = varDef.getSetterMethodPrefix()>
    public void ${methodPrefix}${method}(${varDef.type} ${varDef.name}) <#if varDef.isSetterThrows()> ${varDef.getSetterThrowsSnippet()} </#if>{
<#-- custom attribute snippet - pre setter -->
<#if varDef.getCustomSnippet("PRE_SETTER")?has_content>
<#foreach snippet in varDef.getCustomSnippet("PRE_SETTER")![]>
        ${snippet}
</#foreach>
</#if>
<#-- custom attribute snippet - setter -->
<#if varDef.getCustomSnippet("SETTER")?has_content>
<#foreach snippet in varDef.getCustomSnippet("SETTER")![]>
<#if snippet??>
        ${snippet}
</#if>
</#foreach>
<#else>
        this.${varDef.name} = ${varDef.name};
</#if>
<#-- custom attribute snippet - post setter -->
<#if varDef.getCustomSnippet("POST_SETTER")?has_content>
<#foreach snippet in varDef.getCustomSnippet("POST_SETTER")![]>
        ${snippet}
</#foreach>
</#if>
    }

<#-- fluent -->
<#if fluentAPI>
<#if varDef.isFluentJavaDocExist()>
${varDef.getFluentJavaDoc()}
</#if>
<#foreach annotation in varDef.getAnnotation("FLUENT")![]>
    ${annotation.getSnippet()}
</#foreach>
<#assign fluentMethod = varDef.fluentMethodName>
    public ${classDef.getClassName()} ${fluentMethod}(${varDef.type} ${varDef.name}) {
<#-- custom attribute snippet - pre fluent -->
<#if varDef.getCustomSnippet("PRE_FLUENT")?has_content>
<#foreach snippet in varDef.getCustomSnippet("PRE_FLUENT")>
        ${snippet}
${n}</#foreach>
</#if>
<#-- custom attribute snippet - fluent -->
<#if varDef.getCustomSnippet("FLUENT")?has_content>
<#foreach snippet in varDef.getCustomSnippet("FLUENT")![]>
        ${snippet}
</#foreach>
<#else>
        this.${varDef.name} = ${varDef.name};
        return this;
</#if>
    }
</#if>
<#-- add/remove -->
<#if varDef.getImplementationType()?has_content>
    ${varDef.getHelperMethodSnippet()}
</#if>
<#-- custom attribute snippet - after method -->
<#if varDef.getCustomSnippet("AFTER_METHOD")?has_content>
<#foreach snippet in varDef.getCustomSnippet("AFTER_METHOD")![]>
    ${snippet}
${n}</#foreach>
</#if>
</#foreach>
<#-- custom class snippet - after method -->
<#if classDef.getCustomSnippet("AFTER_METHOD")?has_content>
<#foreach snippet in classDef.getCustomSnippet("AFTER_METHOD")![]>
    ${snippet}
${n}</#foreach>
</#if>
<#-- hashcode, equals and toString method -->
<#if classDef.getEqualsMethod()??>
    @Override
    public boolean equals(Object obj) {
        ${classDef.getEqualsMethod().getSnippet()}
    }
${n}</#if>
<#if classDef.getHashcodeMethod()??>
    @Override
    public int hashCode() {
        ${classDef.getHashcodeMethod().getSnippet()}
    }
${n}</#if>
<#if classDef.getToStringMethod()??>
    @Override
    public String toString() {
        return ${classDef.getToStringMethod().getSnippet()};
    }
${n}</#if>
<#-- custom class snippet - default -->
<#if classDef.getCustomSnippet("DEFAULT")?has_content>
<#foreach snippet in classDef.getCustomSnippet("DEFAULT")![]>
    ${snippet}
${n}</#foreach>
</#if>
}
<#-- custom class snippet - after class -->
<#if classDef.getCustomSnippet("AFTER_CLASS")?has_content>
<#foreach snippet in classDef.getCustomSnippet("AFTER_CLASS")![]>
${snippet} 
${n}</#foreach>
</#if>