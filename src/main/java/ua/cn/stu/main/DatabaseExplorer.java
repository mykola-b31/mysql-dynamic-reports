package ua.cn.stu.main;

import ch.unibas.medizin.dynamicreports.report.builder.VariableBuilder;
import ch.unibas.medizin.dynamicreports.report.builder.column.TextColumnBuilder;
import ch.unibas.medizin.dynamicreports.report.constant.Calculation;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ua.cn.stu.domain.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static ch.unibas.medizin.dynamicreports.report.builder.DynamicReports.*;

public class DatabaseExplorer extends JFrame {
    private static DefaultListModel<Author> authorListModel;
    private static DefaultListModel<Book> bookListModel;
    private static DefaultListModel<Hall> hallListModel;
    private static DefaultListModel<Librarian> librarianListModel;
    private static DefaultListModel<Loan> loanListModel;
    private static DefaultListModel<Reader> readerListModel;

    private static DriverManagerDataSource dataSource;

    private JPanel contentPane;
    private JTabbedPane tabbedPane1;

    private JList<Book> bookList;
    private JTextField bookTitleField;
    private JTextField bookPublicationYearField;
    private JTextField bookCopiesField;
    private JComboBox<Author> bookAuthorComboBox;
    private JComboBox<Hall> bookHallComboBox;
    private JButton addBookButton;

    private JList<Author> authorList;
    private JTextField authorNameField;
    private JTextField authorBirthDateField;
    private JTextField authorNationalityField;
    private JButton addAuthorButton;

    private JList<Hall> hallList;
    private JTextField hallNameField;
    private JTextField hallFloorField;
    private JComboBox<Librarian> hallLibrarianComboBox;
    private JButton addHallButton;

    private JList<Reader> readerList;
    private JTextField readerNameField;
    private JTextField readerPhoneField;
    private JTextField readerEmailField;
    private JTextField readerRegistrationField;
    private JButton addReaderButton;

    private JList<Librarian> librarianList;
    private JTextField librarianNameField;
    private JTextField librarianHireField;
    private JTextField librarianSalaryField;
    private JComboBox<LibrarianPosition> librarianPositionComboBox;
    private JButton addLibrarianButton;

    private JList<Loan> loanList;
    private JComboBox<Reader> loanReaderComboBox;
    private JComboBox<Book> loanBookComboBox;
    private JButton addLoanButton;
    private JTextField loanIssueField;
    private JTextField loanReturnField;
    private JButton librarianSimpleReportButton;
    private JButton statisticReportButton;
    private JButton crossReportButton;

