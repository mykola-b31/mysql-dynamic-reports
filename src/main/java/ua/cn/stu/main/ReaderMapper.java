package ua.cn.stu.main;

import org.springframework.jdbc.core.RowMapper;
import ua.cn.stu.domain.Reader;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReaderMapper implements RowMapper<Reader> {

    @Override
    public Reader mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reader reader = new Reader();
        reader.setReaderId(rs.getInt("reader_id"));
        reader.setEmail(rs.getString("email"));
        reader.setName(rs.getString("name"));
        reader.setPhone(rs.getString("phone"));
        reader.setRegistrationDate(rs.getDate("registration_date"));
        return reader;
    }
}