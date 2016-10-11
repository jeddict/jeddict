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
package org.netbeans.db.modeler.specification.model.util;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toSet;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.eclipse.persistence.descriptors.DBRelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import static org.eclipse.persistence.exceptions.DescriptorException.MULTIPLE_WRITE_MAPPINGS_FOR_FIELD;
import static org.eclipse.persistence.exceptions.DescriptorException.NO_FOREIGN_KEYS_ARE_SPECIFIED;
import static org.eclipse.persistence.exceptions.DescriptorException.NO_MAPPING_FOR_PRIMARY_KEY;
import static org.eclipse.persistence.exceptions.DescriptorException.NO_SOURCE_RELATION_KEYS_SPECIFIED;
import static org.eclipse.persistence.exceptions.DescriptorException.NO_TARGET_FOREIGN_KEYS_SPECIFIED;
import static org.eclipse.persistence.exceptions.DescriptorException.TABLE_NOT_PRESENT;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.exceptions.ValidationException;
import static org.eclipse.persistence.exceptions.ValidationException.INCOMPLETE_JOIN_COLUMNS_SPECIFIED;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.netbeans.db.modeler.exception.DBConnectionNotFound;
import org.netbeans.db.modeler.exception.DBValidationException;
import static org.netbeans.jcode.core.util.StringHelper.getNext;
import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.db.accessor.EntitySpecAccessor;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.openide.windows.WindowManager;

/**
 *
 * @author Shiwani Gupta
 */
public class DeploymentExceptionManager {

