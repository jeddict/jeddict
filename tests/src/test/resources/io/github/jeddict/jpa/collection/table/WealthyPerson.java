/**
 * 11.1.8 CollectionTable Annotation
 */
package io.github.jeddict.jpa.collection.table;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

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