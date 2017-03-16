/**
 * Copyright [2017] Gaurav Gupta
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
import static org.eclipse.persistence.exceptions.ValidationException.CONVERTER_CLASS_NOT_FOUND;
import static org.eclipse.persistence.exceptions.ValidationException.INCOMPLETE_JOIN_COLUMNS_SPECIFIED;
import static org.eclipse.persistence.exceptions.ValidationException.INVALID_DERIVED_ID_PRIMARY_KEY_FIELD;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.netbeans.db.modeler.exception.DBConnectionNotFound;
import org.netbeans.db.modeler.exception.DBValidationException;
import static org.netbeans.jcode.core.util.StringHelper.getNext;
import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.netbeans.jpa.modeler.db.accessor.EntitySpecAccessor;
import org.netbeans.jpa.modeler.spec.*;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.SingleRelationAttribute;
import org.netbeans.jpa.modeler.specification.model.util.DBUtil;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import static org.openide.util.NbBundle.getMessage;
import org.openide.windows.WindowManager;

/**
 *
 * @author Shiwani Gupta
 */
public class DeploymentExceptionManager {

    public static void handleException(ModelerFile file, Throwable throwable) throws ProcessInterruptedException {
        Boolean fixError = null;
        if (throwable instanceof IntegrityException) {
            fixError = handleIntegrityException((IntegrityException) throwable, file);
        } else if (throwable instanceof DBValidationException) {
            fixError = handleDBValidationException((DBValidationException) throwable, file);
        } else if (throwable instanceof DBConnectionNotFound || throwable instanceof org.eclipse.persistence.exceptions.DatabaseException) {
            EntityMappings entityMapping = (EntityMappings) file.getAttributes().get(EntityMappings.class.getSimpleName());
            if (entityMapping.getCache().getDatabaseConnectionCache() != null) {
                entityMapping.getCache().setDatabaseConnection(null);
                ((DBModelerUtil) file.getModelerUtil()).loadModelerFile(file);
                fixError = true;
            }
        } else if (throwable instanceof ValidationException) {
            ValidationException validationException = (ValidationException) throwable;
            if (validationException.getErrorCode() == INCOMPLETE_JOIN_COLUMNS_SPECIFIED) {
                showErrorMessage("Incomplete Join Column Specified", validationException.getMessage());
                fixError = false;
            } else if (validationException.getErrorCode() == CONVERTER_CLASS_NOT_FOUND) {
                showErrorMessage("Register Converter", validationException.getMessage()
                        .replace("persistence unit definition", "(Diagram > Properties > Converters)"));
                fixError = false;
            }
        } else if (throwable instanceof NoClassDefFoundError) {
            NoClassDefFoundError error = (NoClassDefFoundError) throwable;
            showErrorMessage(getMessage(DeploymentExceptionManager.class, "CLASS_NOT_FOUND"),
                    getMessage(DeploymentExceptionManager.class, "ADD_DEPENDENCY", error.getMessage().replace('/', '.')));
            fixError = false;
        }

        if (fixError == null) {
            showException(throwable, file);
            throw new ProcessInterruptedException(throwable.getMessage());
        } else if (!fixError) {
            throw new ProcessInterruptedException(throwable.getMessage());
        } else {
            throw new ProcessInterruptedException(throwable.getMessage(), () -> {
                ModelerFile parentFile = file.getParentFile();
                DBUtil.openDBViewer(parentFile, (EntityMappings) parentFile.getModelerScene().getBaseElementSpec());
            });
        }

    }

