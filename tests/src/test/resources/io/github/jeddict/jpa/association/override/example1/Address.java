/**
 * 11.1.2 AssociationOverride Annotation
 * Example 1
 */
package io.github.jeddict.jpa.association.override.example1;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author jGauravGupta
 */
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}