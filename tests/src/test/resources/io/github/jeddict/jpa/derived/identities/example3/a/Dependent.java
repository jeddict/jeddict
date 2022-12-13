/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 3 : Case (a)
 *
 */
package io.github.jeddict.jpa.derived.identities.example3.a;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * @author jGauravGupta
 */
@Entity
@IdClass(DependentId.class)
public class Dependent {

    @Id
    @Column(name = "dep_name")
    private String name;

    @Id
    @ManyToOne
    @JoinColumn(name = "FK1", referencedColumnName = "FIRSTNAME")
    @JoinColumn(name = "FK2", referencedColumnName = "LASTNAME")
    private Employee emp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee getEmp() {
        return emp;
    }

    public void setEmp(Employee emp) {
        this.emp = emp;
    }

}