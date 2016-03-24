package server;

import lib.Examination;
import lib.Student;

import javax.swing.*;
import javax.xml.stream.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrey on 26/02/16.
 */
public class FileHandler {

    private static final String LAST_NAME = "lastName";
    private static final String FIRST_NAME = "firstName";
    private static final String MIDDLE_NAME = "middleName";
    private static final String GROUP = "group";
    private static final String EXAM = "exam";
    private static final String NAME = "name";
    private static final String NUMBER_EXAM = "numberExam";
    private static final String STUDENT = "student";
    private static final String STUDENTS = "students";
    private static final String FORMAT = "UTF-8";
    private static final String VER = "1.0";
    private static final String EXTENSION = "stable";
    private TableModel tableModel;
    private JTextArea textArea;

    public FileHandler(ServerSession serverSession){
        tableModel = serverSession.getTableModel();
        textArea = serverSession.getTextArea();
    }

    public void saveFile(String saveName){
        try {
            XMLOutputFactory output = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = output.createXMLStreamWriter
                    (new FileWriter(saveName + "." + EXTENSION));
            writer.writeStartDocument(FORMAT, VER);
            writer.writeStartElement(STUDENTS);
            writer.writeStartElement(NUMBER_EXAM);
            writer.writeCharacters(Integer.toString(tableModel.getNumberExaminations()));
            writer.writeEndElement();
            for (Student student : tableModel.getStudents()) {
                writer.writeStartElement(STUDENT);
                writer.writeAttribute(LAST_NAME, student.getLastName());
                writer.writeAttribute(FIRST_NAME, student.getFirstName());
                writer.writeAttribute(MIDDLE_NAME, student.getMiddleName());
                writer.writeAttribute(GROUP, student.getNumberGroup());
                for (Examination exam : student.getExaminations()) {
                    writer.writeStartElement(EXAM);
                    writer.writeAttribute(NAME, exam.getExaminationName());
                    writer.writeCharacters(exam.getExaminationMark());
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
        } catch (Exception eSave) {
            textArea.append("ERROR. File not save!\n");
        }
    }

    public void openXMLFile(String fileName) {
        try {
            String name;
            String mark;
            String lastName = "";
            String firstName = "";
            String middleName = "";
            String group = "";
            String numberExam = "";
            tableModel.getStudents().clear();
            List<Examination> exams = new ArrayList<Examination>();
            XMLStreamReader xmlr = XMLInputFactory.newInstance()
                    .createXMLStreamReader(fileName, new FileInputStream(fileName));
            while (xmlr.hasNext()) {
                xmlr.next();
                if (xmlr.isStartElement()) {
                    if (xmlr.getLocalName().equals(STUDENT)){
                        exams = new ArrayList<Examination>();
                        lastName = xmlr.getAttributeValue(null, LAST_NAME);
                        firstName = xmlr.getAttributeValue(null, FIRST_NAME);
                        middleName = xmlr.getAttributeValue(null, MIDDLE_NAME);
                        group = xmlr.getAttributeValue(null, GROUP);
                    }
                    else if (xmlr.getLocalName().equals(EXAM)){
                        name = xmlr.getAttributeValue(null, NAME);
                        xmlr.next();
                        mark = xmlr.getText();
                        exams.add(new Examination(name, Integer.parseInt(mark)));
                    } else if (xmlr.getLocalName().equals(NUMBER_EXAM)){
                        xmlr.next();
                        numberExam = xmlr.getText();
                    }
                } else if (xmlr.isEndElement()) {
                    if (xmlr.getLocalName().equals(STUDENT)){
                        tableModel.getStudents().add(new Student(lastName, firstName, middleName, group, exams));
                    }
                }
            }
            tableModel.setNumberExaminations(Integer.parseInt(numberExam));
        } catch (Exception e) {
            textArea.append("ERROR. File not find or not correct\n");;
        }
    }

}
