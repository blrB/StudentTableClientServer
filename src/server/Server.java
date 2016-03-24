package server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;
import javax.swing.*;

public class Server {

    private static final Logger log = Logger.getLogger(Server.class);
    private static final int DEFAULT_PORT = 5555;
    private Socket socket;
    private JTextArea textArea;

    public Server(){
        JFrame frame = new JFrame("Server Student Table");
        frame.setSize(850,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.setVisible(true);
        frame.add(scrollPane);
        runServer();
    }

    private void runServer() {
        textArea.append("Run server\n");
        try {
            ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
            log.info("START SERVER");
            while (true){
                socket = serverSocket.accept();
                new ServerSession(serverSocket, socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public static void main(String[] args){
        new Server();
    }

}