package lib;

import java.io.Serializable;

/**
 * Created by andrey on 05/03/16.
 */
public class Examination implements Serializable {

    private static final long serialVersionUID = 1L;
    private String examinationName;
    private int examinationMark;

    public Examination(String examinationName, int examinationMark) {
        this.examinationName = examinationName;
        this.examinationMark = examinationMark;
    }

    public String getExaminationMark() {
        return Integer.toString(examinationMark);
    }

    public int getExaminationMarkInt() {
        return examinationMark;
    }

    public void setExaminationMark(int examinationMark) {
        this.examinationMark = examinationMark;
    }

    public String getExaminationName() {
        return examinationName;
    }

    public void setExaminationName(String examinationName) {
        this.examinationName = examinationName;
    }
}
