/**
 * Copyright [2014] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class VariableDefSnippet implements Snippet {

    public static final String TEMPORAL_DATE = "TemporalType.DATE";
    public static final String TEMPORAL_TIME = "TemporalType.TIME";
    public static final String TEMPORAL_TIMESTAMP = "TemporalType.TIMESTAMP";

    private static final List<String> temporalTypes = getTemporalTypes();

    private boolean autoGenerate = false;
    private boolean embedded = false;
    private boolean embeddedId = false;
    private boolean lob = false;
    private boolean primaryKey = false;
    private boolean temporal = false;
    private boolean tranzient = false;
    private boolean version = false;

    private String name = null;
    private ClassHelper classHelper = new ClassHelper();
    //TODO: See if these 2 can be as a class
    private String mapKey = null;
    private String temporalType = null;

    private BasicSnippet basic = null;
    private ElementCollectionSnippet elementCollection = null;

    private ColumnDefSnippet columnDef = null;
    private RelationDefSnippet relationDef = null;
    private OrderBySnippet orderBy = null;
    private JoinColumnsSnippet joinColumns = null;
    private JoinTableSnippet joinTable = null;
    private CollectionTableSnippet collectionTable = null;
    private GeneratedValueSnippet generatedValue = null;
    private TableGeneratorSnippet tableGenerator = null;
    private SequenceGeneratorSnippet sequenceGenerator = null;
    private EnumeratedSnippet enumerated = null;

    private TypeIdentifierSnippet typeIdentifier = null;

    private static List<String> getTemporalTypes() {
        List<String> temporalTypesList = new ArrayList<String>();

        temporalTypesList.add(TEMPORAL_DATE);
        temporalTypesList.add(TEMPORAL_TIME);
        temporalTypesList.add(TEMPORAL_TIMESTAMP);

        return temporalTypesList;
    }

    public BasicSnippet getBasic() {
        return basic;
    }

    public void setBasic(BasicSnippet basic) {
        this.basic = basic;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public boolean isEmbeddedId() {
        return embeddedId;
    }

    public void setEmbeddedId(boolean embeddedId) {
        this.embeddedId = embeddedId;
    }

    public boolean isLob() {
        return lob;
    }

    public void setLob(boolean lob) {
        this.lob = lob;
    }

    public String getType() {//Modified : Collection => Collection<Entity>
        if (this.getTypeIdentifier() != null) { //Collection<Entity> , Collection<String>
            return this.getTypeIdentifier().getVariableType();
        } else {
            return classHelper.getClassName();
        }
    }

    public void setType(String type) {
        classHelper.setClassName(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnDefSnippet getColumnDef() {
        return columnDef;
    }

    public void setColumnDef(ColumnDefSnippet columnDef) {
        this.columnDef = columnDef;
    }

    public boolean isTranzient() {
        return tranzient;
    }

    public void setTranzient(boolean tranzient) {
        this.tranzient = tranzient;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isAutoGenerate() {
        return autoGenerate;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }

    /**
     * @return the a derived methodName from variableName Eg nickname -->
     * getNickname()
     */
    public String getMethodName() {
        char ch = Character.toUpperCase(name.charAt(0));

        return Character.toString(ch) + name.substring(1);
    }

