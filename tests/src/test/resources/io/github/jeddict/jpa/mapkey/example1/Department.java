/**
 * 11.1.31 MapKey Annotation
 * Example 1
 */
package io.github.jeddict.jpa.mapkey.example1;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

/**
 * @author jGauravGupta
 */
@Entity
public class Department {

    @Id
    private Long id;

    @MapKey(name = "empId")
    @OneToMany(mappedBy = "department")
    private Map<Long, Employee> employees;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<Long, Employee> getEmployees() {
        if (employees == null) {
            employees = new HashMap<>();
        }
        return employees;
    }

    public void setEmployees(Map<Long, Employee> employees) {
        this.employees = employees;
    }

    public void addEmployee(Long empId, Employee employee) {
        getEmployees().put(empId, employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Long empId, Employee employee) {
        getEmployees().remove(empId);
        employee.setDepartment(null);
    }

}