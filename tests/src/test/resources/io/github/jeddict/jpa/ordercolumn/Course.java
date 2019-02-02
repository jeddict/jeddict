/**
 * 11.1.43 OrderColumn Annotation
 * Table 40
 */
package io.github.jeddict.jpa.ordercolumn;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

@Entity
public class Course {

    @Id
    private Long id;

    @ManyToMany
    @JoinTable(name = "COURSE_ENROLLMENT")
    private List<Student> students;

    @ManyToMany
    @OrderColumn(name = "WAITLIST_ORDER")
    @JoinTable(name = "WAIT_LIST")
    private List<Student> waitList;

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

    public List<Student> getWaitList() {
        if (waitList == null) {
            waitList = new ArrayList<>();
        }
        return waitList;
    }

    public void setWaitList(List<Student> waitList) {
        this.waitList = waitList;
    }

    public void addWaitList(Student waitList) {
        getWaitList().add(waitList);
    }

    public void removeWaitList(Student waitList) {
        getWaitList().remove(waitList);
    }

}