    public static void handleException(ModelerFile file, Exception exception) throws ProcessInterruptedException {
        Boolean fixError = null;
//NO_SOURCE_RELATION_KEYS_SPECIFIED
        if (exception instanceof IntegrityException) {
            IntegrityException ie = (IntegrityException) exception;
            if (ie.getIntegrityChecker().getCaughtExceptions().get(0) instanceof DescriptorException) {
                DescriptorException de = (DescriptorException) ie.getIntegrityChecker().getCaughtExceptions().get(0);
                switch (de.getErrorCode()) {
                    case MULTIPLE_WRITE_MAPPINGS_FOR_FIELD:
                        //Issue fix : https://github.com/jGauravGupta/JPAModeler/issues/8 #Same Column name in CompositePK
                        if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                            DBRelationalDescriptor relationalDescriptor = (DBRelationalDescriptor) de.getDescriptor();
                            Entity entity = ((EntitySpecAccessor) relationalDescriptor.getAccessor()).getEntity();
                            Optional optional = file.getParentFile().getModelerScene().getBaseElements().stream().filter(be -> ((IBaseElementWidget) be).getBaseElementSpec() == entity).findAny();
                            if (optional.isPresent() && optional.get() instanceof PersistenceClassWidget) {
                                String attributeName = de.getMapping().getAttributeName();
                                if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "Multiple Same column name exist in entity " + entity.getClazz() + " for attribute " + attributeName
                                        + '\n' + " Would you like to override the column name automatically using @JoinColumn ?", "Error : Same column name exist in table", YES_NO_OPTION) == YES_NO_OPTION) {
                                    Set<String> allFields = relationalDescriptor.getAllFields().stream()
                                            .map(field -> field.getName().toUpperCase()).collect(toSet());
                      
                                    Attribute attribute = (Attribute) de.getMapping().getProperty(Attribute.class);
                                    if (attribute instanceof RelationAttribute) {
                                        List<JoinColumn> joinColumns = new ArrayList<>();
                                        for (DatabaseField databaseField : de.getMapping().getFields()) {
                                            if (databaseField.isPrimaryKey()) {
                                                JoinColumn joinColumn = new JoinColumn();
                                                //Issue fix : https://github.com/jGauravGupta/JPAModeler/issues/45 #Same Column name in table
                                              if(!databaseField.getName().startsWith( attributeName.toUpperCase() + "_")){
                                                String joinColumnName = (attributeName.toUpperCase() + "_" + databaseField.getName()).toUpperCase();
                                                joinColumnName = getNext(joinColumnName, nextJoinColumnName -> allFields.contains(nextJoinColumnName), false);
                                                joinColumn.setName(joinColumnName);
                                                joinColumn.setReferencedColumnName(databaseField.getName());
                                                joinColumns.add(joinColumn);
                                               } else {
                                                  //same join column name exist in table
                                                  joinColumns.clear();
                                                  break;
                                               }
                                            }
                                        }
                                        JPAModelerUtil.addDefaultJoinColumnForCompositePK((PersistenceClassWidget) optional.get(), attributeName, allFields, joinColumns);
                                    } else {
                                        
                                        if(de.getMapping().getFields().size() == 1){
                                            if(attribute instanceof PersistenceBaseAttribute){
                                                PersistenceBaseAttribute persistenceBaseAttribute = (PersistenceBaseAttribute)attribute;
                                                String columnName = de.getMapping().getFields().get(0).getName();
                                                String newColumnName = getNext(columnName, nextColumnName -> allFields.contains(nextColumnName));
                                                persistenceBaseAttribute.getColumn().setName(newColumnName);
                                            } else {
                                                System.out.println("");
                                            }
                                        } else {
                                            System.out.println("");
                                        }
                                        for (DatabaseField databaseField : de.getMapping().getFields()) {
                                            long count = relationalDescriptor.getAllFields().stream().map(field -> field.getName()).filter(fieldName -> fieldName.equals(databaseField.getName())).count();
                                            System.out.println("count : " + count);
                                        }
                                    }

                                    file.getModelerUtil().loadModelerFile(file);
                                    fixError = true;
                                } else {
                                    fixError = false;
                                }
                            }
                        }
                        break;
                    case NO_TARGET_FOREIGN_KEYS_SPECIFIED:
                        if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                            Entity entity = ((EntitySpecAccessor) ((DBRelationalDescriptor) de.getDescriptor()).getAccessor()).getEntity();
                            Optional optional = file.getParentFile().getModelerScene().getBaseElements().stream().filter(be -> ((IBaseElementWidget) be).getBaseElementSpec() == entity).findAny();
                            if (optional.isPresent() && optional.get() instanceof PersistenceClassWidget) {
                                String attributeName = de.getMapping().getAttributeName();
                                if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "No target foreign keys have been specified for this mapping. in entity " + entity.getClazz() + " for attribute " + attributeName
                                        + '\n' + " Would you like to override the column name automatically using @JoinColumn ?",
                                        "Error : No target foreign keys have been specified", YES_NO_OPTION) == YES_NO_OPTION) {
                                    JPAModelerUtil.removeDefaultJoinColumn((PersistenceClassWidget) optional.get(), attributeName);
                                    file.getModelerUtil().loadModelerFile(file);
                                    fixError = true;
                                } else {
                                    fixError = false;
                                }
                            }
                        }
                        break;
                    case NO_FOREIGN_KEYS_ARE_SPECIFIED:
                        if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                            Entity entity = ((EntitySpecAccessor) ((DBRelationalDescriptor) de.getDescriptor()).getAccessor()).getEntity();
                            Optional optional = file.getParentFile().getModelerScene().getBaseElements().stream().filter(be -> ((IBaseElementWidget) be).getBaseElementSpec() == entity).findAny();
                            if (optional.isPresent() && optional.get() instanceof PersistenceClassWidget) {
                                String attributeName = de.getMapping().getAttributeName();
                                if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "No foreign keys are specified for this mapping. in entity " + entity.getClazz() + " for attribute " + attributeName
                                        + '\n' + " Would you like to override the column name automatically using @JoinColumn ?",
                                        "Error : No foreign keys are specified", YES_NO_OPTION) == YES_NO_OPTION) {
                                    JPAModelerUtil.removeDefaultJoinColumn((PersistenceClassWidget) optional.get(), attributeName);
                                    file.getModelerUtil().loadModelerFile(file);
                                    fixError = true;
                                } else {
                                    fixError = false;
                                }
                            }
                        }
                        break;
                    case NO_SOURCE_RELATION_KEYS_SPECIFIED:
                        if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                            Entity entity = ((EntitySpecAccessor) ((DBRelationalDescriptor) de.getDescriptor()).getAccessor()).getEntity();
                            Optional optional = file.getParentFile().getModelerScene().getBaseElements().stream().filter(be -> ((IBaseElementWidget) be).getBaseElementSpec() == entity).findAny();
                            if (optional.isPresent() && optional.get() instanceof PersistenceClassWidget) {
                                String attributeName = de.getMapping().getAttributeName();
//                                fixError = false;//TODO
                            }
                        }
                        break;
                    case NO_MAPPING_FOR_PRIMARY_KEY:
                        if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                            Entity entity = ((EntitySpecAccessor) ((DBRelationalDescriptor) de.getDescriptor()).getAccessor()).getEntity();
                            Optional optional = file.getParentFile().getModelerScene().getBaseElements().stream().filter(be -> ((IBaseElementWidget) be).getBaseElementSpec() == entity).findAny();
                            if (optional.isPresent() && optional.get() instanceof EntityWidget) {
                                if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                                        de.getMessage() + '\n' + " Would you like to fix it automatically using @Column(insertable=true) ?",
                                        "Error : No non-read mapping found for Entity " + entity.getClazz(), YES_NO_OPTION) == YES_NO_OPTION) {
                                    EntityWidget entityWidget = (EntityWidget) optional.get();
                                    entityWidget.getAllIdAttributeWidgets().stream().filter(idAttrWidget -> !idAttrWidget.getBaseElementSpec().getColumn().getInsertable())
                                            .forEach(idAttrWidget -> idAttrWidget.getBaseElementSpec().getColumn().setInsertable(true));
                                    file.getModelerUtil().loadModelerFile(file);
                                    fixError = true;
                                } else {
                                    fixError = false;
                                }
                            }
                        }
                        break;
                    case TABLE_NOT_PRESENT:
                        //TODO fix for @PrimaryKeyJoinColumn on invalid referenceColumn
                        if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                            Entity entity = ((EntitySpecAccessor) ((DBRelationalDescriptor) de.getDescriptor()).getAccessor()).getEntity();
                            Matcher matcher = Pattern.compile("\\[(.+?)\\]").matcher(de.getMessage());
                            String tableName = matcher.find() ? matcher.group(1) : "";
                            showErrorMessage("Error : @Table or @SecondaryTable missing",
                                    " @Column(table=\"" + tableName + "\") annotation defined on attributes but" + '\n'
                                    + " the @Table or @SecondaryTable is not found on Entity " + entity.getClazz() + "?");
                            fixError = false;
                        }
                        break;
                    default:
                        System.out.println("NF");
                        break;
                }
            }

        } else if (exception instanceof DBValidationException) {
            DBValidationException validationException = (DBValidationException) exception;
            if (INCOMPLETE_JOIN_COLUMNS_SPECIFIED == validationException.getErrorCode()) {
                Attribute attribute = validationException.getAttribute();
                JavaClass javaClass = attribute.getJavaClass();
                Optional optional = file.getParentFile().getModelerScene().getBaseElements().stream().filter(be -> ((IBaseElementWidget) be).getBaseElementSpec() == javaClass).findAny();
                if (optional.isPresent() && optional.get() instanceof PersistenceClassWidget) {
                    String attributeName = attribute.getName();
                    if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "entity " + javaClass.getClazz() + " for attribute " + attributeName
                            + '\n' + " Would like to reconstruct the column name automatically using @JoinColumn ?",
                            "Error : ----", YES_NO_OPTION) == YES_NO_OPTION) {
                        if (attribute instanceof JoinColumnHandler) {
                            JoinColumnHandler columnHandler = (JoinColumnHandler) attribute;
                            columnHandler.getJoinColumn().clear();
                            file.getModelerUtil().loadModelerFile(file);
                            fixError = true;
                        } else {
                            fixError = false;
                        }

//                                JPAModelerUtil.addDefaultJoinColumnForCompositePK((PersistenceClassWidget) optional.get(), attributeName);
                    }
                }
            }

        } else if (exception instanceof DBConnectionNotFound || exception instanceof org.eclipse.persistence.exceptions.DatabaseException) {
            EntityMappings entityMapping = (EntityMappings) file.getAttributes().get(EntityMappings.class.getSimpleName());
            if (entityMapping.getCache().getDatabaseConnectionCache() != null) {
                entityMapping.getCache().setDatabaseConnection(null);
                ((DBModelerUtil) file.getModelerUtil()).loadModelerFile(file);
                fixError = true;
            }
        } else if (exception instanceof ValidationException) {
            ValidationException validationException = (ValidationException) exception;
            if (validationException.getErrorCode() == INCOMPLETE_JOIN_COLUMNS_SPECIFIED) {
                showErrorMessage("Incomplete Join Column Specified", validationException.getMessage());
                fixError = false;
            }
        }

        if (fixError == null) {
            exception.printStackTrace();
            showException(exception, file);
        } else if (!fixError) {
            exception.printStackTrace();
            file.getModelerPanelTopComponent().close();
        }

    }

    private static void showException(Exception exception, ModelerFile file) {
        String message = exception.getLocalizedMessage();
        file.getModelerPanelTopComponent().close();
        if (message == null) {
            ExceptionUtils.printStackTrace(exception.getClass().getName(), exception, file);
        } else {
            int end = message.lastIndexOf("Runtime Exceptions:");
            end = end < 1 ? message.length() : end;
            int start = message.lastIndexOf("Exception Description:");
            start = start < 1 ? 0 : start;
            ExceptionUtils.printStackTrace(message.substring(start, end), exception, file);
        }
    }

    private static void showErrorMessage(String title, String message) {
        JTextArea jTextArea = new JTextArea(message);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jScrollPane.setPreferredSize(new Dimension(600, 200));
        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),
                jScrollPane, title, ERROR_MESSAGE);
    }

}
