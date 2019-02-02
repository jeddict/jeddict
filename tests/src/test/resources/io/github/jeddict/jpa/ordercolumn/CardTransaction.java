/**
 * 11.1.43 OrderColumn Annotation
 * Table 40
 */
package io.github.jeddict.jpa.ordercolumn;

import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CardTransaction {

    @Id
    private Long id;

    @Basic
    private String comments;

    @Basic
    private LocalDateTime txTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getTxTime() {
        return txTime;
    }

    public void setTxTime(LocalDateTime txTime) {
        this.txTime = txTime;
    }

}