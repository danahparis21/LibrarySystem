package librarysystem;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class DataAnalytics extends JFrame {
    private Connection connection;

    public DataAnalytics() {
        setTitle("Library Data Analytics");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Create charts
        JFreeChart borrowedBooksChart = createBarChart("Most Borrowed Books", "Book Title", "Times Borrowed", getBorrowedBooksData());
        JFreeChart reservedBooksChart = createBarChart("Most Reserved Books", "Book Title", "Reservations", getReservedBooksData());
        JFreeChart genreChart = createPieChart("Popular Book Genres", getGenreData());

        // Add charts to panels and set positions
        addChart(borrowedBooksChart, 50, 20, 600, 400);
        addChart(reservedBooksChart, 700, 20, 800, 400);
        addChart(genreChart, 80, 450, 300, 300);

        // Add low stock books list
        addLowStockList(700, 470, 500, 300);

        setVisible(true);
    }

    private JFreeChart createBarChart(String title, String categoryLabel, String valueLabel, DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(title, categoryLabel, valueLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        return chart;
    }

    private JFreeChart createPieChart(String title, DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        return chart;
    }

    private void addChart(JFreeChart chart, int x, int y, int width, int height) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setBounds(x, y, width, height);
        add(panel);
    }

    private void addLowStockList(int x, int y, int width, int height) {
        JLabel label = new JLabel("LOW STOCKS (NEEDS RESTOCK)");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBounds(x, y - 30, width, 30);
        add(label);

        JList<String> lowStockList = new JList<>(getLowStockData());
        JScrollPane scrollPane = new JScrollPane(lowStockList);
        scrollPane.setBounds(x, y, width, height);
        add(scrollPane);
    }

    private DefaultCategoryDataset getBorrowedBooksData() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String query = "SELECT b.title, COUNT(bb.bookID) AS borrowed " +
                       "FROM BorrowedBooks bb " +
                       "JOIN Books b ON bb.bookID = b.bookID " +
                       "GROUP BY b.title ORDER BY borrowed DESC LIMIT 5";
        try (Connection con = getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                dataset.addValue(rs.getInt("borrowed"), "Borrowed", rs.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }

    private DefaultCategoryDataset getReservedBooksData() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String query = "SELECT b.title, COUNT(r.bookID) AS reserved " +
                       "FROM Reservations r " +
                       "JOIN Books b ON r.bookID = b.bookID " +
                       "GROUP BY b.title ORDER BY reserved DESC LIMIT 5";
        try (Connection con = getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                dataset.addValue(rs.getInt("reserved"), "Reserved", rs.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }

    private DefaultPieDataset getGenreData() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        String query = "SELECT genre, COUNT(*) AS count FROM Books GROUP BY genre";
        try (Connection con = getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                dataset.setValue(rs.getString("genre"), rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }

    private String[] getLowStockData() {
        ArrayList<String> lowStockBooks = new ArrayList<>();
        String query = "SELECT title, quantity FROM Books WHERE quantity < 5 ORDER BY quantity ASC";
        try (Connection con = getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                lowStockBooks.add(rs.getString("title") + " (" + rs.getInt("quantity") + " left)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lowStockBooks.toArray(new String[0]);
    }

    private Connection getConnection() throws SQLException {
        return Database.connect();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataAnalytics());
    }
}
