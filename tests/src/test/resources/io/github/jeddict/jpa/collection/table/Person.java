/**
 * 11.1.8 CollectionTable Annotation
 */
package io.github.jeddict.jpa.collection.table;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jGauravGupta
 */
@Entity
public class Person {

    @Id
    private Long ssn;

    @Basic
    private String name;

    @ElementCollection
    @Column(name="name", length=50)
    private Set<String> nickNames;

    @Embedded
    private Address address;

    public Long getSsn() {
        return ssn;
    }

    public void setSsn(Long ssn) {
        this.ssn = ssn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getNickNames() {
        if (nickNames == null) {
            nickNames = new HashSet<>();
        }
        return nickNames;
    }

    public void setNickNames(Set<String> nickNames) {
        this.nickNames = nickNames;
    }

    public void addNickName(String nickName) {
        getNickNames().add(nickName);
    }

    public void removeNickName(String nickName) {
        getNickNames().remove(nickName);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}