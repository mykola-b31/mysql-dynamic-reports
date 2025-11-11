package ua.cn.stu.domain;

import java.util.ArrayList;
import java.util.List;

public class Hall {
    private Integer hallId;
    private Integer floor;
    private Integer librarianId;
    private String name;

    private String librarianName;

    public Hall() {}


    public Integer getHallId() {
        return hallId;
    }

    public void setHallId(Integer hallId) {
        this.hallId = hallId;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(Integer librarianId) {
        this.librarianId = librarianId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLibrarianName() {
        return librarianName;
    }

    public void setLibrarianName(String librarianName) {
        this.librarianName = librarianName;
    }

    @Override
    public String toString() {
        return name + " (Поверх: " + floor + ")";
    }
}