    public DatabaseExplorer() {
        setContentPane(contentPane);
        setTitle("Database Explorer - Library");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        refreshData();
        setVisible(true);
        addAuthorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAuthor();
            }
        });

        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        addHallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addHall();
            }
        });

        addLibrarianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLibrarian();
            }
        });

        addLoanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLoan();
            }
        });

        addReaderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addReader();
            }
        });

        librarianSimpleReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateLibrarianSimpleReport();
            }
        });
    }

    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.load();

            String url = dotenv.get("DB_URL");
            String user = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            dataSource = connectToDatabaseJDBCTemplate(url, user, password);

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new DatabaseExplorer();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Database connection error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
        }
    }

    private static DriverManagerDataSource connectToDatabaseJDBCTemplate(String url, String name, String password) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(name);
        dataSource.setPassword(password);
        return dataSource;
    }

    private Date parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return null;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private Date parseDateTime(String dateTimeStr) {
        try {
            if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
                return null;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setLenient(false);
            return formatter.parse(dateTimeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    // AUTHOR METHODS

    private static List<Author> getAllAuthors() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select * from author", new AuthorMapper());
    }

    private static void addAuthorToDB(String name, Date birthDate, String nationality) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("insert into author(name, birth_date, nationality) values (?, ?, ?)",
                name, birthDate, nationality);
    }

    private void addAuthor() {
        try {
            String name = authorNameField.getText();
            String birthDateStr = authorBirthDateField.getText();
            String nationality = authorNationalityField.getText();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(contentPane,
                        "Fill in required fields",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date birthDate = parseDate(birthDateStr);

            addAuthorToDB(name, birthDate, nationality);

            authorNameField.setText("");
            authorBirthDateField.setText("");
            authorNationalityField.setText("");

            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(contentPane,
                    "Error adding author: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // BOOK METHODS

    private static List<Book> getAllBooks() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String query = "SELECT b.*, a.name as author_name, h.name as hall_name " +
                "FROM book b " +
                "LEFT JOIN author a ON b.author_id = a.author_id " +
                "LEFT JOIN hall h ON b.hall_id = h.hall_id " +
                "ORDER BY h.name, b.title";
        return jdbcTemplate.query(query, new BookMapper());
    }

    private static void addBookToDB(String title, Integer pubYear, Integer copies, Integer authorId, Integer hallId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("INSERT INTO book(title, publication_year, copies, author_id, hall_id) VALUES (?, ?, ?, ?, ?)",
                title, pubYear, copies, authorId, hallId);
    }

    private void addBook() {
        try {
            String title = bookTitleField.getText();
            String pubYearStr = bookPublicationYearField.getText();
            String copiesStr = bookCopiesField.getText();
            Author author = (Author) bookAuthorComboBox.getSelectedItem();
            Hall hall = (Hall) bookHallComboBox.getSelectedItem();

            if (title.isEmpty() || pubYearStr.isEmpty() || copiesStr.isEmpty() || author == null || hall == null) {
                JOptionPane.showMessageDialog(contentPane,
                        "Fill in all required fields",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer pubYear = Integer.parseInt(pubYearStr);
            Integer copies = Integer.parseInt(copiesStr);

            addBookToDB(title, pubYear, copies, author.getAuthorId(), hall.getHallId());

            bookTitleField.setText("");
            bookPublicationYearField.setText("");
            bookCopiesField.setText("");

            refreshData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(contentPane, "Рік та Кількість мають бути числами", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(contentPane, "Error adding book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // HALL METHODS

    private static List<Hall> getAllHalls() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String query = "SELECT h.*, l.name as librarian_name " +
                "FROM hall h " +
                "LEFT JOIN librarian l ON h.librarian_id = l.librarian_id";
        return jdbcTemplate.query(query, new HallMapper());
    }

    private static void addHallToDB(String name, Integer floor, Integer librarianId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("INSERT INTO hall(name, floor, librarian_id) VALUES (?, ?, ?)",
                name, floor, librarianId);
    }

    private void addHall() {
        try {
            String name = hallNameField.getText();
            String floorStr = hallFloorField.getText();
            Librarian librarian = (Librarian) hallLibrarianComboBox.getSelectedItem();

            if (name.isEmpty() || floorStr.isEmpty()) {
                JOptionPane.showMessageDialog(contentPane, "Назва та Поверх є обов'язковими", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer floor = Integer.parseInt(floorStr);
            Integer librarianId = (librarian != null) ? librarian.getLibrarianId() : null;

            addHallToDB(name, floor, librarianId);

            hallNameField.setText("");
            hallFloorField.setText("");

            refreshData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(contentPane, "Поверх має бути числом", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(contentPane, "Error adding hall: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static List<Librarian> getAllLibrarians() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("SELECT * FROM librarian", new LibrarianMapper());
    }

    private static void addLibrarianToDB(String name, LibrarianPosition position, Date hireDate, BigDecimal salary) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("INSERT INTO librarian(name, position, hire_date, salary) VALUES (?, ?, ?, ?)",
                name, position.toString(), hireDate, salary);
    }

    private void addLibrarian() {
        try {
            String name = librarianNameField.getText();
            String hireDateStr = librarianHireField.getText();
            String salaryStr = librarianSalaryField.getText();
            LibrarianPosition position = (LibrarianPosition) librarianPositionComboBox.getSelectedItem();

            if (name.isEmpty() || position == null) {
                JOptionPane.showMessageDialog(contentPane, "Ім'я та Посада є обов'язковими", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date hireDate = parseDate(hireDateStr);
            BigDecimal salary = (salaryStr.isEmpty()) ? null : new BigDecimal(salaryStr);

            addLibrarianToDB(name, position, hireDate, salary);

            librarianNameField.setText("");
            librarianHireField.setText("");
            librarianSalaryField.setText("");

            refreshData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(contentPane, "Зарплатня має бути числом", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(contentPane, "Error adding librarian: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // LOAN METHODS

    private static List<Loan> getAllLoans() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String query = "SELECT l.*, r.name as reader_name, b.title as book_title " +
                "FROM loan l " +
                "LEFT JOIN reader r ON l.reader_id = r.reader_id " +
                "LEFT JOIN book b ON l.book_id = b.book_id " +
                "ORDER BY l.issue_date DESC";
        return jdbcTemplate.query(query, new LoanMapper());
    }

    private static void addLoanToDB(Integer readerId, Integer bookId, Date issueDate, Date returnDate) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Timestamp issueTimeStamp = new Timestamp(issueDate.getTime());
        Timestamp returnTimeStamp = (returnDate != null) ? new Timestamp(returnDate.getTime()) : null;
        jdbcTemplate.update("INSERT INTO loan(reader_id, book_id, issue_date, return_date) VALUES (?, ?, ?, ?)",
                readerId, bookId, issueTimeStamp, returnTimeStamp);
    }

    private void addLoan() {
        try {
            Reader reader = (Reader) loanReaderComboBox.getSelectedItem();
            Book book = (Book) loanBookComboBox.getSelectedItem();
            String issueDateStr = loanIssueField.getText();
            String returnDateStr = loanReturnField.getText();

            if (reader == null || book == null) {
                JOptionPane.showMessageDialog(contentPane, "Оберіть Читача та Книгу", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date issueDate = parseDateTime(issueDateStr);
            if (issueDate == null) {
                if (issueDateStr.isEmpty()) {
                    issueDate = new Date();
                } else {
                    JOptionPane.showMessageDialog(contentPane, "Некоректний формат дати видачі. Використовуйте: yyyy-MM-dd HH:mm:ss", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Date returnDate = parseDateTime(returnDateStr);
            if (returnDate == null && !returnDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(contentPane, "Некоректний формат дати повернення. Використовуйте: yyyy-MM-dd HH:mm:ss", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            addLoanToDB(reader.getReaderId(), book.getBookId(), issueDate, returnDate);

            loanIssueField.setText("");
            loanReturnField.setText("");

            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(contentPane, "Error adding loan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // READER METHODS

    private static List<Reader> getAllReaders() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("SELECT * FROM reader", new ReaderMapper());
    }

    private static void addReaderToDB(String name, String phone, String email, Date regDate) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("INSERT INTO reader(name, phone, email, registration_date) VALUES (?, ?, ?, ?)",
                name, phone, email, regDate);
    }

    private void addReader() {
        try {
            String name = readerNameField.getText();
            String phone = readerPhoneField.getText();
            String email = readerEmailField.getText();
            String regDateStr = readerRegistrationField.getText();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(contentPane, "Ім'я читача є обов'язковим", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date regDate = parseDate(regDateStr);
            if (regDate == null) {
                regDate = new Date();
            }

            addReaderToDB(name, phone, email, regDate);

            readerNameField.setText("");
            readerPhoneField.setText("");
            readerEmailField.setText("");
            readerRegistrationField.setText("");

            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(contentPane, "Error adding reader: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        List<Author> authors = getAllAuthors();
        authorListModel.removeAllElements();
        if (bookAuthorComboBox != null) bookAuthorComboBox.removeAllItems();
        for (Author author : authors) {
            authorListModel.addElement(author);
            if (bookAuthorComboBox != null) bookAuthorComboBox.addItem(author);
        }

        List<Hall> halls = getAllHalls();
        hallListModel.removeAllElements();
        if (bookHallComboBox != null) bookHallComboBox.removeAllItems();
        for (Hall hall : halls) {
            hallListModel.addElement(hall);
            if (bookHallComboBox != null) bookHallComboBox.addItem(hall);
        }

        List<Librarian> librarians = getAllLibrarians();
        librarianListModel.removeAllElements();
        if (hallLibrarianComboBox != null) hallLibrarianComboBox.removeAllItems();
        for (Librarian librarian : librarians) {
            librarianListModel.addElement(librarian);
            if (hallLibrarianComboBox != null) hallLibrarianComboBox.addItem(librarian);
        }


        List<Reader> readers = getAllReaders();
        readerListModel.removeAllElements();
        if (loanReaderComboBox != null) loanReaderComboBox.removeAllItems();
        for (Reader reader : readers) {
            readerListModel.addElement(reader);
            if (loanReaderComboBox != null) loanReaderComboBox.addItem(reader);
        }

        List<Book> books = getAllBooks();
        bookListModel.removeAllElements();
        if (loanBookComboBox != null) loanBookComboBox.removeAllItems();
        for (Book book : books) {
            bookListModel.addElement(book);
            if (loanBookComboBox != null) loanBookComboBox.addItem(book);
        }

        List<Loan> loans = getAllLoans();
        loanListModel.removeAllElements();
        for (Loan loan : loans) {
            loanListModel.addElement(loan);
        }

        if (librarianPositionComboBox != null) {
            librarianPositionComboBox.removeAllItems();
            for (LibrarianPosition pos : LibrarianPosition.values()) {
                librarianPositionComboBox.addItem(pos);
            }
        }
    }

    // REPORTS METHODS

    private void generateLibrarianSimpleReport() {
        try {
            List<Librarian> librarianList = getAllLibrarians();
            JRDataSource jrDataSource = new JRBeanCollectionDataSource(librarianList);

            TextColumnBuilder<String> nameColumn = col.column("Name", "name", type.stringType());
            TextColumnBuilder<String> positionColumn = col.column("Position", "positionString", type.stringType());
            TextColumnBuilder<Date> hireDateColumn = col.column("Hire date", "hireDate", type.dateType()).setPattern("dd.MM.yyyy");
            TextColumnBuilder<BigDecimal> salaryColumn = col.column("Salary", "salary", type.bigDecimalType()).setPattern("#,##0.00 'UAH'");

            VariableBuilder<Integer> staffCount = variable(nameColumn, Calculation.COUNT);
            VariableBuilder<BigDecimal> salarySum = variable(salaryColumn, Calculation.SUM);

            report()
                    .setColumnTitleStyle(Templates.columnTitleStyle)
                    .variables(staffCount, salarySum)
                    .columns(
                            nameColumn,
                            positionColumn,
                            hireDateColumn,
                            salaryColumn
                    )
                    .title(cmp.text("Simple report on library staff").setStyle(Templates.boldCenteredStyle))
                    .pageFooter(cmp.pageXofY())
                    .summary(
                            cmp.verticalGap(20),
                            cmp.line(),
                            cmp.verticalGap(10),
                            cmp.horizontalList(
                                    cmp.text("Total number of employees:").setStyle(Templates.boldStyle),
                                    cmp.horizontalGap(10),
                                    cmp.text(staffCount)
                            ),

                            cmp.horizontalList(
                                    cmp.text("Total salary fund:").setStyle(Templates.boldStyle),
                                    cmp.horizontalGap(10),
                                    cmp.text(salarySum).setPattern("#,##0.00 'UAH'")
                            )
                    )
                    .setDataSource(jrDataSource)
                    .show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(contentPane, "Report error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1 = new JTabbedPane();
        contentPane.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Book", panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        bookList = new JList<Book>();
        bookListModel = new DefaultListModel<Book>();
        bookList.setModel(bookListModel);
        scrollPane1.setViewportView(bookList);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(11, 1, new Insets(0, 0, 0, 7), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Book Title");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bookTitleField = new JTextField();
        panel2.add(bookTitleField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Publication Year");
        panel2.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bookPublicationYearField = new JTextField();
        panel2.add(bookPublicationYearField, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Copies");
        panel2.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bookCopiesField = new JTextField();
        panel2.add(bookCopiesField, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Author");
        panel2.add(label4, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bookAuthorComboBox = new JComboBox<Author>();
        panel2.add(bookAuthorComboBox, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Hall");
        panel2.add(label5, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bookHallComboBox = new JComboBox<Hall>();
        panel2.add(bookHallComboBox, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addBookButton = new JButton();
        addBookButton.setText("Add Book");
        panel2.add(addBookButton, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Author", panel3);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel3.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        authorList = new JList<Author>();
        authorListModel = new DefaultListModel<Author>();
        authorList.setModel(authorListModel);
        scrollPane2.setViewportView(authorList);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 7), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Author Name");
        panel4.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        authorNameField = new JTextField();
        panel4.add(authorNameField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Birth Date");
        panel4.add(label7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        authorBirthDateField = new JTextField();
        panel4.add(authorBirthDateField, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Nationality");
        panel4.add(label8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        authorNationalityField = new JTextField();
        panel4.add(authorNationalityField, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        addAuthorButton = new JButton();
        addAuthorButton.setText("Add Author");
        panel4.add(addAuthorButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Hall", panel5);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel5.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        hallList = new JList<Hall>();
        hallListModel = new DefaultListModel<Hall>();
        hallList.setModel(hallListModel);
        scrollPane3.setViewportView(hallList);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 7), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Hall Name");
        panel6.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hallNameField = new JTextField();
        panel6.add(hallNameField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Floor");
        panel6.add(label10, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hallFloorField = new JTextField();
        panel6.add(hallFloorField, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Librarian");
        panel6.add(label11, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hallLibrarianComboBox = new JComboBox<Librarian>();
        panel6.add(hallLibrarianComboBox, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addHallButton = new JButton();
        addHallButton.setText("Add Hall");
        panel6.add(addHallButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Reader", panel7);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel7.add(scrollPane4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        readerList = new JList<Reader>();
        readerListModel = new DefaultListModel<Reader>();
        readerList.setModel(readerListModel);
        scrollPane4.setViewportView(readerList);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 7), -1, -1));
        panel7.add(panel8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Reader Name");
        panel8.add(label12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        readerNameField = new JTextField();
        panel8.add(readerNameField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Phone Number");
        panel8.add(label13, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        readerPhoneField = new JTextField();
        panel8.add(readerPhoneField, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Email");
        panel8.add(label14, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        readerEmailField = new JTextField();
        panel8.add(readerEmailField, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        addReaderButton = new JButton();
        addReaderButton.setText("Add Reader");
        panel8.add(addReaderButton, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Registration Date");
        panel8.add(label15, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        readerRegistrationField = new JTextField();
        panel8.add(readerRegistrationField, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Librarian", panel9);
        final JScrollPane scrollPane5 = new JScrollPane();
        panel9.add(scrollPane5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        librarianList = new JList<Librarian>();
        librarianListModel = new DefaultListModel<Librarian>();
        librarianList.setModel(librarianListModel);
        scrollPane5.setViewportView(librarianList);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 7), -1, -1));
        panel9.add(panel10, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("Librarian Name");
        panel10.add(label16, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        librarianNameField = new JTextField();
        panel10.add(librarianNameField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("Hire Date");
        panel10.add(label17, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        librarianHireField = new JTextField();
        panel10.add(librarianHireField, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("Salary");
        panel10.add(label18, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        librarianSalaryField = new JTextField();
        panel10.add(librarianSalaryField, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        addLibrarianButton = new JButton();
        addLibrarianButton.setText("Add Librarian");
        panel10.add(addLibrarianButton, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        librarianPositionComboBox = new JComboBox<LibrarianPosition>();
        panel10.add(librarianPositionComboBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("Position");
        panel10.add(label19, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Loan", panel11);
        final JScrollPane scrollPane6 = new JScrollPane();
        panel11.add(scrollPane6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        loanList = new JList<Loan>();
        loanListModel = new DefaultListModel<Loan>();
        loanList.setModel(loanListModel);
        scrollPane6.setViewportView(loanList);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 7), -1, -1));
        panel11.add(panel12, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("Issue Date");
        panel12.add(label20, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loanIssueField = new JTextField();
        panel12.add(loanIssueField, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label21 = new JLabel();
        label21.setText("Return Date");
        panel12.add(label21, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loanReturnField = new JTextField();
        panel12.add(loanReturnField, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        addLoanButton = new JButton();
        addLoanButton.setText("Add Loan");
        panel12.add(addLoanButton, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label22 = new JLabel();
        label22.setText("Reader");
        panel12.add(label22, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loanReaderComboBox = new JComboBox<Reader>();
        panel12.add(loanReaderComboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label23 = new JLabel();
        label23.setText("Book");
        panel12.add(label23, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loanBookComboBox = new JComboBox<Book>();
        panel12.add(loanBookComboBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(3, 1, new Insets(15, 24, 15, 24), -1, -1));
        tabbedPane1.addTab("Reports", panel13);
        librarianSimpleReportButton = new JButton();
        librarianSimpleReportButton.setText("Generate a Simple Report on Librarians");
        panel13.add(librarianSimpleReportButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statisticReportButton = new JButton();
        statisticReportButton.setText("Button");
        panel13.add(statisticReportButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        crossReportButton = new JButton();
        crossReportButton.setText("Button");
        panel13.add(crossReportButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
