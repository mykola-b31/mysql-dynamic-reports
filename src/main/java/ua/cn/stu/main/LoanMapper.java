package ua.cn.stu.main;

import org.springframework.jdbc.core.RowMapper;
import ua.cn.stu.domain.Loan;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoanMapper implements RowMapper<Loan> {

    @Override
    public Loan mapRow(ResultSet rs, int rowNum) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setReaderId(rs.getInt("reader_id"));
        loan.setIssueDate(rs.getTimestamp("issue_date"));
        loan.setReturnDate(rs.getTimestamp("return_date"));

        try {
            loan.setBookTitle(rs.getString("book_title"));
        } catch (SQLException e) {
            loan.setBookTitle("Невідома книга");
        }

        try {
            loan.setReaderName(rs.getString("reader_name"));
        } catch (SQLException e) {
            loan.setReaderName("Невідомий читач");
        }

        return loan;
    }
}