/**
 * 11.1.4 AttributeOverride Annotation
 * Example 1
 */
package io.github.jeddict.test.attribute.override.example1;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

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