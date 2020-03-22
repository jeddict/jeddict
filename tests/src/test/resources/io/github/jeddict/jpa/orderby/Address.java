/**
 * 11.1.42 OrderBy Annotation
 * Table 39
 */
package io.github.jeddict.jpa.orderby;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * @author jGauravGupta
 */
@Embeddable
public class Address {

    @Basic
    private String street;

    @Basic
    private String city;

    @Basic
    private String state;

    @Embedded
    private Zipcode zipcode;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Zipcode getZipcode() {
        return zipcode;
    }

    public void setZipcode(Zipcode zipcode) {
        this.zipcode = zipcode;
    }

}