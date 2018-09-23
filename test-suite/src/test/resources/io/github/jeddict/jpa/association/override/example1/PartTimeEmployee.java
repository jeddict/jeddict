/**
 * 11.1.2 AssociationOverride Annotation
 * Example 1
 */
package io.github.jeddict.jpa.association.override.example1;

import javax.persistence.AssociationOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

@Entity
@AssociationOverride(name = "address", joinColumns = @JoinColumn(name = "ADDR_ID"))
public class PartTimeEmployee extends Employee {

    @Basic
    @Column(name = "WAGE")
    private Float hourlyWage;

    public Float getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(Float hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

}