    private static void showException(Throwable throwable, ModelerFile file) {
        String message = throwable.getLocalizedMessage();
        if (message == null) {
            ExceptionUtils.printStackTrace(throwable.getClass().getName(), throwable, file);
        } else {
            int end = message.lastIndexOf("Runtime Exceptions:");
            end = end < 1 ? message.length() : end;
            int start = message.lastIndexOf("Exception Description:");
            start = start < 1 ? 0 : start;
            ExceptionUtils.printStackTrace(message.substring(start, end), throwable, file);
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

    private static Boolean handleDBValidationException(DBValidationException validationException, ModelerFile file) throws ProcessInterruptedException {
        Boolean fixError = null;
        Attribute attribute = validationException.getAttribute();
        JavaClass javaClass = attribute.getJavaClass();
        String attrDetail = javaClass.getClass().getSimpleName().toLowerCase() + " " + javaClass.getClazz() + " for attribute " + attribute.getName();
        if (INCOMPLETE_JOIN_COLUMNS_SPECIFIED == validationException.getErrorCode()) {//reconstruct join column
            //e.g : https://github.com/jGauravGupta/JPAModeler/issues/67
            if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                    getMessage(DeploymentExceptionManager.class, "MSG_INCOMPLETE_JOIN_COLUMNS_SPECIFIED", attrDetail),
                    "Error", YES_NO_OPTION) == YES_NO_OPTION) {
                if (attribute instanceof RelationAttribute) {
                    if (attribute instanceof JoinColumnHandler) {
                        JoinColumnHandler columnHandler = (JoinColumnHandler) attribute;
                        columnHandler.getJoinColumn().clear();
                    }
                    JoinTable joinTable = ((RelationAttribute) attribute).getJoinTable();
                    joinTable.getJoinColumn().clear();
                    joinTable.getInverseJoinColumn().clear();
                    file.getModelerUtil().loadModelerFile(file);
                    fixError = true;
                } else {
                    fixError = false;
                }
            }
        } else if (INVALID_DERIVED_ID_PRIMARY_KEY_FIELD == validationException.getErrorCode()) {
            // If there is no primary key accessor then the user must have
            // specified an incorrect reference column name. Throw an exception.
            //Ref : https://github.com/jGauravGupta/JPAModeler/issues/164
            if (attribute instanceof SingleRelationAttribute) {
                SingleRelationAttribute relationAttribute = (SingleRelationAttribute) attribute;
                if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                        getMessage(DeploymentExceptionManager.class, "MSG_INVALID_DERIVED_ID_PRIMARY_KEY_FIELD", attrDetail),
                        getMessage(DeploymentExceptionManager.class, "TITLE_INVALID_DERIVED_ID_PRIMARY_KEY_FIELD"),
                        YES_NO_OPTION) == YES_NO_OPTION) {
                    relationAttribute.getJoinColumn().clear();
                    relationAttribute.getJoinTable().getJoinColumn().clear();
                    relationAttribute.getJoinTable().getInverseJoinColumn().clear();
                    fixError = true;
                } else {
                    fixError = false;
                }
            }
        }
        return fixError;
    }

    private static Boolean handleIntegrityException(IntegrityException ie, ModelerFile file) throws ProcessInterruptedException {
        Boolean fixError = null;
        if (ie.getIntegrityChecker().getCaughtExceptions().get(0) instanceof DescriptorException) {
            DescriptorException de = (DescriptorException) ie.getIntegrityChecker().getCaughtExceptions().get(0);
            Entity entity = ((EntitySpecAccessor) ((DBRelationalDescriptor) de.getDescriptor()).getAccessor()).getEntity();
            String attributeName = de.getMapping().getAttributeName();
            String attrDetail = entity.getClass().getSimpleName().toLowerCase() + " " + entity.getClazz() + " for attribute " + attributeName;

            switch (de.getErrorCode()) {
                case MULTIPLE_WRITE_MAPPINGS_FOR_FIELD:
                    //Issue fix : https://github.com/jGauravGupta/JPAModeler/issues/8 #Same Column name in CompositePK
                    if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                        DBRelationalDescriptor relationalDescriptor = (DBRelationalDescriptor) de.getDescriptor();
                        if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                                getMessage(DeploymentExceptionManager.class, "MSG_MULTIPLE_WRITE_MAPPINGS_FOR_FIELD", attrDetail)
                                + getMessage(DeploymentExceptionManager.class, "OVERRIDE_JOIN_COLUMN"),
                                getMessage(DeploymentExceptionManager.class, "TITLE_MULTIPLE_WRITE_MAPPINGS_FOR_FIELD"),
                                YES_NO_OPTION) == YES_NO_OPTION) {
                            Set<String> allFields = relationalDescriptor.getAllFields()
                                    .stream()
                                    .map(field -> field.getName().toUpperCase())
                                    .collect(toSet());
                            Attribute attribute = (Attribute) de.getMapping().getProperty(Attribute.class);
                            if (attribute instanceof RelationAttribute) {
                                List<JoinColumn> joinColumns = new ArrayList<>();
                                for (DatabaseField databaseField : de.getMapping().getFields()) {
                                    if (databaseField.isPrimaryKey()) {
                                        JoinColumn joinColumn = new JoinColumn();
                                        //Issue fix : https://github.com/jGauravGupta/JPAModeler/issues/45 #Same Column name in table
                                        if (!databaseField.getName().startsWith(attributeName.toUpperCase() + "_")) {
                                            String joinColumnName = (attributeName.toUpperCase() + "_" + databaseField.getName()).toUpperCase();
                                            joinColumnName = getNext(joinColumnName, nextJoinColumnName -> allFields.contains(nextJoinColumnName));
                                            joinColumn.setName(joinColumnName);
                                            joinColumn.setReferencedColumnName(databaseField.getName());
                                            joinColumns.add(joinColumn);
                                        } else {
                                            //same join column name exist in table
                                            //if basic column name is same as join column
                                            joinColumns.clear();
                                            break;
                                        }
                                    }
                                }
                                JPAModelerUtil.addDefaultJoinColumnForCompositePK(entity, attributeName, allFields, joinColumns);
                            } else {
                                if (de.getMapping().getFields().size() == 1) {
                                    if (attribute instanceof PersistenceBaseAttribute) {
                                        PersistenceBaseAttribute persistenceBaseAttribute = (PersistenceBaseAttribute) attribute;
                                        String columnName = de.getMapping().getFields().get(0).getName();
                                        String newColumnName = getNext(columnName, nextColumnName -> allFields.contains(nextColumnName));
                                        persistenceBaseAttribute.getColumn().setName(newColumnName);
                                    } else {
                                        fixError = false;
                                        break;
                                    }
                                } else {
                                    fixError = false;
                                    break;
                                }
                            }
                            file.getModelerUtil().loadModelerFile(file);
                            fixError = true;
                        } else {
                            fixError = false;
                        }
                    }
                    break;
                case NO_TARGET_FOREIGN_KEYS_SPECIFIED:
                    if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                        if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                                getMessage(DeploymentExceptionManager.class, "MSG_NO_TARGET_FOREIGN_KEYS_SPECIFIED", attrDetail)
                                + getMessage(DeploymentExceptionManager.class, "OVERRIDE_JOIN_COLUMN"),
                                getMessage(DeploymentExceptionManager.class, "TITLE_NO_TARGET_FOREIGN_KEYS_SPECIFIED"),
                                YES_NO_OPTION) == YES_NO_OPTION) {
                            JPAModelerUtil.removeDefaultJoinColumn(entity, attributeName);
                            file.getModelerUtil().loadModelerFile(file);
                            fixError = true;
                        } else {
                            fixError = false;
                        }
                    }
                    break;
                case NO_FOREIGN_KEYS_ARE_SPECIFIED:
                    if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                        //todo MappedSuperClass support
                        if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                                getMessage(DeploymentExceptionManager.class, "MSG_NO_FOREIGN_KEYS_ARE_SPECIFIED", attrDetail)
                                + getMessage(DeploymentExceptionManager.class, "OVERRIDE_JOIN_COLUMN"),
                                getMessage(DeploymentExceptionManager.class, "TITLE_NO_FOREIGN_KEYS_ARE_SPECIFIED"),
                                YES_NO_OPTION) == YES_NO_OPTION) {
                            JPAModelerUtil.removeDefaultJoinColumn(entity, attributeName);
                            file.getModelerUtil().loadModelerFile(file);
                            fixError = true;
                        } else {
                            fixError = false;
                        }
                    }
                    break;
                case NO_SOURCE_RELATION_KEYS_SPECIFIED:
                    if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
