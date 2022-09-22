/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 6 : Case (a)
 *
 */
package io.github.jeddict.jpa.derived.identities.example6.a;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * @author jGauravGupta
 */
@Entity
@IdClass(PersonId.class)
public class MedicalHistory {

    @Id
    @OneToOne
    @JoinColumn(name = "FK1", referencedColumnName = "FIRSTNAME")
    @JoinColumn(name = "FK2", referencedColumnName = "LASTNAME")
    private Person patient;

    public Person getPatient() {
        return patient;
    }

    public void setPatient(Person patient) {
        this.patient = patient;
    }

}