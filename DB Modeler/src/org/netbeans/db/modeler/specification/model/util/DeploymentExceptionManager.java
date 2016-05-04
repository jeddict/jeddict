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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import org.eclipse.persistence.descriptors.DBRelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import static org.eclipse.persistence.exceptions.DescriptorException.MULTIPLE_WRITE_MAPPINGS_FOR_FIELD;
import static org.eclipse.persistence.exceptions.DescriptorException.NO_FOREIGN_KEYS_ARE_SPECIFIED;
import static org.eclipse.persistence.exceptions.DescriptorException.NO_MAPPING_FOR_PRIMARY_KEY;
import static org.eclipse.persistence.exceptions.DescriptorException.NO_TARGET_FOREIGN_KEYS_SPECIFIED;
import org.eclipse.persistence.exceptions.IntegrityException;
import static org.eclipse.persistence.exceptions.ValidationException.INCOMPLETE_JOIN_COLUMNS_SPECIFIED;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.netbeans.db.modeler.exception.DBConnectionNotFound;
import org.netbeans.db.modeler.exception.DBValidationException;
import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.db.accessor.EntitySpecAccessor;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
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

//    private static Map<Integer,CheckedBiFunction<ModelerFile, Exception>> errorHandler = new HashMap<>(); //CheckedBiFunction not available in Java8
    public static void handleException(ModelerFile file, Exception exception) throws ProcessInterruptedException {
        boolean throwError = true;

        if (exception instanceof IntegrityException) {
            IntegrityException ie = (IntegrityException) exception;
            if (ie.getIntegrityChecker().getCaughtExceptions().get(0) instanceof DescriptorException) {
                DescriptorException de = (DescriptorException) ie.getIntegrityChecker().getCaughtExceptions().get(0);
                if (MULTIPLE_WRITE_MAPPINGS_FOR_FIELD == de.getErrorCode()) {
                    if (de.getDescriptor() instanceof DBRelationalDescriptor && ((DBRelationalDescriptor) de.getDescriptor()).getAccessor() instanceof EntitySpecAccessor) {
                        Entity entity = ((EntitySpecAccessor) ((DBRelationalDescriptor) de.getDescriptor()).getAccessor()).getEntity();
                        Optional optional = file.getParentFile().getModelerScene().getBaseElements().stream().filter(be -> ((IBaseElementWidget) be).getBaseElementSpec() == entity).findAny();
                        if (optional.isPresent() && optional.get() instanceof PersistenceClassWidget) {
                            String attributeName = de.getMapping().getAttributeName();
                            if (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "Multiple Same column name exist in entity " + entity.getClazz() + " for attribute " + attributeName
                                    + '\n' + " Would you like to override the column name automatically using @JoinColumn ?",
                                    "Error : Same column name exist in table", YES_NO_OPTION) == YES_NO_OPTION) {
                                List<JoinColumn> joinColumns = new ArrayList<>();

                                for (DatabaseField databaseField : de.getMapping().getFields()) {
                                    if (databaseField.isPrimaryKey()) {
                                        JoinColumn joinColumn = new JoinColumn();
                                        joinColumn.setName(attributeName.toUpperCase() + "_" + databaseField.getName());
                                        joinColumn.setReferencedColumnName(databaseField.getName());
                                        joinColumns.add(joinColumn);
                                    }
                                }
                                JPAModelerUtil.addDefaultJoinColumnForCompositePK((PersistenceClassWidget) optional.get(), attributeName, joinColumns);
                                file.getModelerUtil().loadModelerFile(file);
                                throwError = false;
                            }
                        }
                    }
                } else if (NO_TARGET_FOREIGN_KEYS_SPECIFIED == de.getErrorCode()) {
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
                                throwError = false;
                            }
                        }
                    }
                } else if (NO_FOREIGN_KEYS_ARE_SPECIFIED == de.getErrorCode()) {
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
                                throwError = false;
                            }
                            System.out.println("");
                        }
                    }
                } else if (NO_MAPPING_FOR_PRIMARY_KEY == de.getErrorCode()) {
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
                                throwError = false;
                            }
                        }
                    }
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
                            throwError = false;
                        }

//                                JPAModelerUtil.addDefaultJoinColumnForCompositePK((PersistenceClassWidget) optional.get(), attributeName);
                    }
                }
            }

        } else if (exception instanceof DBConnectionNotFound) {
            try {
                ((DBModelerUtil) file.getModelerUtil()).loadModelerFileInternal(file);
            } catch (DBConnectionNotFound ex1) {
                file.handleException(ex1);
            }
        } else if (exception instanceof DBValidationException) {
            file.getModelerPanelTopComponent().close();
            String message = exception.getLocalizedMessage();
            int end = message.lastIndexOf("Runtime Exceptions:");
            end = end < 1 ? message.length() : end;
            int start = message.lastIndexOf("Exception Description:");
            start = start < 1 ? 0 : start;
            ExceptionUtils.printStackTrace(message.substring(start, end), exception, file);

        } else {
            file.handleException(exception);
        }

        if (throwError) {
//            throw new RuntimeException(exception);
              file.getModelerPanelTopComponent().close();

        } else {
        }

    }

}
