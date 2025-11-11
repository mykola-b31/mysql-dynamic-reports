package ua.cn.stu.main;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HallBookStatsMapper implements RowMapper<HallBookStats> {

    @Override
    public HallBookStats mapRow(ResultSet rs, int rowNum) throws SQLException {
        HallBookStats stats = new HallBookStats();
        stats.setHallName(rs.getString("hallName"));
        stats.setUniqueBookCount(rs.getInt("uniqueBookCount"));
        stats.setLoanCount(rs.getInt("loanCount"));
        return stats;
    }
}