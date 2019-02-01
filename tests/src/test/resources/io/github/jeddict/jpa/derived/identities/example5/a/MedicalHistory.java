/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 5 : Case (a)
 *
 */
package io.github.jeddict.jpa.derived.identities.example5.a;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

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