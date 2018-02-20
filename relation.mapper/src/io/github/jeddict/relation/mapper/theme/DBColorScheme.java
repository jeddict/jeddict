/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import javax.swing.border.Border;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modeler.border.ShadowBorder;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.edge.IEdgeWidget;
import org.netbeans.modeler.widget.edge.IPEdgeWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.IPinSeperatorWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.openide.util.ImageUtilities;

/**
 * @author Gaurav Gupta
 */
public class DBColorScheme implements IColorScheme {

    private final Paint SCENE_BACKGROUND;
    private final org.netbeans.api.visual.border.Border OPAQUE_BORDER;
    private final Image BUTTON_E;
    private final Image BUTTON_C;
    private final Image BUTTON_E_F;
    private final Image BUTTON_C_F;
    private final Image BUTTON_E_H;
    private final Image BUTTON_C_H;
    private final Color COLOR1;
    private final Color COLOR2;
    private final Color COLOR3;
    private final Color COLOR4;
    private final Color COLOR5;

    protected final Color WIDGET_BORDER_COLOR;
    private final Color WIDGET_SELECT_BORDER_COLOR;
    private final Color WIDGET_HOVER_BORDER_COLOR;

    private final Color WIDGET_HOVER_BACKGROUND;
    private final Color WIDGET_SELECT_BACKGROUND;
    private final Color WIDGET_BACKGROUND;

    private final Color WIDGET_HOVER_LBACKGROUND;
    private final Color WIDGET_SELECT_LBACKGROUND;
    private final Color WIDGET_LBACKGROUND;

    private final Border WIDGET_BORDER;
    private final Border WIDGET_SELECT_BORDER;
    private final Border WIDGET_HOVER_BORDER;

    private final Color EDGE_WIDGET_COLOR;
    private final Color EDGE_WIDGET_SELECT_COLOR;
    private final Color EDGE_WIDGET_HOVER_COLOR;

    private final Color PIN_WIDGET_BACKGROUND;
    private final Color PIN_WIDGET_LBACKGROUND;
    private final Color PIN_WIDGET_HOVER_BACKGROUND;
    private final Color PIN_WIDGET_SELECT_BACKGROUND;
    private final Color PIN_WIDGET_HOVER_LBACKGROUND;
    private final Color PIN_WIDGET_SELECT_LBACKGROUND;

    private final Color PIN_WIDGET_TEXT_COLOR;
    private final Color PIN_WIDGET_HOVER_TEXT_COLOR;
    private final Color PIN_WIDGET_SELECT_TEXT_COLOR;
    private final org.netbeans.api.visual.border.Border PIN_WIDGET_SELECT_BORDER;

    private final Color PIN_SEPERATOR_WIDGET_BACKGROUND;
    private final Color PIN_SEPERATOR_WIDGET_FOREGROUND;

    private static DBColorScheme instance;

    public static DBColorScheme getInstance() {
        if (instance == null) {
            synchronized (DBColorScheme.class) {
                if (instance == null) {
                    instance = new DBColorScheme();
                }
            }
        }
        return instance;
    }

    public Paint getBackgroundPaint() {
        final float[] FRACTIONS = {0.0f, 0.25f, 0.5f, 0.75f, 1.0f};
        final Color[] DARK_COLORS = {new Color(76, 30, 57), new Color(78, 29, 47), new Color(76, 24, 40), new Color(115, 68, 56), new Color(163, 83, 76)};

        LinearGradientPaint DARK_GRADIENT = new LinearGradientPaint(
                new Point2D.Double(0, 0), new Point2D.Double(1000, 0),
                FRACTIONS, DARK_COLORS, MultipleGradientPaint.CycleMethod.REFLECT);
        return DARK_GRADIENT;
    }

