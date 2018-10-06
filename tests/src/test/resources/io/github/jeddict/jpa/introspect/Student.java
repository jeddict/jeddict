package io.github.jeddict.jpa.introspect;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

@Entity
public class Student {

    @Id
    private Long id;

    @Basic(fetch = FetchType.LAZY)
    private String name;

    @Basic
    private boolean primitiveBoolean;

    @Basic
    private Boolean wrapperBoolean;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getPrimitiveBoolean() {
        return primitiveBoolean;
    }

    public void setPrimitiveBoolean(boolean primitiveBoolean) {
        this.primitiveBoolean = primitiveBoolean;
    }

    public Boolean getWrapperBoolean() {
        return wrapperBoolean;
    }

    public void setWrapperBoolean(Boolean wrapperBoolean) {
        this.wrapperBoolean = wrapperBoolean;
    }

}
