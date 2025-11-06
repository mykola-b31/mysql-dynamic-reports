package ua.cn.stu.main;

import org.springframework.jdbc.core.RowMapper;
import ua.cn.stu.domain.Librarian;
import ua.cn.stu.domain.LibrarianPosition;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LibrarianMapper implements RowMapper<Librarian> {

    @Override
    public Librarian mapRow(ResultSet rs, int rowNum) throws SQLException {
        Librarian librarian = new Librarian();
        librarian.setLibrarianId(rs.getInt("librarian_id"));
        librarian.setHireDate(rs.getDate("hire_date"));
        librarian.setName(rs.getString("name"));

        librarian.setPosition(LibrarianPosition.fromString(rs.getString("position")));

        librarian.setSalary(rs.getBigDecimal("salary"));
        return librarian;
    }
}