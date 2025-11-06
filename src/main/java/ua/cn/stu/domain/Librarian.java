package ua.cn.stu.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Librarian {
    private Integer librarianId;
    private Date hireDate;
    private String name;
    private LibrarianPosition position;
    private BigDecimal salary;

    public Librarian() {}

    public Integer getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(Integer librarianId) {
        this.librarianId = librarianId;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LibrarianPosition getPosition() {
        return position;
    }

    public String getPositionString() {
        return position != null ? position.toString() : "";
    }

    public void setPosition(LibrarianPosition position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return name + " - " + (position != null ? position.toString() : "N/A");
    }
}