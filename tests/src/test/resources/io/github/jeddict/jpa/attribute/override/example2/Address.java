/**
 * 11.1.4 AttributeOverride Annotation
 * Example 2
 */
package io.github.jeddict.jpa.attribute.override.example2;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

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