import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


// Main class that creates a GUI for tracking income and expenses
public class Mainframe extends JFrame {

    // GUI components for user input and display
    private JTextField descriptionField, amountField;
    private JEditorPane transactionArea;
    private JLabel balanceLabel;

    // Data variables
    private double balance = 0.0;
    private ArrayList<String> transactions = new ArrayList<>();

    private final String FILE_NAME = "transactions.txt"; // File to save data

    // Constructor to build the user interface
    public Mainframe() {
        setTitle("💰 Income and Expense Tracker");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // --- Input Fields ---
        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(30, 20, 100, 25);
        add(descLabel);

        descriptionField = new JTextField();
        descriptionField.setBounds(130, 20, 300, 25);
        add(descriptionField);

        JLabel amtLabel = new JLabel("Amount ($):");
        amtLabel.setBounds(30, 60, 100, 25);
        add(amtLabel);

        amountField = new JTextField();
        amountField.setBounds(130, 60, 300, 25);
        add(amountField);

        // --- Buttons for adding income and expense ---
        JButton incomeBtn = new JButton("Add Income");
        incomeBtn.setBounds(130, 100, 120, 30);
        add(incomeBtn);

        JButton expenseBtn = new JButton("Add Expense");
        expenseBtn.setBounds(260, 100, 120, 30);
        add(expenseBtn);

        // --- Area to display transactions ---
        transactionArea = new JEditorPane();
        transactionArea.setContentType("text/html"); // Enables colored text
        transactionArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(transactionArea);
        scrollPane.setBounds(30, 150, 400, 200);
        add(scrollPane);

        // --- Displays current balance ---
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setBounds(30, 370, 300, 25);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(balanceLabel);

        // --- Action Listeners for buttons ---
        incomeBtn.addActionListener(e -> addTransaction(true));
        expenseBtn.addActionListener(e -> addTransaction(false));

        // Load saved data from file
        loadDataFromFile();

        // --- Clear all button ---
        JButton clearBtn = new JButton("Clear All");
        clearBtn.setBounds(180, 410, 120, 30);
        add(clearBtn);

        // --- Calendar Button ---
JButton calendarBtn = new JButton("View Calendar");
calendarBtn.setBounds(310, 410, 140, 30);
add(calendarBtn);

// Add action to open calendar page
calendarBtn.addActionListener(e -> openCalendarPage());


        // Add functionality to clear button
        clearBtn.addActionListener(e -> clearAllData());

        // Make window visible
        setVisible(true);

        
    }

    // Adds a new income or expense to the tracker
    private void addTransaction(boolean isIncome) {
        String desc = descriptionField.getText().trim();
        String amtText = amountField.getText().trim();
    
        if (desc.isEmpty() || amtText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both description and amount.");
            return;
        }
    
        try {
            double amt = Double.parseDouble(amtText);
            if (!isIncome) amt = -amt;
    
            balance += amt;
            String type = isIncome ? "Income" : "Expense";
    
            // Get current timestamp
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);
    
            // 📌 Place the timestamp *at the end* now
            String entry = String.format("%s: %s | $%.2f [%s]", type, desc, Math.abs(amt), timestamp);
            transactions.add(entry);
    
            updateUI();
    
            descriptionField.setText("");
            amountField.setText("");
    
            saveDataToFile();
    
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }
    
    

    // Updates the transaction display and balance
    private void updateUI() {
        StringBuilder html = new StringBuilder("<html><body style='font-family: Arial;'>");
    
        for (String t : transactions) {
            if (t.contains("Income:")) {
                html.append("<span style='color: green;'>").append(t).append("</span><br>");
            } else if (t.contains("Expense:")) {
                html.append("<span style='color: red;'>").append(t).append("</span><br>");
            } else {
                html.append(t).append("<br>");
            }
        }
    
        html.append("</body></html>");
        transactionArea.setText(html.toString());
    
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
    }
    

    // Saves transaction history and balance to a file
    private void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String transaction : transactions) {
                writer.write(transaction);
                writer.newLine();
            }
            writer.write("BALANCE=" + balance); // Save balance at the end
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    // Loads saved data from file when the app starts
    private void loadDataFromFile() {
        transactions.clear();
        balance = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("BALANCE=")) {
                    balance = Double.parseDouble(line.substring(8));
                } else {
                    transactions.add(line);
                }
            }
            updateUI(); // Refresh UI after loading
        } catch (FileNotFoundException e) {
            // File not found is okay on first run
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    // Clears all data and resets the application
    private void clearAllData() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear all data?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            transactions.clear();
            balance = 0.0;
            updateUI(); // Reset display

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                writer.write(""); // Overwrite file to clear data
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error clearing file: " + e.getMessage());
            }
        }
    }
    private void openCalendarPage() {
        JFrame calendarFrame = new JFrame("📅 Daily Summary");
        calendarFrame.setSize(400, 300);
        calendarFrame.setLayout(null);
    
        // Label for date
        JLabel dateLabel = new JLabel("Enter date (yyyy-MM-dd):");
        dateLabel.setBounds(30, 30, 200, 25);
        calendarFrame.add(dateLabel);
    
        // Text field to type a date
        JTextField dateField = new JTextField();
        dateField.setBounds(30, 60, 200, 25);
        calendarFrame.add(dateField);
    
        // Button to view earnings/spending
        JButton viewBtn = new JButton("View Summary");
        viewBtn.setBounds(250, 60, 100, 25);
        calendarFrame.add(viewBtn);
    
        // Area to show results
        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setBounds(30, 100, 320, 120);
        calendarFrame.add(scrollPane);
    
        // Action when clicking View
        viewBtn.addActionListener(e -> {
            String selectedDate = dateField.getText().trim();
            if (selectedDate.isEmpty()) {
                JOptionPane.showMessageDialog(calendarFrame, "Please enter a date.");
                return;
            }
    
            double dailyIncome = 0.0;
            double dailyExpense = 0.0;
    
            for (String t : transactions) {
                // Extract the timestamp between [ ]
                int startBracket = t.indexOf('[');
                int endBracket = t.indexOf(']');
                if (startBracket != -1 && endBracket != -1 && endBracket > startBracket) {
                    String timestamp = t.substring(startBracket + 1, endBracket);
                    String transactionDate = timestamp.split(" ")[0]; // Only the date part yyyy-MM-dd
            
                    // Now compare the extracted date
                    if (transactionDate.equals(selectedDate)) {
                        if (t.contains("Income:") || t.contains("Expense:")) {
                            String[] parts = t.split("\\|");
                            if (parts.length > 1) {
                                String amountPart = parts[1].trim();
                                if (amountPart.startsWith("$")) {
                                    int spaceIndex = amountPart.indexOf(' ');
                                    String amountString;
                                    if (spaceIndex > 0) {
                                        amountString = amountPart.substring(1, spaceIndex);
                                    } else {
                                        amountString = amountPart.substring(1);
                                    }
            
                                    try {
                                        double amt = Double.parseDouble(amountString);
                                        if (t.contains("Income:")) {
                                            dailyIncome += amt;
                                        } else if (t.contains("Expense:")) {
                                            dailyExpense += amt;
                                        }
                                    } catch (NumberFormatException ex) {
                                        // skip if broken
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            
    
            summaryArea.setText(String.format(
                "Summary for %s:\n\nTotal Income: $%.2f\nTotal Expense: $%.2f\nNet: $%.2f",
                selectedDate, dailyIncome, dailyExpense, (dailyIncome - dailyExpense)
            ));
        });
    
        calendarFrame.setVisible(true);
    }
    
}