//    public String getGetterName() {
//        char ch = Character.toUpperCase(name.charAt(0));
//
//        String type = "get";
//        if ("boolean".equals(this.getType())) {
//            type = "is";
//        }
//
//        return type + Character.toString(ch) + name.substring(1);
//    }
    public RelationDefSnippet getRelationDef() {
        return relationDef;
    }

    public void setRelationDef(RelationDefSnippet relationType) {
        this.relationDef = relationType;
    }

    public boolean isTemporal() {
        return temporal;
    }

    public void setTemporal(boolean temporal) {
        this.temporal = temporal;
    }

    public String getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(String temporalType) {

        if (!temporalTypes.contains(temporalType)) {
            throw new IllegalArgumentException("Given type : " + temporalType
                    + " Valid temporal types : " + temporalTypes);
        }

        this.temporalType = temporalType;
    }

    public String getTemporalTypeString() {

        if (temporalType == null) {
            return "@Temporal";
        }
        return "@Temporal(" + temporalType + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public OrderBySnippet getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBySnippet orderBy) {
        this.orderBy = orderBy;
    }

    public JoinColumnsSnippet getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(JoinColumnsSnippet joinColumns) {
        this.joinColumns = joinColumns;
    }

    public JoinTableSnippet getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(JoinTableSnippet joinTable) {
        this.joinTable = joinTable;
    }

    public GeneratedValueSnippet getGeneratedValue() {
        return generatedValue;
    }

    public void setGeneratedValue(GeneratedValueSnippet generatedValue) {
        this.generatedValue = generatedValue;
    }

    public TableGeneratorSnippet getTableGenerator() {
        return tableGenerator;
    }

    public void setTableGenerator(TableGeneratorSnippet tableGenerator) {
        this.tableGenerator = tableGenerator;
    }

    public SequenceGeneratorSnippet getSequenceGenerator() {
        return sequenceGenerator;
    }

    public void setSequenceGenerator(SequenceGeneratorSnippet sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    public EnumeratedSnippet getEnumerated() {
        return enumerated;
    }

    public void setEnumerated(EnumeratedSnippet enumerated) {
        this.enumerated = enumerated;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

    public String getMapKeyString() {

        if (mapKey == null) {
            return "@MapKey";
        }

        return "@MapKey(name=\"" + mapKey + ORMConverterUtil.QUOTE + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public TypeIdentifierSnippet getTypeIdentifier() {
        return typeIdentifier;
    }

    public String getSnippet() throws InvalidDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        Collection<String> importSnippets = new ArrayList<String>();

        if (classHelper.getClassName() == null) {
            typeIdentifier = new TypeIdentifierSnippet(this);

            classHelper.setClassName(typeIdentifier.getVariableType());

            importSnippets.addAll(typeIdentifier.getImportSnippets());
        } else {

            if (classHelper.getPackageName() != null) {
                importSnippets.add(classHelper.getFQClassName());
            }
        }

        if (basic != null) {
            importSnippets.addAll(basic.getImportSnippets());
        }
        if (elementCollection != null) {
            importSnippets.addAll(elementCollection.getImportSnippets());
        }

        if (columnDef != null) {
            importSnippets.addAll(columnDef.getImportSnippets());
        }

        if (relationDef != null) {
            importSnippets.addAll(relationDef.getImportSnippets());
        }

        if (orderBy != null) {
            importSnippets.addAll(orderBy.getImportSnippets());
        }

        if (joinColumns != null) {
            importSnippets.addAll(joinColumns.getImportSnippets());
        }

        if (joinTable != null) {
            importSnippets.addAll(joinTable.getImportSnippets());
        }

        if (collectionTable != null) {
            importSnippets.addAll(collectionTable.getImportSnippets());
        }

        if (generatedValue != null) {
            importSnippets.addAll(generatedValue.getImportSnippets());
        }

        if (tableGenerator != null) {
            importSnippets.addAll(tableGenerator.getImportSnippets());
        }

        if (sequenceGenerator != null) {
            importSnippets.addAll(sequenceGenerator.getImportSnippets());
        }

        if (enumerated != null) {
            importSnippets.addAll(enumerated.getImportSnippets());
        }

        /*try {
         Field fields[] = getClass().getDeclaredFields();

         for (Field field : fields) {
         Type[] interfaces = field.getType().getGenericInterfaces();
         for (Type interfaze : interfaces) {
         if(interfaze.getName())
         }
         }
         } catch (Exception e) {
         e.printStackTrace();
         }*/
        if (temporal) {
            importSnippets.add("javax.persistence.Temporal");

            if (temporalType != null) {
                importSnippets.add("javax.persistence.TemporalType");
            }
        }

        if (mapKey != null) {
            importSnippets.add("javax.persistence.MapKey");
        }

        if (autoGenerate) {
            importSnippets.add("javax.persistence.GenerateType");
            importSnippets.add("javax.persistence.GenerateValue");
        }

        if (elementCollection != null) {
            importSnippets.add("javax.persistence.ElementCollection");
        }
        if (embedded) {
            importSnippets.add("javax.persistence.Embedded");
        }

        if (embeddedId) {
            importSnippets.add("javax.persistence.EmbeddedId");
        }

        if (lob) {
            importSnippets.add("javax.persistence.Lob");
        }

        if (primaryKey) {
            importSnippets.add("javax.persistence.Id");
        }

        if (tranzient) {
            importSnippets.add("javax.persistence.Transient");
        }

        if (version) {
            importSnippets.add("javax.persistence.Version");
        }

//        if (importSnippets.contains("java.util.Date")) {  //BUG : remove date
//            importSnippets.remove("java.util.Date");
//        }
//        if (importSnippets.contains("java.lang.String")) {  //BUG : remove String
//            importSnippets.remove("java.lang.String");
//        }
//        if (importSnippets.contains("java.lang.Boolean")) {  //BUG : remove String
//            importSnippets.remove("java.lang.Boolean");
//        }
//        if (importSnippets.contains("java.lang.Byte")) {  //BUG : remove String
//            importSnippets.remove("java.lang.Byte");
//        }
//        if (importSnippets.contains("java.lang.Integer")) {  //BUG : remove String
//            importSnippets.remove("java.lang.Integer");
//        }
        return importSnippets;
    }

    /**
     * @return the elementCollection
     */
    public ElementCollectionSnippet getElementCollection() {
        return elementCollection;
    }

    /**
     * @param elementCollection the elementCollection to set
     */
    public void setElementCollection(ElementCollectionSnippet elementCollection) {
        this.elementCollection = elementCollection;
    }

//    public boolean isPremitive() {
//        String type = getType();
//        if ("boolean".equals(type) || "byte".equals(type)
//                || "short".equals(type) || "char".equals(type)
//                || "int".equals(type) || "long".equals(type)
//                || "float".equals(type) || "double".equals(type)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    /**
     * @return the collectionTable
     */
    public CollectionTableSnippet getCollectionTable() {
        return collectionTable;
    }

    /**
     * @param collectionTable the collectionTable to set
     */
    public void setCollectionTable(CollectionTableSnippet collectionTable) {
        this.collectionTable = collectionTable;
    }
}
