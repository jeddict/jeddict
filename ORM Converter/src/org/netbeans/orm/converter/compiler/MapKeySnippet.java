/**
 * Copyright [2016] Gaurav Gupta
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
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_FQN;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import static org.netbeans.orm.converter.util.ORMConverterUtil.QUOTE;

public class MapKeySnippet implements Snippet {

    
    //Existing MapKeyType
     private Attribute mapKeyAttribute;
     
    private final ClassHelper mapKeyAttributeType = new ClassHelper();

    //New MapKeyType - Basic
    private ColumnDefSnippet columnSnippet;
    private TemporalSnippet temporalSnippet;
    private EnumeratedSnippet enumeratedSnippet;
    
    //New MapKeyType - Entity
    private JoinColumnsSnippet joinColumnsSnippet;
            
    //New MapKeyType - Embeddable
    private AttributeOverridesSnippet attributeOverrideSnippet;//REVENG PENDING
     
         
    @Override
    public String getSnippet() throws InvalidDataException {

//        if (mappedBy == null
//                && getTargetEntity() == null
//                && getFetchType() == null
//                && getCascadeTypes().isEmpty() && mapKeyAttribute==null) {
//            return "@"+getType();
//        }

        StringBuilder builder = new StringBuilder();
        if (mapKeyAttribute != null) {
           builder.append('@').append(MAP_KEY);
           builder.append(ORMConverterUtil.OPEN_PARANTHESES);
           builder.append("name = ");
           builder.append(QUOTE).append(mapKeyAttribute.getName()).append(QUOTE);
           builder.append(ORMConverterUtil.CLOSE_PARANTHESES);
        } else {
             if (columnSnippet != null) {
                builder.append(columnSnippet.getSnippet()).append("\n");
            } else if (temporalSnippet != null) {
                builder.append(temporalSnippet.getSnippet()).append("\n");
            } else if (enumeratedSnippet != null) {
                builder.append(enumeratedSnippet.getSnippet()).append("\n");
            } else if (joinColumnsSnippet!= null) {
                builder.append(joinColumnsSnippet.getSnippet()).append("\n");
            } else if (attributeOverrideSnippet!= null) {
                builder.append(attributeOverrideSnippet.getSnippet()).append("\n");
            }
        }
        
        return builder.toString();
    }

    @Override
    public List<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        if (mapKeyAttribute != null) {
            importSnippets.add(MAP_KEY_FQN);
        } else {
             if (columnSnippet != null) {
                 importSnippets.addAll(columnSnippet.getImportSnippets());
            } else if (temporalSnippet != null) {
                 importSnippets.addAll(temporalSnippet.getImportSnippets());
            } else if (enumeratedSnippet != null) {
                 importSnippets.addAll(enumeratedSnippet.getImportSnippets());
            } else if (joinColumnsSnippet!= null) {
                 importSnippets.addAll(joinColumnsSnippet.getImportSnippets());
            } else if (attributeOverrideSnippet!= null) {
                 importSnippets.addAll(attributeOverrideSnippet.getImportSnippets());
            }
        }
        return importSnippets;
    }
    public boolean isEmpty(){
        return false;
    }
           /**
     * @return the mapKeyAttribute
     */
    public Attribute getMapKeyAttribute() {
        return mapKeyAttribute;
    }

    /**
     * @param mapKeyAttribute the mapKeyAttribute to set
     */
//    @Override
    public void setMapKeyAttribute(Attribute mapKeyAttribute) {
        this.mapKeyAttribute = mapKeyAttribute;
    }
    
     /**
     * @return the temporalSnippet
     */
    public TemporalSnippet getTemporalSnippet() {
        return temporalSnippet;
    }

    /**
     * @param temporalSnippet the temporalSnippet to set
     */
    public void setTemporalSnippet(TemporalSnippet temporalSnippet) {
        this.temporalSnippet = temporalSnippet;
    }

    /**
     * @return the enumeratedSnippet
     */
    public EnumeratedSnippet getEnumeratedSnippet() {
        return enumeratedSnippet;
    }

    /**
     * @param enumeratedSnippet the enumeratedSnippet to set
     */
    public void setEnumeratedSnippet(EnumeratedSnippet enumeratedSnippet) {
        this.enumeratedSnippet = enumeratedSnippet;
    }

    /**
     * @return the joinColumnsSnippet
     */
    public JoinColumnsSnippet getJoinColumnsSnippet() {
        return joinColumnsSnippet;
    }

    /**
     * @param joinColumnsSnippet the joinColumnsSnippet to set
     */
    public void setJoinColumnsSnippet(JoinColumnsSnippet joinColumnsSnippet) {
        this.joinColumnsSnippet = joinColumnsSnippet;
    }


    public void setMapKeyAttributeType(String type) {
        this.getMapKeyAttributeType().setClassName(type);
    }

    /**
     * @return the mapKeyColumnSnippet
     */
    public ColumnDefSnippet getColumnSnippet() {
        return columnSnippet;
    }

    /**
     * @param mapKeyColumnSnippet the mapKeyColumnSnippet to set
     */
    public void setColumnSnippet(ColumnDefSnippet mapKeyColumnSnippet) {
        this.columnSnippet = mapKeyColumnSnippet;
    }

    /**
     * @return the mapKeyAttributeOverrideSnippet
     */
    public AttributeOverridesSnippet getAttributeOverrideSnippet() {
        return attributeOverrideSnippet;
    }

    /**
     * @param attributeOverridesSnippet the mapKeyAttributeOverrideSnippet to set
     */
    public void setAttributeOverrideSnippet(AttributeOverridesSnippet attributeOverridesSnippet) {
        this.attributeOverrideSnippet = attributeOverridesSnippet;
    }

    /**
     * @return the mapKeyAttributeType
     */
    public ClassHelper getMapKeyAttributeType() {
        return mapKeyAttributeType;
    }


}
