package client;

import lib.Constants;
import lib.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by andrey on 04/03/16.
 */
public class StudentTableWithPaging extends JComponent {

    private JScrollPane scrollTable;
    private Client client;
    private List<Student> students;
    private String namePanel;
    private int currentPage = 1;
    private int studentOnPage = 10;
    private int numberExaminations = 5;
    private int maxNumberExaminations = 5;
    private int studentSize = 0;

    public StudentTableWithPaging(){
        students = new ArrayList<Student>();
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    }

    public void makePanel() {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(makeTable(), BorderLayout.NORTH);
        scrollTable = new JScrollPane(tablePanel);
        scrollTable.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent evt) {
                updateScrollTable();
            }
        });
        add(scrollTable);
        add(makeToolsPanel());
    }

    private JPanel makeTable(){
        JPanel table = new JPanel();
        table.setLayout(new GridBagLayout());
        AddComponent.add(table, "Full Name", 0, 0, 1, 3);
        AddComponent.add(table, "Group", 1, 0, 1, 3);
        AddComponent.add(table, "Examinations", 2, 0, numberExaminations * 2, 1);
        for (int i = 0, x = 2; i < numberExaminations; i++, x += 2) {
            AddComponent.add(table, Integer.toString(i + 1), x, 1, 2, 1);
            AddComponent.add(table, "name", x, 2, 1, 1);
            AddComponent.add(table, "mark", x + 1, 2, 1, 1);
        }
        int lineInHeaderTable = 3;
        for (int y = lineInHeaderTable, student = 0; student < students.size(); y++, student++) {
            for (int i = 0; i < numberExaminations * 2 + 2; i++) {
                String write = getFieldForStudent(students.get(student), i);
                AddComponent.add(table, write, i, y, 1, 1);
            }
        }
        return table;
    }

    private JPanel makeToolsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        String statusBar = "Page " + currentPage + "/" + getNumberMaxPage()
                + " Total records: " + studentSize + " ";
        panel.add(new JLabel(statusBar));
        panel.add(AddComponent.makeButton(new JButton(), "FIRST_12.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                firstPage();
            }
        }));
        panel.add(AddComponent.makeButton(new JButton(), "PREVIOUS_12.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                prevPage();
            }
        }));
        panel.add(AddComponent.makeButton(new JButton(), "NEXT_12.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nextPage();
            }
        }));
        panel.add(AddComponent.makeButton(new JButton(), "LAST_12.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lastPage();
            }
        }));
        JLabel label = new JLabel(" Student on page: ");
        panel.add(label);
        String[] sizeStudent = {"10", "20", "30", "50", "100"};
        JComboBox sizeBox = new JComboBox(sizeStudent);
        sizeBox.setSelectedIndex(Arrays.asList(sizeStudent).indexOf(Integer.toString(studentOnPage)));
        sizeBox.setMaximumSize(sizeBox.getPreferredSize());
        sizeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeStudentOnPage(e);
            }
        });
        panel.add(sizeBox);
        label = new JLabel(" Number examinations: ");
        panel.add(label);
        String[] sizeExam = {"5", "6", "7", "8", "9", "10", "12", "15", "20"};
        JComboBox examBox = new JComboBox(sizeExam);
        examBox.setSelectedIndex(Arrays.asList(sizeExam).indexOf(Integer.toString(numberExaminations)));
        examBox.setMaximumSize(examBox.getPreferredSize());
        examBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeNumberExam(e);
            }
        });
        panel.add(examBox);
        panel.setMaximumSize(new Dimension(840,100));
        return panel;
    }

    public String getFieldForStudent(Student student, int i) {
        if (i == 0) return student.getLastName() + " " + student.getFirstName() + " " + student.getMiddleName();
        else if (i == 1) return student.getNumberGroup();
        else {
            int numberExamination = (i - 2) / 2;
            if (i % 2 == 0) {
                if (numberExamination < student.getExaminations().size()) {
                    return student.getExaminations().get(numberExamination).getExaminationName();
                } else return " - ";
            } else {
                if (numberExamination < student.getExaminations().size()) {
                    return student.getExaminations().get(numberExamination).getExaminationMark();
                } else return " - ";
            }
        }
    }

    private boolean hasNextPage() {
        return studentSize > studentOnPage * (currentPage - 1) + studentOnPage;
    }

    public void nextPage(){
        if (hasNextPage()) {
            client.sendToServer(Constants.NEXT_PAGE);
            client.sendToServer(getNamePanel());
            update();
        }
    }

    public void prevPage(){
        if (currentPage > 1) {
            client.sendToServer(Constants.PREV_PAGE);
            client.sendToServer(getNamePanel());
            update();
        }
    }

    public void firstPage(){
        if (currentPage > 1) {
            client.sendToServer(Constants.FIRST_PAGE);
            client.sendToServer(getNamePanel());
            update();
        }
    }

    public void lastPage(){
        if (currentPage != getNumberMaxPage()){
            client.sendToServer(Constants.LAST_PAGE);
            client.sendToServer(getNamePanel());
            update();
        }
    }

    private int getNumberMaxPage() {
        return (int)((studentSize - 1)/ studentOnPage) + 1;
    }

    public void changeStudentOnPage(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        String change = (String) cb.getSelectedItem();
        if (studentOnPage != Integer.parseInt(change)) {
            client.sendToServer(Constants.CHANGE_NUMBER_STUDENT_ON_PAGE);
            client.sendToServer(getNamePanel());
            client.sendToServer(change);
            update();
        }
    }

    public void changeNumberExam(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        String change = (String) cb.getSelectedItem();
        if (canChangeNumberExam(change)){
            client.sendToServer(Constants.CHANGE_NUMBER_EXAMINATIONS);
            client.sendToServer(getNamePanel());
            client.sendToServer(change);
            update();
        } else {
            JOptionPane.showMessageDialog
                    (null, "Can't do number exam, small than " + numberExaminations,
                            "ERROR", JOptionPane.ERROR_MESSAGE|JOptionPane.OK_OPTION);
        }
    }

    public String getNamePanel() {
        return namePanel;
    }

    public void setNamePanel(String namePanel) {
        this.namePanel = namePanel;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private boolean canChangeNumberExam(String change) {
        return maxNumberExaminations <= Integer.parseInt(change);
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Student> getStudents() {
        return students;
    }

    public int getStudentSize() {
        return studentSize;
    }

    public void setStudentSize(int studentSize) {
        this.studentSize = studentSize;
    }

    public int getStudentOnPage() {
        return studentOnPage;
    }

    public void setStudentOnPage(int studentOnPage) {
        this.studentOnPage = studentOnPage;
    }

    public int getNumberExaminations() {
        return numberExaminations;
    }

    public void setNumberExaminations(int numberExaminations) {
        this.numberExaminations = numberExaminations;
    }

    public int getMaxNumberExaminations() {
        return maxNumberExaminations;
    }

    public void setMaxNumberExaminations(int maxNumberExaminations) {
        this.maxNumberExaminations = maxNumberExaminations;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void updatePanel(){
        client.getUpdatePanel(namePanel);
    }

    public void update(){
        updatePanel();
        updateComponent();
    }

    public void updateComponent(){
        removeAll();
        makePanel();
        revalidate();
        repaint();
    }

    private void updateScrollTable() {
        scrollTable.revalidate();
        scrollTable.repaint();
    }

}

