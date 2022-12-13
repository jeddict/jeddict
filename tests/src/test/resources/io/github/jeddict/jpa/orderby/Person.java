/**
 * 11.1.42 OrderBy Annotation
 * Table 39
 */
package io.github.jeddict.jpa.orderby;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jGauravGupta
 */
@Entity
public class Person {

    @Id
    private Long id;

    @ElementCollection
    @OrderBy("zipcode.zip, zipcode.plusFour")
    private List<Address> residences;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Address> getResidences() {
        if (residences == null) {
            residences = new ArrayList<>();
        }
        return residences;
    }

    public void setResidences(List<Address> residences) {
        this.residences = residences;
    }

    public void addResidence(Address residence) {
        getResidences().add(residence);
    }

    public void removeResidence(Address residence) {
        getResidences().remove(residence);
    }

}