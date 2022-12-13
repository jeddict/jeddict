/**
 * 11.1.3 AssociationOverrides Annotation
 */
package io.github.jeddict.jpa.association.overrides;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * @author jGauravGupta
 */
@Entity
public class Locker {

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