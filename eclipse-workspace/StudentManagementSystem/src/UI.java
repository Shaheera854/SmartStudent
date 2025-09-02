import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.FileWriter;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class UI {

    private final StudentDAO dao = new StudentDAO();
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JLabel lblTotal, lblMax, lblMin, lblDept;

    // filter fields
    private final JTextField tfSearchName = new JTextField(12);
    private final JTextField tfSearchDept = new JTextField(8);
    private final JTextField tfSearchRoll = new JTextField(8);
    private final JFormattedTextField tfMinMarks = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private final JFormattedTextField tfMaxMarks = new JFormattedTextField(NumberFormat.getIntegerInstance());

    public StudentDAO getDao() {
        return dao;
    }

    public static void setLookAndFeel() throws Exception {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }

    public void showLogin() {
        JDialog login = new JDialog((Frame) null, "SmartStudent - Admin Login", true);
        login.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12,12,12,12));

        // Fields
        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx=0; c.gridy=0; fields.add(new JLabel("Username:"), c);
        c.gridy=1; fields.add(new JLabel("Password:"), c);

        JTextField user = new JTextField(14);
        JPasswordField pass = new JPasswordField(14);
        c.gridx=1; c.gridy=0; fields.add(user, c);
        c.gridy=1; fields.add(pass, c);

        root.add(fields, BorderLayout.CENTER);

        // Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");
        actions.add(btnCancel); actions.add(btnLogin);
        root.add(actions, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> System.exit(0));
        btnLogin.addActionListener(e -> {
            if (AuthService.login(user.getText().trim(), new String(pass.getPassword()))) {
                login.dispose();
                createAndShowGUI();
            } else {
                JOptionPane.showMessageDialog(login, "Invalid credentials", "Login failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        login.getContentPane().add(root);
        login.pack();
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }

    public void createAndShowGUI() {
        frame = new JFrame("SmartStudent — Admin Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 640);
        frame.setMinimumSize(new Dimension(700, 450));
        frame.setLocationRelativeTo(null);

        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel top = buildTopPanel();
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(top);

        JPanel center = buildCenterPanel();
        center.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(center);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buildBottomPanel(), BorderLayout.SOUTH);

        refreshTable();
        frame.setVisible(true);
    }

    private JPanel buildTopPanel() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(10, 10, 0, 10));
        top.setOpaque(false);

        // Title
        JLabel title = new JLabel("SmartStudent — Admin Panel");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        JPanel titleHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleHolder.setOpaque(false);
        titleHolder.add(title);
        titleHolder.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(titleHolder);

        // Toolbar
        String[] toolbarCols = { "Add Student", "Edit Student", "Delete Student",
                                 "Export CSV", "Refresh", "Logout", "Show All" };
        Object[][] toolbarData = { toolbarCols };

        DefaultTableModel toolbarModel = new DefaultTableModel(toolbarData, toolbarCols) {
            @Override public boolean isCellEditable(int row, int col) { return true; }
        };

        JTable toolbar = new JTable(toolbarModel);
        toolbar.setRowHeight(40);
        toolbar.setTableHeader(null);
        toolbar.setShowGrid(false);
        toolbar.setIntercellSpacing(new Dimension(0, 0));
        toolbar.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Buttons
        for (int i = 0; i < toolbarCols.length; i++) {
            final int colIndex = i;
            toolbar.getColumnModel().getColumn(i).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                JButton btn = new JButton(value == null ? "" : value.toString());
                btn.setFocusable(false);
                return btn;
            });

            toolbar.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
                private final JButton btn = new JButton();
                {
                    btn.setFocusable(false);
                    btn.addActionListener(e -> {
                        fireEditingStopped();
                        switch (colIndex) {
                            case 0 -> showStudentForm(null);
                            case 1 -> handleEdit();
                            case 2 -> handleDelete();
                            case 3 -> exportCSV();
                            case 4 -> refreshTable();
                            case 5 -> { frame.dispose(); showLogin(); }
                            case 6 -> {
                                tfSearchName.setText("");
                                tfSearchDept.setText("");
                                tfSearchRoll.setText("");
                                tfMinMarks.setValue(null);
                                tfMaxMarks.setValue(null);
                                refreshTable();
                            }
                        }
                    });
                }
                @Override
                public Component getTableCellEditorComponent(JTable table, Object value,
                                                             boolean isSelected, int row, int column) {
                    btn.setText(value == null ? "" : value.toString());
                    return btn;
                }
                @Override
                public Object getCellEditorValue() { return btn.getText(); }
            });
        }

        JScrollPane toolbarScroll = new JScrollPane(toolbar,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        toolbarScroll.setBorder(BorderFactory.createEmptyBorder());
        toolbarScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbarScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, toolbar.getRowHeight() + 4));
        top.add(toolbarScroll);
        return top;
    }

    private JPanel buildCenterPanel() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(new EmptyBorder(10,10,10,10));

        // Filters
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filters.add(new JLabel("Name:")); filters.add(tfSearchName);
        filters.add(new JLabel("Department:")); filters.add(tfSearchDept);
        filters.add(new JLabel("Roll No:")); filters.add(tfSearchRoll);
        filters.add(new JLabel("Marks >= ")); tfMinMarks.setColumns(4); filters.add(tfMinMarks);
        filters.add(new JLabel("<= ")); tfMaxMarks.setColumns(4); filters.add(tfMaxMarks);

        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        filters.add(btnSearch); filters.add(btnClear);
        btnSearch.addActionListener(e -> search());
        btnClear.addActionListener(e -> {
            tfSearchName.setText(""); tfSearchDept.setText(""); tfSearchRoll.setText("");
            tfMinMarks.setValue(null); tfMaxMarks.setValue(null); refreshTable();
        });

        p.add(filters, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID","Name","Roll No","Department","Email","Phone","Marks"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                if (columnIndex == 6) return Integer.class;
                return String.class;
            }
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JScrollPane sp = new JScrollPane(table);
        p.add(sp, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildBottomPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(6,12,12,12));
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        lblTotal = new JLabel("Total: -");
        lblMax = new JLabel("Max Marks: -");
        lblMin = new JLabel("Min Marks: -");
        lblDept = new JLabel("Dept Count: -");
        stats.add(lblTotal); stats.add(lblMax); stats.add(lblMin); stats.add(lblDept);
        p.add(stats, BorderLayout.WEST);
        return p;
    }

    public void refreshTable() {
        try {
            List<Student> list = dao.getAll();
            populateTable(list);
            updateStats();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error fetching data: " + e.getMessage());
        }
    }

    private void populateTable(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) {
            Vector<Object> row = new Vector<>();
            row.add(s.getId());
            row.add(s.getName());
            row.add(s.getRollNo());
            row.add(s.getDepartment());
            row.add(s.getEmail());
            row.add(s.getPhone());
            row.add(s.getMarks());
            tableModel.addRow(row);
        }
    }

    private void updateStats() {
        try {
            Stats st = dao.getStats();
            lblTotal.setText("Total: " + st.getTotalStudents());
            lblMax.setText("Max: " + st.getMaxMarks());
            lblMin.setText("Min: " + st.getMinMarks());
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String,Integer> e : st.getDeptCounts().entrySet()) {
                sb.append(e.getKey()).append("(").append(e.getValue()).append(") ");
            }
            lblDept.setText("Dept: " + sb.toString());
        } catch (SQLException e) {
            lblTotal.setText("Total: -");
            lblMax.setText("Max: -");
            lblMin.setText("Min: -");
            lblDept.setText("Dept: -");
        }
    }

    private void search() {
        try {
            String name = tfSearchName.getText().trim();
            String dept = tfSearchDept.getText().trim();
            String roll = tfSearchRoll.getText().trim();
            Integer min = (tfMinMarks.getValue() == null) ? null : Integer.parseInt(tfMinMarks.getValue().toString().replaceAll(",", ""));
            Integer max = (tfMaxMarks.getValue() == null) ? null : Integer.parseInt(tfMaxMarks.getValue().toString().replaceAll(",", ""));
            List<Student> list = dao.search(name.isEmpty() ? null : name, dept.isEmpty() ? null : dept, roll.isEmpty() ? null : roll, min, max);
            populateTable(list);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Search error: " + e.getMessage());
        }
    }

    private Student findByIdInModel(int modelRow) {
        Student s = new Student();
        Object idObj = tableModel.getValueAt(modelRow, 0);
        long id = (idObj instanceof Number) ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
        s.setId(id);
        s.setName((String) tableModel.getValueAt(modelRow, 1));
        s.setRollNo((String) tableModel.getValueAt(modelRow, 2));
        s.setDepartment((String) tableModel.getValueAt(modelRow, 3));
        s.setEmail((String) tableModel.getValueAt(modelRow, 4));
        s.setPhone((String) tableModel.getValueAt(modelRow, 5));
        s.setMarks((Integer) tableModel.getValueAt(modelRow, 6));
        return s;
    }

    private void showStudentForm(Student s) {
        boolean edit = (s != null);
        JDialog dlg = new JDialog(frame, edit ? "Edit Student" : "Add Student", true);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Name:"), c);
        c.gridy++; p.add(new JLabel("Roll No:"), c);
        c.gridy++; p.add(new JLabel("Department:"), c);
        c.gridy++; p.add(new JLabel("Email:"), c);
        c.gridy++; p.add(new JLabel("Phone:"), c);
        c.gridy++; p.add(new JLabel("Marks:"), c);

        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1; c.gridy = 0;
        JTextField name = new JTextField(18);
        p.add(name, c);
        c.gridy++; JTextField roll = new JTextField(12); p.add(roll, c);
        c.gridy++; JTextField dept = new JTextField(12); p.add(dept, c);
        c.gridy++; JTextField email = new JTextField(16); p.add(email, c);
        c.gridy++; JTextField phone = new JTextField(12); p.add(phone, c);
        c.gridy++; JFormattedTextField marks = new JFormattedTextField(NumberFormat.getIntegerInstance()); marks.setColumns(6); p.add(marks, c);

        if (edit) {
            name.setText(s.getName());
            roll.setText(s.getRollNo());
            dept.setText(s.getDepartment());
            email.setText(s.getEmail());
            phone.setText(s.getPhone());
            marks.setValue(s.getMarks());
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton(edit ? "Save" : "Add");
        JButton cancel = new JButton("Cancel");
        actions.add(cancel); actions.add(ok);

        cancel.addActionListener(a -> dlg.dispose());
        ok.addActionListener(a -> {
            String nm = name.getText().trim();
            String r = roll.getText().trim();
            int mk = (marks.getValue() == null) ? 0 : Integer.parseInt(marks.getValue().toString().replaceAll(",", ""));
            if (nm.isEmpty() || r.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Name and Roll No are required");
                return;
            }
            try {
                if (edit) {
                    s.setName(nm); s.setRollNo(r); s.setDepartment(dept.getText().trim());
                    s.setEmail(email.getText().trim()); s.setPhone(phone.getText().trim()); s.setMarks(mk);
                    dao.update(s);
                } else {
                    Student ns = new Student();
                    ns.setName(nm); ns.setRollNo(r); ns.setDepartment(dept.getText().trim());
                    ns.setEmail(email.getText().trim()); ns.setPhone(phone.getText().trim()); ns.setMarks(mk);
                    dao.create(ns);
                }
                dlg.dispose();
                refreshTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(p, BorderLayout.CENTER);
        dlg.getContentPane().add(actions, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
            fw.write("id,name,roll_no,department,email,phone,marks\n");
            for (int i=0;i<tableModel.getRowCount();i++) {
                fw.append(tableModel.getValueAt(i,0).toString()).append(",");
                fw.append(escapeCsv(tableModel.getValueAt(i,1))).append(",");
                fw.append(escapeCsv(tableModel.getValueAt(i,2))).append(",");
                fw.append(escapeCsv(tableModel.getValueAt(i,3))).append(",");
                fw.append(escapeCsv(tableModel.getValueAt(i,4))).append(",");
                fw.append(escapeCsv(tableModel.getValueAt(i,5))).append(",");
                fw.append(tableModel.getValueAt(i,6).toString()).append("\n");
            }
            JOptionPane.showMessageDialog(frame, "Exported successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Export error: " + e.getMessage());
        }
    }

    private String escapeCsv(Object o) {
        if (o == null) return "";
        String s = o.toString().replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) return "\"" + s + "\"";
        return s;
    }

    private void handleEdit() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(frame, "Select a row first");
            return;
        }
        int modelRow = table.convertRowIndexToModel(r);
        Student s = findByIdInModel(modelRow);
        showStudentForm(s);
    }

    private void handleDelete() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(frame, "Select a row first");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure to delete?");
        if (confirm != JOptionPane.YES_OPTION) return;
        int modelRow = table.convertRowIndexToModel(r);
        Student s = findByIdInModel(modelRow);
        try {
            dao.delete(s.getId());
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Delete error: " + e.getMessage());
        }
    }

    public static class WrapLayout extends FlowLayout {
        public WrapLayout() { super(); }
        public WrapLayout(int align) { super(align); }
        public WrapLayout(int align, int hgap, int vgap) { super(align,hgap,vgap); }
    }
}