    private DBColorScheme() {
        SCENE_BACKGROUND = Color.white;//getBackgroundPaint();//Color.white;

        OPAQUE_BORDER = BorderFactory.createOpaqueBorder(2, 8, 2, 8);
        BUTTON_E = ImageUtilities.loadImage("io/github/jeddict/relation/mapper/resource/image/theme/expand_u.png");
        BUTTON_C = ImageUtilities.loadImage("io/github/jeddict/relation/mapper/resource/image/theme/collapse_u.png");
        BUTTON_E_F = ImageUtilities.loadImage("io/github/jeddict/relation/mapper/resource/image/theme/expand_f.png");
        BUTTON_C_F = ImageUtilities.loadImage("io/github/jeddict/relation/mapper/resource/image/theme/collapse_f.png");
        BUTTON_E_H = ImageUtilities.loadImage("io/github/jeddict/relation/mapper/resource/image/theme/expand_h.png");
        BUTTON_C_H = ImageUtilities.loadImage("io/github/jeddict/relation/mapper/resource/image/theme/collapse_h.png");

        COLOR1 = new Color(221, 235, 246);
        COLOR2 = new Color(255, 255, 255);
        COLOR3 = new Color(214, 235, 255);
        COLOR4 = new Color(255, 255, 255);
        COLOR5 = new Color(241, 249, 253);

        WIDGET_BORDER_COLOR = new Color(80, 78, 71);
        WIDGET_SELECT_BORDER_COLOR = new Color(36, 35, 32);
        WIDGET_HOVER_BORDER_COLOR = new Color(66, 65, 62);

        WIDGET_HOVER_BACKGROUND = new Color(66, 65, 62);
        WIDGET_SELECT_BACKGROUND = new Color(56, 55, 52);
        WIDGET_BACKGROUND = new Color(80, 78, 71);

        WIDGET_HOVER_LBACKGROUND = new Color(60, 59, 56);
        WIDGET_SELECT_LBACKGROUND = new Color(50, 49, 46);
        WIDGET_LBACKGROUND = new Color(63, 62, 57);

        WIDGET_BORDER = new ShadowBorder(new Color(230, 230, 230, 230), 2, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5);
        WIDGET_SELECT_BORDER = new ShadowBorder(new Color(130, 130, 130, 230), 2, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5);
        WIDGET_HOVER_BORDER = new ShadowBorder(new Color(200, 200, 200, 150), 2, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5);

        EDGE_WIDGET_COLOR = new Color(156, 156, 156);
        EDGE_WIDGET_SELECT_COLOR = new Color(136, 136, 136);
        EDGE_WIDGET_HOVER_COLOR = new Color(146, 146, 146);

        PIN_WIDGET_BACKGROUND = new Color(231, 229, 228);
        PIN_WIDGET_LBACKGROUND = new Color(231, 229, 228);
        PIN_WIDGET_HOVER_BACKGROUND = new Color(211, 219, 218);
        PIN_WIDGET_HOVER_LBACKGROUND = new Color(211, 219, 218);
        PIN_WIDGET_SELECT_BACKGROUND = new Color(235, 112, 62);
        PIN_WIDGET_SELECT_LBACKGROUND = new Color(246, 129, 82);

        PIN_WIDGET_TEXT_COLOR = new Color(35, 35, 35);
        PIN_WIDGET_HOVER_TEXT_COLOR = new Color(40, 40, 40);
        PIN_WIDGET_SELECT_TEXT_COLOR = Color.WHITE;
        PIN_WIDGET_SELECT_BORDER = BorderFactory.createCompositeBorder(BorderFactory.createLineBorder(0, 1, 0, 1, WIDGET_BORDER_COLOR), BorderFactory.createLineBorder(2, 3, 2, 3, WIDGET_HOVER_BORDER_COLOR));

        PIN_SEPERATOR_WIDGET_BACKGROUND = new Color(136, 136, 136);
        PIN_SEPERATOR_WIDGET_FOREGROUND = Color.WHITE;
    }

    @Override
    public void installUI(IPNodeWidget widget) {
        widget.setBorder(WIDGET_BORDER);
        Widget header = widget.getHeader();
        Rectangle bound = widget.getHeader().getBounds();
        if (bound == null) {
            bound = HEADER_BOUND;
        }
        GradientPaint gp = new GradientPaint(bound.x + bound.width / 2, bound.y, WIDGET_LBACKGROUND, bound.x + bound.width / 2, bound.y + bound.height, WIDGET_BACKGROUND);
        header.setBackground(gp);
        header.setBorder(OPAQUE_BORDER);
        widget.getHeader().setOpaque(true);
        widget.getNodeNameWidget().setForeground(Color.WHITE);
        Font font = widget.getNodeNameWidget().getFont()!=null?widget.getNodeNameWidget().getFont():widget.getScene().getDefaultFont();
        widget.getNodeNameWidget().setFont(font.deriveFont(Font.BOLD, 12));
        Widget pinsSeparator = widget.getPinsSeparator();
        pinsSeparator.setForeground(PIN_SEPERATOR_WIDGET_BACKGROUND);
        widget.getMinimizeButton().setImage(this.getMinimizeWidgetImage(widget));
    }

    @Override
    public void updateUI(IPNodeWidget widget, ObjectState previousState, ObjectState state) {
        if (!previousState.isSelected() && state.isSelected()) {
            widget.bringToFront();
        }
        Rectangle bound = widget.getHeader().getBounds();
        if (bound != null) {
            if (state.isHovered()) {
                GradientPaint gp = new GradientPaint(bound.x + bound.width / 2, bound.y, WIDGET_HOVER_LBACKGROUND, bound.x + bound.width / 2, bound.y + bound.height, WIDGET_HOVER_BACKGROUND);
                widget.getHeader().setBackground(gp);
                widget.setBorder(WIDGET_HOVER_BORDER);
            } else if (state.isSelected() || state.isFocused()) {
                GradientPaint gp = new GradientPaint(bound.x, bound.y, WIDGET_SELECT_LBACKGROUND, bound.width, bound.height, WIDGET_SELECT_BACKGROUND);
                widget.getHeader().setBackground(gp);
                widget.setBorder(WIDGET_SELECT_BORDER);
            } else {
                GradientPaint gp = new GradientPaint(bound.x + bound.width / 2, bound.y, WIDGET_LBACKGROUND, bound.x + bound.width / 2, bound.y + bound.height, WIDGET_BACKGROUND);
                widget.getHeader().setBackground(gp);
                widget.setBorder(WIDGET_BORDER);
            }
            widget.getMinimizeButton().setImage(this.getMinimizeWidgetImage(state, widget));
        }

    }

