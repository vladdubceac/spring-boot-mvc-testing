package md.vladdubceac.learning.models;

public class HistoryGrade {
    private int id;
    private int studentId;
    private double grade;

    public HistoryGrade() {
    }

    public HistoryGrade(double grade) {
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}
