/**
 * 11.1.28 Lob Annotation
 */
package io.github.jeddict.jpa.lob;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

/**
 * @author jGauravGupta
 */
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