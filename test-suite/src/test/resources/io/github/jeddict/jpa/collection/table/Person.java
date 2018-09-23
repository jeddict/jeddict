/**
 * 11.1.8 CollectionTable Annotation
 */
package io.github.jeddict.jpa.collection.table;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@DiscriminatorColumn(length = 31)
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