import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelBookingSystem extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private Connection connection;

    private JComboBox<String> roomComboBox;
    private JTextField guestNameTextField;
    private JTextField checkInTextField;
    private JTextField checkOutTextField;

    public HotelBookingSystem() {
        setTitle("Hotel Booking System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize GUI components
        JPanel mainPanel = new JPanel(new GridLayout(5, 2));
        JLabel roomLabel = new JLabel("Room:");
        roomComboBox = new JComboBox<>();
        JLabel guestNameLabel = new JLabel("Guest Name:");
        guestNameTextField = new JTextField();
        JLabel checkInLabel = new JLabel("Check-in Date:");
        checkInTextField = new JTextField();
        JLabel checkOutLabel = new JLabel("Check-out Date:");
        checkOutTextField = new JTextField();
        JButton reserveButton = new JButton("Reserve Room");

        mainPanel.add(roomLabel);
        mainPanel.add(roomComboBox);
        mainPanel.add(guestNameLabel);
        mainPanel.add(guestNameTextField);
        mainPanel.add(checkInLabel);
        mainPanel.add(checkInTextField);
        mainPanel.add(checkOutLabel);
        mainPanel.add(checkOutTextField);
        mainPanel.add(new JLabel());
        mainPanel.add(reserveButton);

        add(mainPanel);

        // Connect to the database
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            loadRooms();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // ActionListener for reserveButton
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reserveRoom();
            }
        });
    }

    private void loadRooms() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT room_number FROM rooms WHERE is_reserved = FALSE");
            List<String> rooms = new ArrayList<>();
            while (resultSet.next()) {
                rooms.add(resultSet.getString("room_number"));
            }
            roomComboBox.setModel(new DefaultComboBoxModel<>(rooms.toArray(new String[0])));
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load rooms.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

private void reserveRoom() {
    String roomNumber = (String) roomComboBox.getSelectedItem();
    String guestName = guestNameTextField.getText();
    String checkInDate = checkInTextField.getText();
    String checkOutDate = checkOutTextField.getText();

    try {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO reservations (room_number, guest_name, check_in_date, check_out_date) VALUES (?, ?, ?, ?)");
        statement.setString(1, roomNumber);
        statement.setString(2, guestName);
        statement.setString(3, checkInDate);
        statement.setString(4, checkOutDate);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Room reserved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRooms(); // Reload the list of available rooms
            guestNameTextField.setText("");
            checkInTextField.setText("");
            checkOutTextField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reserve room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to reserve room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HotelBookingSystem().setVisible(true);
            }
        });
    }
}