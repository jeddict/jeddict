/**
 * 11.1.28 Lob Annotation
 */
package io.github.jeddict.jpa.lob;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Student {

    @Id
    private Long id;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "REPORT")
    private String report;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "EMP_PIC", columnDefinition = "BLOB NOT NULL")
    private byte[] pic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public byte[] getPic() {
        return pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

}