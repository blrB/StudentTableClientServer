package client;

import lib.Constants;
import lib.SearchTemplate;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

/**
 * Created by andrey on 06/03/16.
 */
public class SearchDialog {

    private StudentTableWithPaging studentTableWithPaging;
    private StudentTableWithPaging searchPanel;
    private Client client;
    private String mode;
    private JTextField lastName;
    private JTextField group;
    private JComboBox minMiddleMark;
    private JComboBox maxMiddleMark;
    private JComboBox minMark;
    private JComboBox maxMark;
    private JFrame frame;

    public SearchDialog(MainWindow mainWindow, String mode) {
        this.studentTableWithPaging = mainWindow.getStudentTableWithPaging();
        this.searchPanel = mainWindow.getSearchPanel();
        this.client = mainWindow.getClient();
        this.mode = mode;
        searchPanel.setNumberExaminations(studentTableWithPaging.getNumberExaminations());
        frame = createFrame();
        frame.pack();
        frame.setLocationRelativeTo(studentTableWithPaging);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private JFrame createFrame(){
        JFrame frame;
        JLabel labelText;
        if (mode.equals(Constants.SEARCH_MODE)) {
            frame = new JFrame("Search student");
            labelText = new JLabel("Search student");
        }
        else {
            frame = new JFrame("Remove student");
            labelText = new JLabel("Remove Student");
        }
        JPanel jPanelID = new JPanel();
        jPanelID.setLayout(new GridBagLayout());
        labelText.setHorizontalAlignment(JLabel.CENTER);
        AddComponent.add(jPanelID,labelText, 0, 0, 3, 1);
        String[] labelString = {"Last Name:", "Group:"};
        labelText = new JLabel(labelString[0]);
        AddComponent.add(jPanelID,labelText, 0, 1, 1, 1);
        lastName = new JTextField(30);
        AddComponent.add(jPanelID, lastName, 1, 1, 3, 1);
        labelText = new JLabel(labelString[1]);
        AddComponent.add(jPanelID, labelText, 0, 2, 1, 1);
        group = new JTextField(30);
        AddComponent.add(jPanelID, group, 1, 2, 3, 1);
        String[] markString = {"-", "4", "5", "6", "7", "8", "9", "10"};
        labelText = new JLabel("Middle mark(less/great)");
        labelText.setHorizontalAlignment(JLabel.CENTER);
        AddComponent.add(jPanelID,labelText, 0, 3, 1, 1);
        minMiddleMark = new JComboBox(markString);
        AddComponent.add(jPanelID, minMiddleMark, 1, 3, 1, 1);
        maxMiddleMark = new JComboBox(markString);
        AddComponent.add(jPanelID, maxMiddleMark, 2, 3, 1, 1);
        labelText = new JLabel("Mark(less/great)");
        labelText.setHorizontalAlignment(JLabel.CENTER);
        AddComponent.add(jPanelID,labelText, 0, 4, 1, 1);
        minMark = new JComboBox(markString);
        AddComponent.add(jPanelID, minMark, 1, 4, 1, 1);
        maxMark = new JComboBox(markString);
        AddComponent.add(jPanelID, maxMark, 2, 4, 1, 1);
        frame.add(jPanelID, BorderLayout.NORTH);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clickButton();
            }
        });
        frame.add(okButton, BorderLayout.SOUTH);
        return frame;
    }

    private void clickButton() {
        if (mode.equals("SEARCH_MODE")) searchStudent();
        else removeStudent();
    }

    private void searchStudent(){
        if (isAllCorrect()){
            searchPanel.setNamePanel(Constants.SEARCH_PANEL);
            searchPanel.setNumberExaminations(studentTableWithPaging.getNumberExaminations());
            SearchTemplate searchTemplate = new SearchTemplate(lastName.getText(), group.getText(),
                    (String)minMiddleMark.getSelectedItem(), (String)maxMiddleMark.getSelectedItem(),
                    (String)minMark.getSelectedItem(), (String)maxMark.getSelectedItem());
            client.sendToServer(Constants.SEARCH_STUDENT);
            client.sendToServer(searchTemplate);
            searchPanel.update();
            frame.add(searchPanel, BorderLayout.CENTER);
            frame.setSize(new Dimension(850,350));
            frame.revalidate();
            frame.repaint();
        } else {
            JOptionPane.showMessageDialog
                    (null, "Not correct student information", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeStudent(){
        if (isAllCorrect()){
            int counterStudent = studentTableWithPaging.getStudentSize();
            SearchTemplate removeTemplate = new SearchTemplate(lastName.getText(), group.getText(),
                    (String)minMiddleMark.getSelectedItem(), (String)maxMiddleMark.getSelectedItem(),
                    (String)minMark.getSelectedItem(), (String)maxMark.getSelectedItem());
            client.sendToServer(Constants.REMOVE_STUDENT);
            client.sendToServer(removeTemplate);
            studentTableWithPaging.update();
            counterStudent -= studentTableWithPaging.getStudentSize();
            if (counterStudent > 0) {
                JOptionPane.showMessageDialog
                        (null, "Delete " + counterStudent + " student", "INFO", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog
                        (null, "Student not find", "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog
                    (null, "Not correct student information", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isAllCorrect() {
        return (!(isNotCorrectLastName() || isNotCorrectGroup() ||
                isNotCorrectMark(minMiddleMark, maxMiddleMark) || isNotCorrectMark(minMark, maxMark)));
    }

    private boolean isNotCorrectGroup() {
        Pattern p = Pattern.compile("[0-9]+");
        return (!p.matcher(group.getText()).matches() ^ group.getText().equals(""));
    }

    private boolean isNotCorrectLastName() {
        return (lastName.getText().length() > 0 && lastName.getText().charAt(0) == ' ');
    }

    private boolean isNotCorrectMark(JComboBox min, JComboBox max) {
        return ((getExaminationsMarkNull(min) && !getExaminationsMarkNull(max)) ||
                (getExaminationsMarkNull(max) && !getExaminationsMarkNull(min)) ||
                (getExaminationsMarkInt(min) > getExaminationsMarkInt(max)));
    }

    private boolean getExaminationsMarkNull(JComboBox markBox){
        return markBox.getSelectedItem().equals("-");
    }

    private int getExaminationsMarkInt(JComboBox markBox){
        if (getExaminationsMarkNull(markBox)) return 0;
        return Integer.parseInt((String)markBox.getSelectedItem());
    }

}
