/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 2 : Case (b)
 *
 */
package io.github.jeddict.jpa.derived.identities.example2.b;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

/**
 * @author jGauravGupta
 */
@Entity
@IdClass(EmployeeId.class)
public class Employee {

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