package client;

import lib.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

/**
 * Created by andrey on 18/02/16.
 */
public class MainWindow {

    private StudentTableWithPaging studentTableWithPaging;
    private StudentTableWithPaging searchPanel;
    private JFrame frame;
    private JTextField port;
    private JTextField host;
    private Client client;
    private boolean connect = false;

    public MainWindow() {
        frame = new JFrame("Student Table");
        frame.setSize(850,350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createFileMenu());
        frame.add(createToolBar(), BorderLayout.PAGE_START);
        studentTableWithPaging = new StudentTableWithPaging();
        studentTableWithPaging.setNamePanel(Constants.MAIN_PANEL);
        searchPanel = new StudentTableWithPaging();
        searchPanel.setNamePanel(Constants.SEARCH_PANEL);
        frame.add(studentTableWithPaging, BorderLayout.CENTER);
        frame.setMinimumSize(new Dimension(850,350));
        frame.setVisible(true);
    }

    private JMenuBar createFileMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openFile = new JMenuItem("Open");
        openFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        fileMenu.add(openFile);
        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exit);
        menuBar.add(fileMenu);
        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        JLabel label = new JLabel("Host: ");
        toolBar.add(label);
        host = new JTextField("127.0.0.1", 16);
        host.setMaximumSize(new Dimension(160,20));
        toolBar.add(host);
        toolBar.addSeparator();
        label = new JLabel("Port: ");
        toolBar.add(label);
        port = new JTextField("5555", 4);
        port.setMaximumSize(new Dimension(50,20));
        toolBar.add(port);
        toolBar.addSeparator();
        toolBar.add(AddComponent.makeButton(new JButton(), "CONNECT.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        }));
        toolBar.addSeparator();
        toolBar.add(AddComponent.makeButton(new JButton(), "SAVE.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        }));
        toolBar.add(AddComponent.makeButton(new JButton(), "OPEN.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        }));
        toolBar.addSeparator();
        toolBar.add(AddComponent.makeButton(new JButton(), "SEARCH.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchDialog();
            }
        }));
        toolBar.add(AddComponent.makeButton(new JButton(), "ADD.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDialog();
            }
        }));
        toolBar.add(AddComponent.makeButton(new JButton(), "REMOVE.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeDialog();
            }
        }));
        return toolBar;
    }

    private void addDialog() {
        if (connect) {
            new AddDialog(studentTableWithPaging);
        }
    }

    private void searchDialog() {
        if (connect) {
            new SearchDialog(this, Constants.SEARCH_MODE);
        }
    }

    private void removeDialog() {
        if (connect) {
            new SearchDialog(this, Constants.REMOVE_MODE);
        }
    }

    private void openFile() {
        if (connect) {
            String name = (String) JOptionPane.showInputDialog(null, "Open file on server",
                    "Open file", JOptionPane.QUESTION_MESSAGE, null, null, "student.stable");
            if ((name != null) && (name.length() > 0)) {
                client.sendToServer(Constants.OPEN_FILE);
                client.sendToServer(name);
                studentTableWithPaging.update();
            }
        } else {
            JOptionPane.showMessageDialog
                    (null, "You not connected to server!", "WARNING", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveFile() {
        if (connect) {
            String name = (String) JOptionPane.showInputDialog(null, "Save file on server",
                    "Save file", JOptionPane.QUESTION_MESSAGE, null, null, "new_file");
            if ((name != null) && (name.length() > 0)) {
                client.sendToServer(Constants.SAVE_FILE);
                client.sendToServer(name);
            }
        } else {
            JOptionPane.showMessageDialog
                    (null, "You not connected to server!", "WARNING", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void connect() {
        if (isCorrectHostAndPort()){
            client = new Client(this, host.getText(), Integer.parseInt(port.getText()));
            studentTableWithPaging.setClient(client);
            searchPanel.setClient(client);
        } else {
            client.sendToServer(Constants.CLIENT_EXIT);
            connect = false;
            JOptionPane.showMessageDialog
                    (null, "Not correct host or port", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isCorrectHostAndPort(){
        Pattern pHost = Pattern.compile("((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])");
        Pattern pPort = Pattern.compile("[0-9]{1,5}");
        if (pPort.matcher(port.getText()).matches()){
            int portInt = Integer.parseInt(port.getText());
            return (pHost.matcher(host.getText()).matches() && 0 <= portInt && portInt <= 65535);
        } else {
            return false;
        }
    }

    public StudentTableWithPaging getStudentTableWithPaging(){
        return studentTableWithPaging;
    }

    public StudentTableWithPaging getSearchPanel(){
        return searchPanel;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public JTextField getPort() {
        return port;
    }

    public void setPort(JTextField port) {
        this.port = port;
    }

    public JTextField getHost() {
        return host;
    }

    public void setHost(JTextField host) {
        this.host = host;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public JFrame getFrame(){
        return frame;
    }

    public static void main(String[] args) {
        final MainWindow mainWindow = new MainWindow();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                if (mainWindow.getClient() != null) {
                    mainWindow.getClient().sendToServer(Constants.CLIENT_EXIT);
                }
            }
        }));
    }

}
