/**
 * 11.1.42 OrderBy Annotation
 * Table 39
 */
package io.github.jeddict.jpa.orderby;

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;

/**
 * @author jGauravGupta
 */
@Embeddable
public class Zipcode {

    @Basic
    private String zip;

    @Basic
    private String plusFour;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPlusFour() {
        return plusFour;
    }

    public void setPlusFour(String plusFour) {
        this.plusFour = plusFour;
    }

}