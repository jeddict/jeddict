/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jpa.modeler.spec.validator;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class MarshalValidator<E> extends XmlAdapter<E, E> {
        @Override
        public E unmarshal(E e) throws Exception {
            return e;

        }
    }
