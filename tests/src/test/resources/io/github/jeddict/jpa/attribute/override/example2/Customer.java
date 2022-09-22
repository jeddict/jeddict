/**
 * 11.1.4 AttributeOverride Annotation
 * Example 2
 */
package io.github.jeddict.jpa.attribute.override.example2;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @author jGauravGupta
 */
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