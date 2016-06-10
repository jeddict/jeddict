/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jpa.modeler.spec.extend.adapter;

import javax.xml.bind.annotation.XmlAnyElement;

public class AdaptedMap {

    private Object value;

    @XmlAnyElement
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
