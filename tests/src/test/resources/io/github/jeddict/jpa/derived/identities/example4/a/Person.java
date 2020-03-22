/**
 * 2.4.1.3 Examples of Derived Identities
 * Example 4 : Case (a)
 *
 */
package io.github.jeddict.jpa.derived.identities.example4.a;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author jGauravGupta
 */
@Entity
public class Person {

    @Id
    private String ssn;

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

}