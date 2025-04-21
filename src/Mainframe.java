import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;

public class Mainframe extends JFrame {

    private JTextField descriptionField, amountField;
    private JTextArea transactionArea;
    private JLabel balanceLabel;

    private double balance = 0.0;
    private ArrayList<String> transactions = new ArrayList<>();

    private final String FILE_NAME = "transactions.txt";

    public Mainframe() {
        setTitle("💰 Income and Expense Tracker");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // --- Input: Description ---
        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(30, 20, 100, 25);
        add(descLabel);

        descriptionField = new JTextField();
        descriptionField.setBounds(130, 20, 300, 25);
        add(descriptionField);

        // --- Input: Amount ---
        JLabel amtLabel = new JLabel("Amount ($):");
        amtLabel.setBounds(30, 60, 100, 25);
        add(amtLabel);

        amountField = new JTextField();
        amountField.setBounds(130, 60, 300, 25);
        add(amountField);

        // --- Buttons ---
        JButton incomeBtn = new JButton("Add Income");
        incomeBtn.setBounds(130, 100, 120, 30);
        add(incomeBtn);

        JButton expenseBtn = new JButton("Add Expense");
        expenseBtn.setBounds(260, 100, 120, 30);
        add(expenseBtn);

        // --- Transaction Area ---
        transactionArea = new JTextArea();
        transactionArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(transactionArea);
        scrollPane.setBounds(30, 150, 400, 200);
        add(scrollPane);

        // --- Balance Label ---
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setBounds(30, 370, 300, 25);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(balanceLabel);

        // --- Action Listeners ---
        incomeBtn.addActionListener(e -> addTransaction(true));
        expenseBtn.addActionListener(e -> addTransaction(false));

        // Load existing data
        loadDataFromFile();

        setVisible(true);
    }

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
            String entry = String.format("%s: %s | $%.2f", type, desc, Math.abs(amt));
            transactions.add(entry);
            updateUI();

            // Clear input fields
            descriptionField.setText("");
            amountField.setText("");

            // Save to file
            saveDataToFile();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    private void updateUI() {
        transactionArea.setText("");
        for (String t : transactions) {
            transactionArea.append(t + "\n");
        }
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
    }

    private void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String transaction : transactions) {
                writer.write(transaction);
                writer.newLine();
            }
            writer.write("BALANCE=" + balance);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

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
            updateUI();
        } catch (FileNotFoundException e) {
            // Ignore if first time running
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }
}