    @Override
    public void installUI(IPEdgeWidget widget) {
        widget.setPaintControlPoints(true);
    }

    @Override
    public void updateUI(IPEdgeWidget widget, ObjectState previousState, ObjectState state) {
        if (state.isSelected()) {
            widget.setForeground(EDGE_WIDGET_SELECT_COLOR);
        } else if (state.isHovered() || state.isFocused()) {
            widget.setForeground(EDGE_WIDGET_HOVER_COLOR);
        } else {
            widget.setForeground(EDGE_WIDGET_COLOR);
        }

        if (state.isSelected()) {
            widget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
            widget.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
            widget.setControlPointCutDistance(0);
        } else if (state.isHovered()) {
            widget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
            widget.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
            widget.setControlPointCutDistance(0);
        } else {

            widget.setControlPointShape(PointShape.NONE);
            widget.setEndPointShape(PointShape.NONE);
            widget.setControlPointCutDistance(5);
        }
    }

    @Override
    public void installUI(IPinWidget widget) {
        widget.setBorder(OPAQUE_BORDER);
        widget.setBackground(PIN_WIDGET_BACKGROUND);
        widget.getPinNameWidget().setForeground(PIN_WIDGET_TEXT_COLOR);
    }

    @Override
    public void updateUI(IPinWidget widget, ObjectState previousState, ObjectState state) {
        widget.setOpaque(state.isHovered() || state.isFocused());

        Rectangle bound = widget.getBounds();
        if (bound != null) {
            if (state.isFocused() || state.isSelected()) {
                GradientPaint gp = new GradientPaint(bound.x, bound.y, PIN_WIDGET_SELECT_BACKGROUND, bound.x, bound.height, PIN_WIDGET_SELECT_LBACKGROUND);
                widget.setBackground(gp);
            } else {
                GradientPaint gp = new GradientPaint(bound.x, bound.y, PIN_WIDGET_HOVER_BACKGROUND, bound.x, bound.height, PIN_WIDGET_HOVER_LBACKGROUND);
                widget.setBackground(gp);
            }
        }

        if (state.isHovered()) {
            widget.getPinNameWidget().setForeground(PIN_WIDGET_HOVER_TEXT_COLOR);
        } else {
            widget.getPinNameWidget().setForeground(PIN_WIDGET_TEXT_COLOR);
        }

        if (state.isSelected()) {
            widget.setBorder(PIN_WIDGET_SELECT_BORDER);
            widget.getPinNameWidget().setForeground(PIN_WIDGET_SELECT_TEXT_COLOR);
        } else {
            widget.setBorder(OPAQUE_BORDER);
            widget.getPinNameWidget().setForeground(PIN_WIDGET_TEXT_COLOR);
        }
    }

    @Override
    public boolean isNodeMinimizeButtonOnRight(IPNodeWidget widget) {
        return true;
    }

    @Override
    public Image getMinimizeWidgetImage(IPNodeWidget widget) {
        return widget.isMinimized() ? BUTTON_E : BUTTON_C;
    }

    public Image getMinimizeWidgetImage(ObjectState state, IPNodeWidget widget) {
        return widget.isMinimized()
                ? (state.isHovered() ? BUTTON_E_H : (state.isSelected() ? BUTTON_E_F : BUTTON_E))
                : (state.isHovered() ? BUTTON_C_H : (state.isSelected() ? BUTTON_C_F : BUTTON_C));
    }

    @Override
    public void installUI(IPinSeperatorWidget label) {
        label.setOpaque(true);
        label.setBackground(PIN_SEPERATOR_WIDGET_BACKGROUND);
        label.setForeground(PIN_SEPERATOR_WIDGET_FOREGROUND);
        Font fontPinCategory = label.getScene().getDefaultFont().deriveFont(9.5f);
        label.setFont(fontPinCategory);
        label.setAlignment(LabelWidget.Alignment.CENTER);
        label.setCheckClipping(true);
    }

    @Override
    public void installUI(IModelerScene scene) {
        scene.setBackground(SCENE_BACKGROUND);
    }

    @Override
    public void highlightUI(IPNodeWidget widget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void highlightUI(IEdgeWidget widget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void highlightUI(IPinWidget widget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
