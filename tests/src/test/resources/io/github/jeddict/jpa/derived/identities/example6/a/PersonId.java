package io.github.jeddict.jpa.derived.identities.example6.a;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PersonId implements Serializable {

    private String firstName;

    private String lastName;

    public PersonId() {
    }

    public PersonId(String firstName, String lastName) {
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
        final PersonId other = (PersonId) obj;
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
        return "PersonId{" + " firstName=" + firstName + ", lastName=" + lastName + '}';
    }

}