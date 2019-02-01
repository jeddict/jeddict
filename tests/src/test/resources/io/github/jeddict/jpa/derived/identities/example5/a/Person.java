/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 5 : Case (a)
 *
 */
package io.github.jeddict.jpa.derived.identities.example5.a;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(PersonId.class)
public class Person {

    @Id
    private String firstName;

    @Id
    private String lastName;

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

}