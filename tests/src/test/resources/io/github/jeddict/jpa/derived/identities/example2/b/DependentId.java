package io.github.jeddict.jpa.derived.identities.example2.b;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DependentId implements Serializable {

    private String name;

    private EmployeeId emp;

    public DependentId() {
    }

    public DependentId(String name, EmployeeId emp) {
        this.name = name;
        this.emp = emp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmployeeId getEmp() {
        return emp;
    }

    public void setEmp(EmployeeId emp) {
        this.emp = emp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Objects.equals(getClass(), obj.getClass())) {
            return false;
        }
        final DependentId other = (DependentId) obj;
        if (!java.util.Objects.equals(this.getName(), other.getName())) {
            return false;
        }
        if (!java.util.Objects.equals(this.getEmp(), other.getEmp())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.getName());
        hash = 31 * hash + Objects.hashCode(this.getEmp());
        return hash;
    }

    @Override
    public String toString() {
        return "DependentId{" + " name=" + name + ", emp=" + emp + '}';
    }

}