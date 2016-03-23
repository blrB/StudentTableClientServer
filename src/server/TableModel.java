package server;

import lib.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrey on 05/03/16.
 */
public class TableModel {

    private int numberExaminations = 5;
    private int numberMaxExaminations;
    private int currentPage = 1;
    private int studentOnPage = 10;
    private List<Student> students;

    public TableModel() {
        students = new ArrayList<Student>();
    }

    public int getNumberExaminations() {
        return numberExaminations;
    }

    public void setNumberExaminations(int numberExaminations) {
        this.numberExaminations = numberExaminations;
    }

    public int getNumberMaxExaminations() {
        return numberMaxExaminations;
    }

    public void setNumberMaxExaminations() {
        numberMaxExaminations = 5;
    }

    public void setNumberMaxExaminations(int maxExaminations) {
        numberMaxExaminations = (maxExaminations > numberMaxExaminations) ? maxExaminations : numberMaxExaminations;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student){
        students.add(student);
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public int getStudentSize() {
        return students.size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getStudentOnPage() {
        return studentOnPage;
    }

    public void setStudentOnPage(int studentOnPage) {
        this.studentOnPage = studentOnPage;
    }

}
