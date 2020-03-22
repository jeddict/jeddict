/**
 * 11.1.8 CollectionTable Annotation
 */
package io.github.jeddict.jpa.collection.table;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

/**
 * @author jGauravGupta
 */
@Entity
public class WealthyPerson extends Person {

    @ElementCollection
    @CollectionTable(name = "HOMES")
    @AttributeOverride(name = "city", column = @Column(name = "HOME_CITY"))
    @AttributeOverride(name = "state", column = @Column(name = "HOME_STATE"))
    @AttributeOverride(name = "street", column = @Column(name = "HOME_STREET"))
    private List<Address> vacationHomes;

    public List<Address> getVacationHomes() {
        if (vacationHomes == null) {
            vacationHomes = new ArrayList<>();
        }
        return vacationHomes;
    }

    public void setVacationHomes(List<Address> vacationHomes) {
        this.vacationHomes = vacationHomes;
    }

    public void addVacationHome(Address vacationHome) {
        getVacationHomes().add(vacationHome);
    }

    public void removeVacationHome(Address vacationHome) {
        getVacationHomes().remove(vacationHome);
    }

}