//                                fixError = false;//TODO
                    }
                    break;
                case NO_MAPPING_FOR_PRIMARY_KEY:
                    if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                        if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                                getMessage(DeploymentExceptionManager.class, "MSG_NO_MAPPING_FOR_PRIMARY_KEY"),
                                getMessage(DeploymentExceptionManager.class, "TITLE_NO_MAPPING_FOR_PRIMARY_KEY", entity.getClazz()),
                                YES_NO_OPTION) == YES_NO_OPTION) {
                            entity.getAttributes().getSuperId()
                                    .stream()
                                    .filter(idAttr -> !idAttr.getColumn().getInsertable())
                                    .forEach(idAttr -> idAttr.getColumn().setInsertable(true));
                            file.getModelerUtil().loadModelerFile(file);
                            fixError = true;
                        } else {
                            fixError = false;
                        }
                    }
                    break;
                case TABLE_NOT_PRESENT:
                    //TODO fix for @PrimaryKeyJoinColumn on invalid referenceColumn
                    if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                        Matcher matcher = Pattern.compile("\\[(.+?)\\]").matcher(de.getMessage());
                        String tableName = matcher.find() ? matcher.group(1) : "";
                        showErrorMessage(getMessage(DeploymentExceptionManager.class, "TITLE_TABLE_NOT_PRESENT"),
                                getMessage(DeploymentExceptionManager.class, "MSG_TABLE_NOT_PRESENT", tableName, entity.getClazz()));
                        fixError = false;
                    }
                    break;
                default:
                    break;
            }
        }

        return fixError;
    }

}
