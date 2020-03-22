/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 3 : Case (b)
 *
 */
package io.github.jeddict.jpa.derived.identities.example3.b;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

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