/**
 * 11.1.7 Cacheable Annotation
 */
package io.github.jeddict.jpa.cacheable;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author jGauravGupta
 */
@Entity
@Cacheable(false)
public class CacheableForceDisable {

    @Id
    private Long id;

    @Basic
    private String attribute;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

}