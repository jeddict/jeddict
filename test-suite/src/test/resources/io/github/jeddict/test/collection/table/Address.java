/**
 * 11.1.8 CollectionTable Annotation
 */
package io.github.jeddict.test.collection.table;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

    @Basic
    private String street;

    @Basic
    private String city;

    @Basic
    private String state;

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

}