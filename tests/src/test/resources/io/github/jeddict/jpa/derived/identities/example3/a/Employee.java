/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 3 : Case (a)
 *
 */
package io.github.jeddict.jpa.derived.identities.example3.a;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

/**
 * @author jGauravGupta
 */
@Entity
public class Employee {

    @EmbeddedId
    private EmployeeId empId;

    public EmployeeId getEmpId() {
        return empId;
    }

    public void setEmpId(EmployeeId empId) {
        this.empId = empId;
    }

}