/**
 * 11.1.31 MapKey Annotation
 * Example 1
 */
package io.github.jeddict.jpa.mapkey.example1;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * @author jGauravGupta
 */
@Entity
public class Employee {

    @Id
    private Long empId;

    @ManyToOne
    @JoinColumn(name = "DEPT_ID")
    private Department department;

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

}