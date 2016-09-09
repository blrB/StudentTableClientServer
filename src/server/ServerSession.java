package server;

import lib.Constants;
import lib.SearchTemplate;
import lib.Student;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by andrey on 21/03/16.
 */
public class ServerSession  implements Runnable {

    private ServerSocket serverSocket;
    private Socket socket;
    private Server server;
    private JTextArea jTextArea;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private TableModel tableModel;
    private TableModel searchTableModel;

    public ServerSession(ServerSocket serverSocket, Socket socket, Server server) {
        jTextArea = server.getTextArea();
        jTextArea.append("New session\n");
        tableModel = new TableModel();
        this.serverSocket = serverSocket;
        this.server = server;
        this.socket = socket;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
            //runSession();
        } catch (Exception e) {
            jTextArea.append("ERROR.\n");
            e.printStackTrace();
        }
    }

    public void runSession() throws IOException, ClassNotFoundException {
        jTextArea.append("Run session\n");
        String command;
        jTextArea.append("Client connected\n");
        while (true) {
            command = (String) inputStream.readObject();
            if (command.equals(Constants.CLIENT_EXIT)) break;
            jTextArea.append("New command from client " + command + "\n");
            switch (command) {
                case Constants.OPEN_FILE:
                    openFile();
                    break;
                case Constants.SAVE_FILE:
                    saveFile();
                    break;
                case Constants.FIRST_PAGE:
                    firstPage();
                    break;
                case Constants.PREV_PAGE:
                    prevPage();
                    break;
                case Constants.NEXT_PAGE:
                    nextPage();
                    break;
                case Constants.LAST_PAGE:
                    lastPage();
                    break;
                case Constants.CHANGE_NUMBER_STUDENT_ON_PAGE:
                    changeStudentOnPage();
                    break;
                case Constants.CHANGE_NUMBER_EXAMINATIONS:
                    changeNumberExam();
                    break;
                case Constants.ADD_STUDENT:
                    addStudent();
                    break;
                case Constants.SEARCH_STUDENT:
                    searchStudent();
                    break;
                case Constants.REMOVE_STUDENT:
                    removeStudent();
                    break;
                default:
                    jTextArea.append("Wrong command " + command);
                    break;
            }
        }
        server.getTextArea().append("Client exit\n");
    }

    private void removeStudent() throws IOException, ClassNotFoundException {
        SearchTemplate removeTemplate = (SearchTemplate) inputStream.readObject();
        jTextArea.append("Remove student... \n");
        Iterator<Student> itr = tableModel.getStudents().iterator();
        int counterStudent = 0;
        while (itr.hasNext()) {
            Student student = itr.next();
            if (removeTemplate.compliesTemplate(student)) {
                itr.remove();
                counterStudent++;
            }
        }
        jTextArea.append("Delete " + counterStudent + "\n");
        sendStudentArray(tableModel);
    }

    private void searchStudent() throws IOException, ClassNotFoundException {
        SearchTemplate searchTemplate = (SearchTemplate) inputStream.readObject();
        jTextArea.append("Search student... \n");
        searchTableModel = new TableModel();
        for (Student student : tableModel.getStudents()) {
            if (searchTemplate.compliesTemplate(student)) {
                searchTableModel.addStudent(student);
            }
        }
        sendStudentArray(searchTableModel);
    }

    private void addStudent() throws IOException, ClassNotFoundException {
        Student student = (Student) inputStream.readObject();
        jTextArea.append("Add new student " + student.getLastName() + " "
                + student.getFirstName() + " "
                + student.getMiddleName() + "\n");
        tableModel.addStudent(student);
        sendStudentArray(tableModel);
    }

    private void saveFile() throws IOException, ClassNotFoundException {
        String fileName = (String) inputStream.readObject();
        jTextArea.append("Try save file " + fileName + "\n");
        FileHandler fileHandler = new FileHandler(this);
        fileHandler.saveFile(fileName);
    }

    private void openFile() throws IOException, ClassNotFoundException {
        tableModel = new TableModel();
        String fileName = (String) inputStream.readObject();
        jTextArea.append("Try open file " + fileName + "\n");
        FileHandler fileHandler = new FileHandler(this);
        fileHandler.openXMLFile(fileName);
        sendStudentArray(tableModel);
    }

    private void sendStudentArray(TableModel tableModel) throws IOException {
        List<Student> sendStudents = new ArrayList<Student>();
        List<Student> students = tableModel.getStudents();
        int currentPage = tableModel.getCurrentPage();
        int studentOnPage = tableModel.getStudentOnPage();
        int numberExaminations = tableModel.getNumberMaxExaminations();
        int maxNumberExaminations = tableModel.getNumberExaminations();
        int studentSize = tableModel.getStudentSize();
        int firstStudentOnPage = studentOnPage * (currentPage - 1);
        for (int numberStudent = firstStudentOnPage;
             numberStudent < firstStudentOnPage + studentOnPage && numberStudent < students.size();
             numberStudent++) {
            sendStudents.add(students.get(numberStudent));
        }
        outputStream.writeObject(sendStudents);
        outputStream.writeObject(maxNumberExaminations);
        outputStream.writeObject(numberExaminations);
        outputStream.writeObject(studentSize);
        outputStream.writeObject(currentPage);
        outputStream.writeObject(studentOnPage);
        outputStream.flush();
    }

    public void nextPage() throws IOException, ClassNotFoundException {
        String where = (String) inputStream.readObject();
        jTextArea.append("Command get from " + where + "\n");
        TableModel table = (where.equals(Constants.MAIN_PANEL)) ? tableModel : searchTableModel;
        table.setCurrentPage(table.getCurrentPage() + 1);
        sendStudentArray(table);
    }

    public void prevPage() throws IOException, ClassNotFoundException {
        String where = (String) inputStream.readObject();
        jTextArea.append("Command get from " + where + "\n");
        TableModel table = (where.equals(Constants.MAIN_PANEL)) ? tableModel : searchTableModel;
        table.setCurrentPage(table.getCurrentPage() - 1);
        sendStudentArray(table);
    }

    public void firstPage() throws IOException, ClassNotFoundException {
        String where = (String) inputStream.readObject();
        jTextArea.append("Command get from " + where + "\n");
        TableModel table = (where.equals(Constants.MAIN_PANEL)) ? tableModel : searchTableModel;
        table.setCurrentPage(1);
        sendStudentArray(table);
    }

    public void lastPage() throws IOException, ClassNotFoundException {
        String where = (String) inputStream.readObject();
        jTextArea.append("Command get from " + where + "\n");
        TableModel table = (where.equals(Constants.MAIN_PANEL)) ? tableModel : searchTableModel;
        table.setCurrentPage((int) ((table.getStudentSize() - 1) / table.getStudentOnPage()) + 1);
        sendStudentArray(table);
    }

    public void changeStudentOnPage() throws IOException, ClassNotFoundException {
        String where = (String) inputStream.readObject();
        jTextArea.append("Command get from " + where + "\n");
        TableModel table = (where.equals(Constants.MAIN_PANEL)) ? tableModel : searchTableModel;
        String change = (String) inputStream.readObject();
        jTextArea.append("Change student on page number on" + change + "\n");
        table.setStudentOnPage(Integer.parseInt(change));
        sendStudentArray(table);
    }

    public void changeNumberExam() throws IOException, ClassNotFoundException {
        String where = (String) inputStream.readObject();
        jTextArea.append("Command get from " + where + "\n");
        TableModel table = (where.equals(Constants.MAIN_PANEL)) ? tableModel : searchTableModel;
        String number = (String) inputStream.readObject();
        jTextArea.append("Change examination number number on" + number + "\n");
        table.setNumberExaminations(Integer.parseInt(number));
        sendStudentArray(table);
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public JTextArea getTextArea() {
        return jTextArea;
    }

    @Override
    public void run() {
        try {
            runSession();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
