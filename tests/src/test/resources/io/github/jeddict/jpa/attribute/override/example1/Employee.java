/**
 * 11.1.4 AttributeOverride Annotation
 * Example 1
 */
package io.github.jeddict.jpa.attribute.override.example1;

import javax.persistence.Basic;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public class Employee {

    @Id
    protected Long id;

    @Basic
    private String address;

    @Version
    protected long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

}