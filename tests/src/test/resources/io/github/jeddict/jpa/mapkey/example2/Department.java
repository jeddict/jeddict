/**
 * 11.1.31 MapKey Annotation
 * Example 2
 */
package io.github.jeddict.jpa.mapkey.example2;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

@Entity
public class Department {

    @Id
    private Long id;

    @MapKey(name = "name")
    @OneToMany(mappedBy = "department")
    private Map<String, Employee> employees;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Employee> getEmployees() {
        if (employees == null) {
            employees = new HashMap<>();
        }
        return employees;
    }

    public void setEmployees(Map<String, Employee> employees) {
        this.employees = employees;
    }

    public void addEmployee(String name, Employee employee) {
        getEmployees().put(name, employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(String name, Employee employee) {
        getEmployees().remove(name);
        employee.setDepartment(null);
    }

}