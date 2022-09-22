/**
 * 11.1.2 AssociationOverride Annotation
 * Example 1
 */
package io.github.jeddict.jpa.association.override.example1;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

/**
 * @author jGauravGupta
 */
@MappedSuperclass
public class Employee {

    @Id
    protected Long id;

    @ManyToOne
    protected Address address;

    @Version
    protected long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

}