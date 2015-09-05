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
package org.netbeans.jpa.modeler.specification.export;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.poi.hslf.usermodel.HSLFFreeformShape;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedIdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.VersionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.GENERALIZATION;
import org.netbeans.modeler.anchorshape.IconAnchorShape;
import org.netbeans.modeler.specification.export.ExportManager;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.widget.edge.EdgeWidget;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.openide.util.Exceptions;

public class ExportManagerImpl implements ExportManager {

    private final Map<Image, byte[]> icons = new HashMap<Image, byte[]>();

    @Override
    public void export(IModelerScene scene, FileType format, File file) {
        try {
            HSLFSlideShow ppt = new HSLFSlideShow();
            int width = 0, height = 0;
            HSLFSlide slide = ppt.createSlide();
            for (IBaseElementWidget baseElementWidget : scene.getBaseElements()) {
                if (baseElementWidget instanceof INodeWidget) {
                    INodeWidget nodeWidget = (INodeWidget) baseElementWidget;
                    Rectangle rect = nodeWidget.getSceneViewBound();

                    HSLFGroupShape group = new HSLFGroupShape();
                    slide.addShape(group);

                    if (width < rect.x + rect.width) {
                        width = rect.x + rect.width;
                    }
                    if (height < rect.y + rect.height) {
                        height = rect.y + rect.height;
                    }
                                       
                    HSLFTextBox shape = new HSLFTextBox();
                    shape.setAnchor(new java.awt.Rectangle(rect.x, rect.y, rect.width, rect.height));
                    shape.setFillColor(Color.WHITE);
                    shape.setLineColor(Color.BLACK);

                    if (baseElementWidget instanceof PersistenceClassWidget) {
                        PersistenceClassWidget entityWidget = (PersistenceClassWidget) baseElementWidget;
                        HSLFTextRun classTextRun = shape.setText(entityWidget.getName());
                        classTextRun.setFontSize(18.);
                        classTextRun.setFontFamily("Arial");
                        classTextRun.setBold(true);
                        classTextRun.setUnderlined(true);
                        classTextRun.setFontColor(Color.BLACK);
                        classTextRun.getTextParagraph().setAlignment(TextAlign.LEFT);
                        Class category = null;
                        for (AttributeWidget attributeWidget : entityWidget.getAllAttributeWidgets(false)) {
                            if (category == null || !category.isAssignableFrom(attributeWidget.getClass())) {
                                HSLFTextRun categoryTextRun = null;
                                if (attributeWidget instanceof BasicAttributeWidget) {
                                    if (category == BasicCollectionAttributeWidget.class) {
                                        continue;
                                    }
                                    category = BasicAttributeWidget.class;
                                    categoryTextRun = shape.appendText("Basic", true);
                                } else if (attributeWidget instanceof BasicCollectionAttributeWidget) {
                                    if (category == BasicAttributeWidget.class) {
                                        continue;
                                    }
                                    category = BasicCollectionAttributeWidget.class;
                                    categoryTextRun = shape.appendText("Basic", true);
                                } else if (attributeWidget instanceof RelationAttributeWidget) {
                                    category = RelationAttributeWidget.class;
                                    categoryTextRun = shape.appendText("Relation", true);
                                } else if (attributeWidget instanceof VersionAttributeWidget) {
                                    category = VersionAttributeWidget.class;
                                    categoryTextRun = shape.appendText("Version", true);
                                } else if (attributeWidget instanceof TransientAttributeWidget) {
                                    category = TransientAttributeWidget.class;
                                    categoryTextRun = shape.appendText("Transient", true);
                                } else if (attributeWidget instanceof EmbeddedAttributeWidget) {
                                    category = EmbeddedAttributeWidget.class;
                                    categoryTextRun = shape.appendText("Embedded", true);
                                } else if (attributeWidget instanceof IdAttributeWidget) {
                                    category = IdAttributeWidget.class;
                                    categoryTextRun = shape.appendText("PrimaryKey", true);
                                } else if (attributeWidget instanceof EmbeddedIdAttributeWidget) {
                                    category = EmbeddedIdAttributeWidget.class;
                                    categoryTextRun = shape.appendText("Embedded Id", true);
                                } else {
                                    category = null;
                                    categoryTextRun = shape.appendText("Attribute", true);
                                }
                                categoryTextRun.setFontSize(10.);
                                categoryTextRun.setFontFamily("Arial");
                                categoryTextRun.setBold(true);
                                categoryTextRun.setUnderlined(true);
                                categoryTextRun.setFontColor(Color.GRAY);
                                categoryTextRun.getTextParagraph().setAlignment(TextAlign.CENTER);
                            }
                            HSLFTextRun attrTextRun = shape.appendText(attributeWidget.getLabel(), true);
                            attrTextRun.setFontSize(16.);
                            attrTextRun.setFontFamily("Arial");
                            attrTextRun.setBold(false);
                            attrTextRun.setUnderlined(false);
                            attrTextRun.setFontColor(Color.BLACK);
                            attrTextRun.getTextParagraph().setAlignment(TextAlign.LEFT);
                        }
                        group.addShape(shape);
                    }

                } else {
                    EdgeWidget edgeWidget = (EdgeWidget) baseElementWidget;

                    java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
                    Point initPoint = null, moveTo = null;
                    for (java.awt.Point point : edgeWidget.getControlPoints()) {
                        if (initPoint == null) {
                            initPoint = point;
                            path.moveTo(point.x, point.y);
                        } else {
                            path.lineTo(point.x, point.y);
                        }
                        moveTo = point;
                    }

                    HSLFGroupShape group = new HSLFGroupShape();
                    slide.addShape(group);

                    HSLFFreeformShape shape = new HSLFFreeformShape();
                    shape.setPath(path);
                    shape.setFillColor(null);
                    group.addShape(shape);

                    if (edgeWidget instanceof GeneralizationFlowWidget) {
                        Image targetAnchorImage = GENERALIZATION;//((IconAnchorShape) edgeWidget.getTargetAnchorShape()).getImage();
                        byte[] targetAnchor;
                        targetAnchor = icons.get(targetAnchorImage);
                        if (targetAnchor == null) {
                            ByteArrayOutputStream targetAnchorStream = new ByteArrayOutputStream();
                            ImageIO.write(((BufferedImage) targetAnchorImage), "png", targetAnchorStream);
                            targetAnchor = targetAnchorStream.toByteArray();
                            icons.put(targetAnchorImage, targetAnchor);
                        }
                        HSLFPictureData targetPictureData = ppt.addPicture(targetAnchor, PictureData.PictureType.PNG);
                        HSLFPictureShape targetPictureShape = new HSLFPictureShape(targetPictureData);
                        group.addShape(targetPictureShape);
                        targetPictureShape.moveTo(moveTo.x - 6, moveTo.y - 3);
                    } else {
                        //source anchor
                        Image sourceAnchorImage = ((IconAnchorShape) edgeWidget.getSourceAnchorShape()).getImage();
                        byte[] sourceAnchor;
                        sourceAnchor = icons.get(sourceAnchorImage);
                        if (sourceAnchor == null) {
                            ByteArrayOutputStream sourceAnchorStream = new ByteArrayOutputStream();
                            ImageIO.write(((BufferedImage) sourceAnchorImage), "png", sourceAnchorStream);
                            sourceAnchor = sourceAnchorStream.toByteArray();
                            icons.put(sourceAnchorImage, sourceAnchor);
                        }

                        HSLFPictureData sourcePictureData = ppt.addPicture(sourceAnchor, PictureData.PictureType.PNG);
                        HSLFPictureShape sourcePictureShape = new HSLFPictureShape(sourcePictureData);
                        group.addShape(sourcePictureShape);
                        sourcePictureShape.moveTo(initPoint.x - 6, initPoint.y - 5);
                        
                        //target anchor
                        Image targetAnchorImage = ((IconAnchorShape) edgeWidget.getTargetAnchorShape()).getImage();
                        byte[] targetAnchor;
                        targetAnchor = icons.get(targetAnchorImage);
                        if (targetAnchor == null) {
                            ByteArrayOutputStream targetAnchorStream = new ByteArrayOutputStream();
                            ImageIO.write(((BufferedImage) targetAnchorImage), "png", targetAnchorStream);
                            targetAnchor = targetAnchorStream.toByteArray();
                            icons.put(targetAnchorImage, targetAnchor);
                        }
                        HSLFPictureData targetPictureData = ppt.addPicture(targetAnchor, PictureData.PictureType.PNG);
                        HSLFPictureShape targetPictureShape = new HSLFPictureShape(targetPictureData);
                        group.addShape(targetPictureShape);
                        targetPictureShape.moveTo(moveTo.x - 6, moveTo.y - 5);
                    }
                }
            }
            ppt.setPageSize(new Dimension(width + 20, height + 20));
            FileOutputStream out = new FileOutputStream(file);
            ppt.write(out);
            out.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static List<FileType> filetypes;

    @Override
    public List<FileType> getExportType() {
        if (filetypes == null) {
            filetypes = new ArrayList<FileType>();
            filetypes.add(new FileType("ppt", "PPT - Microsoft PowerPoint Presentation"));
        }
        return filetypes;
    }
}
