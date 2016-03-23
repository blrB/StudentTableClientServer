package client;

import lib.Constants;
import lib.Student;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Client {

    private MainWindow mainWindow;
    private StudentTableWithPaging studentTableWithPaging;
    private StudentTableWithPaging searchPanel;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String host;
    private Socket socket;
    private int port;

    public Client(MainWindow mainWindow, String host, int port) {
        this.mainWindow = mainWindow;
        studentTableWithPaging = mainWindow.getStudentTableWithPaging();
        searchPanel = mainWindow.getSearchPanel();
        this.host = host;
        this.port = port;
        this.socket = null;
        createSocket();
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            JOptionPane.showMessageDialog
                    (null, "Not can install connection", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendToServer(Object object){
        try {
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog
                    (null, "Not send date", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void getUpdatePanel(String where) {
        if (where.equals(Constants.MAIN_PANEL)) {
            getUpdatePanel(studentTableWithPaging);
        } else {
            getUpdatePanel(searchPanel);
        }
    }

    public void getUpdatePanel(StudentTableWithPaging studentTableWithPaging) {
        try {
            studentTableWithPaging.setStudents((List<Student>) inputStream.readObject());
            studentTableWithPaging.setNumberExaminations((int) inputStream.readObject());
            studentTableWithPaging.setMaxNumberExaminations((int) inputStream.readObject());
            studentTableWithPaging.setStudentSize((int) inputStream.readObject());
            studentTableWithPaging.setCurrentPage((int) inputStream.readObject());
            studentTableWithPaging.setStudentOnPage((int) inputStream.readObject());
        } catch (Exception e) {
            JOptionPane.showMessageDialog
                    (null, "Not read date", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createSocket() {
        try {
            socket = new Socket(host, port);
            mainWindow.setConnect(true);
            JOptionPane.showMessageDialog
                    (null, "You connect to " + host + ":" + port, "INFO", JOptionPane.INFORMATION_MESSAGE);
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog
                    (null, "Unknown host " + host, "ERROR", JOptionPane.ERROR_MESSAGE);
            mainWindow.setConnect(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog
                    (null, "I/O Error creating socket " + host + ":" + port, "ERROR", JOptionPane.ERROR_MESSAGE);
            mainWindow.setConnect(false);
        }
    }

}