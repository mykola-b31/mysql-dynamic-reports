package ua.cn.stu.main;

import org.springframework.jdbc.core.RowMapper;
import ua.cn.stu.domain.Book;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookMapper implements RowMapper<Book> {
    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setAuthorId(rs.getInt("author_id"));
        book.setCopies(rs.getInt("copies"));
        book.setHallId(rs.getInt("hall_id"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setTitle(rs.getString("title"));

        try {
            book.setAuthorName(rs.getString("author_name"));
        } catch (SQLException e) {
            book.setAuthorName("Невідомий автор");
        }

        try {
            book.setHallName(rs.getString("hall_name"));
        } catch (SQLException e) {
            book.setHallName("Невідомий зал");
        }

        return book;
    }
}