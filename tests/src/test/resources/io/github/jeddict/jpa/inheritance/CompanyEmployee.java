/**
 * Inheritance Annotation
 */
package io.github.jeddict.jpa.inheritance;

import jakarta.persistence.Basic;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * @author jGauravGupta
 */
@Entity
@DiscriminatorValue("2")
public class CompanyEmployee extends Employee {

    @Basic
    private String vacations;

    @Basic
    private int vacation;

    public String getVacations() {
        return vacations;
    }

    public void setVacations(String vacations) {
        this.vacations = vacations;
    }

    public int getVacation() {
        return vacation;
    }

    public void setVacation(int vacation) {
        this.vacation = vacation;
    }

}