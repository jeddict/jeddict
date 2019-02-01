package io.github.jeddict.jpa.derived.identities.example2.a;

import java.io.Serializable;
import java.util.Objects;

public class EmployeeId implements Serializable {

    private String firstName;

    private String lastName;

    public EmployeeId() {
    }

    public EmployeeId(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Objects.equals(getClass(), obj.getClass())) {
            return false;
        }
        final EmployeeId other = (EmployeeId) obj;
        if (!java.util.Objects.equals(this.getFirstName(), other.getFirstName())) {
            return false;
        }
        if (!java.util.Objects.equals(this.getLastName(), other.getLastName())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.getFirstName());
        hash = 31 * hash + Objects.hashCode(this.getLastName());
        return hash;
    }

    @Override
    public String toString() {
        return "EmployeeId{" + " firstName=" + firstName + ", lastName=" + lastName + '}';
    }

}