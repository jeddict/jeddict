/**
 * This file was generated by the JPA Modeler
 */
package io.github.jeddict.jpa.employee;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;

@Entity
public class CompanyEmployee extends Employee {

    @Basic
    private int vacation;

    public int getVacation() {
        return vacation;
    }

    public void setVacation(int vacation) {
        this.vacation = vacation;
    }

}