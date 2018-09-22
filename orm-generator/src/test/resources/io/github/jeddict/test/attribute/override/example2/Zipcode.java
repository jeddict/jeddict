/**
 * 11.1.4 AttributeOverride Annotation
 * Example 2
 */
package io.github.jeddict.test.attribute.override.example2;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

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