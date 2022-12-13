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
public class Address {

    @Id
    @GeneratedValue(generator = "addressGen", strategy = GenerationType.TABLE)
    @TableGenerator(name = "addressGen", table = "ID_GEN", pkColumnValue = "ADDR_ID", valueColumnName = "GEN_VALUE", pkColumnName = "GEN_KEY")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}