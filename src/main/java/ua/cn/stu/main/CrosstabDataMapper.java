package ua.cn.stu.main;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CrosstabDataMapper implements RowMapper<CrosstabData> {
    @Override
    public CrosstabData mapRow(ResultSet rs, int rowNum) throws SQLException {
        CrosstabData data = new CrosstabData();
        data.setHallName(rs.getString("hallName"));
        data.setIssueQuarter(rs.getString("issueQuarter")); // Використовуємо Timestamp
        data.setLoanId(rs.getInt("loanId"));
        return data;
    }
}
