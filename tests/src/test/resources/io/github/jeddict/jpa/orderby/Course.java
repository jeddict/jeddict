/**
 * 11.1.42 OrderBy Annotation
 * Table 39
 */
package io.github.jeddict.jpa.orderby;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;

/**
 * @author jGauravGupta
 */
@Entity
public class Course {

    @Id
    private Long id;

    @ManyToMany
    @OrderBy("lastname")
    private List<Student> students;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Student> getStudents() {
        if (students == null) {
            students = new ArrayList<>();
        }
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void addStudent(Student student) {
        getStudents().add(student);
        student.getCourses().add(this);
    }

    public void removeStudent(Student student) {
        getStudents().remove(student);
        student.getCourses().remove(this);
    }

}