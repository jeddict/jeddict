/**
 * 11.1.31 MapKey Annotation
 * Example 2
 */
package io.github.jeddict.jpa.mapkey.example2;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author jGauravGupta
 */
@Entity
public class Employee {

    @Id
    private Long empId;

    @Basic
    private String name;

    @ManyToOne
    @JoinColumn(name = "DEPT_ID")
    private Department department;

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

}