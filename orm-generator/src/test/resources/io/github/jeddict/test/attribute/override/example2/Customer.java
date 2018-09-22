/**
 * 11.1.4 AttributeOverride Annotation
 * Example 2
 */
package io.github.jeddict.test.attribute.override.example2;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    private Long id;

    @Basic
    private String name;

    @Embedded
    @AttributeOverride(name = "state", column = @Column(name = "ADDR_STATE"))
    @AttributeOverride(name = "zipcode.zip", column = @Column(name = "ADDR_ZIP"))
    private Address address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}