package lib;

import java.io.Serializable;

/**
 * Created by andrey on 23.03.16.
 */
public class SearchTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    private String lastName;
    private String group;
    private String minMiddleMark;
    private String maxMiddleMark;
    private String minMark;
    private String maxMark;

    public SearchTemplate(String lastName, String group,
                          String minMiddleMark, String maxMiddleMark,
                          String minMark, String maxMark){
        this.lastName = lastName;
        this.group = group;
        this.minMiddleMark = minMiddleMark;
        this.maxMiddleMark = maxMiddleMark;
        this.maxMark = maxMark;
        this.minMark = minMark;
    }

    public boolean compliesTemplate(Student student) {
        if (!lastName.equals("") && !lastName.equals(student.getLastName())) return false;
        if (!group.equals("") && !group.equals(student.getNumberGroup())) return false;
        if (!maxMiddleMark.equals("-") && !isaCompliesMiddleMark(student)) return false;
        if (!minMark.equals("-") && !isaCompliesMark(student)) return false;
        return true;
    }

    private boolean isaCompliesMiddleMark(Student student) {
        return  getExaminationsMarkInt(minMiddleMark) <= student.getMiddleMark() &&
                getExaminationsMarkInt(maxMiddleMark) >= student.getMiddleMark();
    }

    private boolean isaCompliesMark(Student student) {
        boolean answer = true;
        if (student.getExaminations().size() == 0) return false;
        for (Examination exam : student.getExaminations()) {
            int examMark = exam.getExaminationMarkInt();
            if (!(getExaminationsMarkInt(minMark) <= examMark && getExaminationsMarkInt(maxMark) >= examMark))
                answer = false;
        }
        return answer;
    }

    private int getExaminationsMarkInt(String mark){
        if (mark.equals("-")) return 0;
        return Integer.parseInt(mark);
    }

}
