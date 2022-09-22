/**
 * 11.1.50 TableGenerator Annotation
 */
package io.github.jeddict.jpa.generator.table;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.TableGenerator;

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