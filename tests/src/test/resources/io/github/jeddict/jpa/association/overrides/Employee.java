/**
 * 11.1.3 AssociationOverrides Annotation
 */
package io.github.jeddict.jpa.association.overrides;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Version;

@MappedSuperclass
public class Employee {

    @Id
    protected Long id;

    @OneToOne
    private Locker locker;

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

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
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