/**
 * 11.1.7 Cacheable Annotation
 */
package io.github.jeddict.test.cacheable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CacheableDisable {

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