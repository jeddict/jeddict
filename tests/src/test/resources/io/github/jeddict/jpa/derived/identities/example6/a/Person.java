/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 6 : Case (a)
 *
 */
package io.github.jeddict.jpa.derived.identities.example6.a;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Person {

    @EmbeddedId
    private PersonId id;

    public PersonId getId() {
        return id;
    }

    public void setId(PersonId id) {
        this.id = id;
    }

}