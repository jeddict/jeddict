/**
 * 11.1.42 OrderBy Annotation
 * Table 39
 */
package io.github.jeddict.jpa.orderby;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderBy;

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