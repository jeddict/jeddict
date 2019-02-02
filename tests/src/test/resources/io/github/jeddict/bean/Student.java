// before package snippet 
package io.github.jeddict.bean;

import java.util.Objects;

public class Student extends Object implements Cloneable {

    // before field snippet 
    private String name;

    private int age;

    public Student(String name) {
        this.name = name;
    }

    public Student() {
    }

    // before method snippet
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student name(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Student age(int age) {
        this.age = age;
        return this;
    }
    // after method snippet

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Objects.equals(getClass(), obj.getClass())) {
            return false;
        }
        final Student other = (Student) obj;
        if (!java.util.Objects.equals(this.getName(), other.getName())) {
            return false;
        }
        if (this.getAge() != other.getAge()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.getName());
        hash = 31 * hash + this.getAge();
        return hash;
    }

    @Override
    public String toString() {
        return "Student{" + " name=" + name + '}';
    }

}