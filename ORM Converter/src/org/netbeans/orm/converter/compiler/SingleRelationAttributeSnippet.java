/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.orm.converter.compiler;

/**
 *
 * @author jGauravGupta
 */
public abstract class SingleRelationAttributeSnippet extends AbstractRelationDefSnippet {

    protected boolean optional = false;
    protected String mapsId;
    protected boolean primaryKey;

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * @return the mapsId
     */
    public String getMapsId() {
        return mapsId;
    }

    /**
     * @param mapsId the mapsId to set
     */
    public void setMapsId(String mapsId) {
        this.mapsId = mapsId;
    }

    /**
     * @return the primaryKey
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
}
