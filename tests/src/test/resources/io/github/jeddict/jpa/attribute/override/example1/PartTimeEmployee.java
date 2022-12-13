/**
 * 11.1.4 AttributeOverride Annotation
 * Example 1
 */
package io.github.jeddict.jpa.attribute.override.example1;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * @author jGauravGupta
 */
@Entity
@AttributeOverride(name = "address", column = @Column(name = "ADDR"))
public class PartTimeEmployee extends Employee {

    @Basic
    private Float hourlyWage;

    public Float getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(Float hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

}