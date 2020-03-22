/**
 * 11.1.50 TableGenerator Annotation
 */
package io.github.jeddict.jpa.generator.table;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

/**
 * @author jGauravGupta
 */
@Entity
public class Employee {

    @Id
    @GeneratedValue(generator = "empGen", strategy = GenerationType.TABLE)
    @TableGenerator(name = "empGen", table = "ID_GEN", pkColumnValue = "EMP_ID", valueColumnName = "GEN_VALUE", pkColumnName = "GEN_KEY", allocationSize = 1)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}