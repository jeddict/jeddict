/**
 * 11.1.6 Basic Annotation
 * Example 2
 */
package io.github.jeddict.jpa.basic;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

@Entity
public class Student {

    @Id
    private Long id;

    @Basic(fetch = FetchType.LAZY)
    private String name;

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

}
