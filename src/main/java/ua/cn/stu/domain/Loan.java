package ua.cn.stu.domain;

import java.util.Date;

public class Loan {
    private Integer loanId;
    private Integer bookId;
    private Integer readerId;
    private Date issueDate;
    private Date returnDate;

    private String bookTitle;
    private String readerName;

    public Loan() {}

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getReaderId() {
        return readerId;
    }

    public void setReaderId(Integer readerId) {
        this.readerId = readerId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    @Override
    public String toString() {
        String reader = readerName != null ? readerName : "ID:" + readerId;
        String book = bookTitle != null ? bookTitle : "ID:" + bookId;
        String returned = returnDate != null ? " (повернено)" : " (на руках)";
        return "Видача №" + loanId + ": " + reader + " -> " + book + returned;
    }
}