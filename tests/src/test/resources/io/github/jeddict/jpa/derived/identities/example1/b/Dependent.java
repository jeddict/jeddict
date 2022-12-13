/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 1 : Case (b)
 *
 */
package io.github.jeddict.jpa.derived.identities.example1.b;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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