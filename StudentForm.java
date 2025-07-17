import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.io.FileWriter;
import java.util.ArrayList;

public class StudentForm extends JFrame {
    private JTextField idField, nameField, ageField, courseField, marksField, searchField;
    private JTable table;
    private DefaultTableModel model;
    private StudentDBManager dbManager;

    public StudentForm() {
        dbManager = new StudentDBManager();
        setTitle("Student Record Management");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
        Color primary = new Color(60, 63, 65);
        Color accent = new Color(0, 120, 215);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        inputPanel.setBackground(Color.white);

        idField = new JTextField();
        nameField = new JTextField();
        ageField = new JTextField();
        courseField = new JTextField();
        marksField = new JTextField();

        JLabel[] labels = {
            new JLabel("ID:"), new JLabel("Name:"),
            new JLabel("Age:"), new JLabel("Course:"), new JLabel("Marks:")
        };
        JTextField[] fields = {idField, nameField, ageField, courseField, marksField};

        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(labelFont);
            inputPanel.add(labels[i]);
            fields[i].setFont(fieldFont);
            inputPanel.add(fields[i]);
        }

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        JButton addBtn = createStyledButton("Add");
        JButton updateBtn = createStyledButton("Update");
        JButton deleteBtn = createStyledButton("Delete");
        JButton clearBtn = createStyledButton("Clear");
        JButton exportBtn = createStyledButton("Export CSV");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(exportBtn);

        inputPanel.add(new JLabel());
        inputPanel.add(buttonPanel);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Course", "Marks"}, 0);
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        searchField = new JTextField();
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        add(searchPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        refreshTable();

        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearFields());
        exportBtn.addActionListener(e -> exportCSV());
        table.getSelectionModel().addListSelectionListener(e -> fillFieldsFromTable());

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void addStudent() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String course = courseField.getText();
            double marks = Double.parseDouble(marksField.getText());
            if (marks < 0 || marks > 100) throw new IllegalArgumentException("Marks must be between 0-100");
            dbManager.addStudent(new Student(id, name, age, course, marks));
            refreshTable();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateStudent() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String course = courseField.getText();
            double marks = Double.parseDouble(marksField.getText());
            dbManager.updateStudent(new Student(id, name, age, course, marks));
            refreshTable();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            int id = Integer.parseInt(idField.getText());
            dbManager.deleteStudent(id);
            refreshTable();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        idField.setText(""); nameField.setText(""); ageField.setText(""); courseField.setText(""); marksField.setText("");
    }

    private void fillFieldsFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            idField.setText(model.getValueAt(row, 0).toString());
            nameField.setText(model.getValueAt(row, 1).toString());
            ageField.setText(model.getValueAt(row, 2).toString());
            courseField.setText(model.getValueAt(row, 3).toString());
            marksField.setText(model.getValueAt(row, 4).toString());
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Student s : dbManager.getAllStudents()) {
            model.addRow(new Object[]{s.getId(), s.getName(), s.getAge(), s.getCourse(), s.getMarks()});
        }
    }

    private void exportCSV() {
        try (FileWriter fw = new FileWriter("students_export.csv")) {
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    fw.write(model.getValueAt(i, j).toString() + (j < model.getColumnCount() - 1 ? "," : ""));
                }
                fw.write("\n");
            }
            JOptionPane.showMessageDialog(this, "Exported successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage());
        }
    }

    private void filterTable() {
        String keyword = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
    }
}
