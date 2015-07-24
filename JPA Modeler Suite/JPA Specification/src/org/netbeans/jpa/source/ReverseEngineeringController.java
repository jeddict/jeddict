/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jpa.source;

import javax.lang.model.element.TypeElement;
import org.netbeans.jpa.modeler.spec.EntityMappings;

/**
 *
 * @author Gaurav Gupta
 */
public interface ReverseEngineeringController {
   void load(EntityMappings entityMappings, TypeElement element, boolean fieldAccess);
}
