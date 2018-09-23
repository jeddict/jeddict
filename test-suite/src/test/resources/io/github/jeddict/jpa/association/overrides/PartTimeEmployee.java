/**
 * 11.1.3 AssociationOverrides Annotation
 */
package io.github.jeddict.jpa.association.overrides;

import javax.persistence.AssociationOverride;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

@Entity
@AssociationOverride(name = "address", joinColumns = @JoinColumn(name = "ADDR_ID"))
@AssociationOverride(name = "locker", joinColumns = @JoinColumn(name = "LCKR_ID"))
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