package ua.cn.stu.main;

import org.springframework.jdbc.core.RowMapper;
import ua.cn.stu.domain.Hall;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallMapper implements RowMapper<Hall> {

    @Override
    public Hall mapRow(ResultSet rs, int rowNum) throws SQLException {
        Hall hall = new Hall();
        hall.setHallId(rs.getInt("hall_id"));
        hall.setFloor(rs.getInt("floor"));
        hall.setLibrarianId(rs.getInt("librarian_id"));
        hall.setName(rs.getString("name"));

        try {
            hall.setLibrarianName(rs.getString("librarian_name"));
        } catch (SQLException e) {
            hall.setLibrarianName("Не призначено");
        }

        return hall;
    }
}