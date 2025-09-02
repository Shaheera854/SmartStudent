import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UI.setLookAndFeel();
            } catch (Exception ignored) {}

            UI ui = new UI();
            try {
                ui.getDao().ensureTableExists();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ui.showLogin();
        });
    }
}
