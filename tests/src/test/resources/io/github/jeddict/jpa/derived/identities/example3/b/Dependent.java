/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 3 : Case (b)
 *
 */
package io.github.jeddict.jpa.derived.identities.example3.b;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

/**
 * @author jGauravGupta
 */
@Entity
public class Dependent {

    @EmbeddedId
    private DependentId id;

    @MapsId("emp")
    @ManyToOne
    @JoinColumn(name = "FK1", referencedColumnName = "FIRSTNAME")
    @JoinColumn(name = "FK2", referencedColumnName = "LASTNAME")
    private Employee emp;

    public DependentId getId() {
        return id;
    }

    public void setId(DependentId id) {
        this.id = id;
    }

    public Employee getEmp() {
        return emp;
    }

    public void setEmp(Employee emp) {
        this.emp = emp;
    }

}