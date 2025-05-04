package md.vladdubceac.learning.springmvc.service;

import jakarta.transaction.Transactional;
import md.vladdubceac.learning.springmvc.models.*;
import md.vladdubceac.learning.springmvc.repository.HistoryGradeDao;
import md.vladdubceac.learning.springmvc.repository.MathGradeDao;
import md.vladdubceac.learning.springmvc.repository.ScienceGradeDao;
import md.vladdubceac.learning.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Autowired
    private StudentGrades studentGrades;

    public void createStudent(String firstName, String lastName, String emailAddress){
        CollegeStudent student = new CollegeStudent(firstName,lastName,emailAddress);
        studentDao.save(student);}

    public boolean checkIfStudentIsNull(int id){
        Optional<CollegeStudent>  student = studentDao.findById(id);
        return student.isPresent();
    }

    public void deleteStudent(int id){
        if(checkIfStudentIsNull(id)) {
            studentDao.deleteById(id);
            historyGradeDao.deleteByStudentId(id);
            scienceGradeDao.deleteByStudentId(id);
            mathGradeDao.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook(){
        return studentDao.findAll();
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {
        if(!checkIfStudentIsNull(studentId)) {
            return false;
        }

        if(grade >= 0 && grade <= 100){
            switch (gradeType.toLowerCase()){
                case "math":
                    saveMathGrade(grade, studentId);
                    return true;
                case "science":
                    saveScienceGrade(grade, studentId);
                    return true;
                case "history":
                    saveHistoryGrade(grade, studentId);
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void saveHistoryGrade(double grade, int studentId) {
        historyGrade.setId(0);
        historyGrade.setGrade(grade);
        historyGrade.setStudentId(studentId);
        historyGradeDao.save(historyGrade);
    }

    private void saveScienceGrade(double grade, int studentId) {
        scienceGrade.setId(0);
        scienceGrade.setGrade(grade);
        scienceGrade.setStudentId(studentId);
        scienceGradeDao.save(scienceGrade);
    }

    private void saveMathGrade(double grade, int studentId) {
        mathGrade.setId(0);
        mathGrade.setGrade(grade);
        mathGrade.setStudentId(studentId);
        mathGradeDao.save(mathGrade);
    }

    public int deleteGrade(int id, String gradeType) {
        int studentId = 0;
        Optional<? extends Grade> grade = Optional.empty();
        switch (gradeType){
            case "math":
                grade = mathGradeDao.findById(id);
                if(grade.isPresent()) {
                    studentId = grade.get().getStudentId();
                    mathGradeDao.deleteById(id);
                }
                break;
            case "science":
                grade = scienceGradeDao.findById(id);
                if(grade.isPresent()){
                    studentId = grade.get().getStudentId();
                    scienceGradeDao.deleteById(id);
                }
                break;
            case "history":
                grade = historyGradeDao.findById(id);
                if(grade.isPresent()){
                    studentId = grade.get().getStudentId();
                    historyGradeDao.deleteById(id);
                }
                break;
            default:
                return studentId;
        }
        return studentId;
    }

    public GradebookCollegeStudent studentInformation(int id) {
        Optional<CollegeStudent> student = studentDao.findById(id);
        if(student.isPresent()) {
            CollegeStudent collegeStudent = student.get();

            Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(id);
            List<Grade> mathGradeList = new ArrayList<>();
            mathGrades.forEach(mathGradeList::add);

            Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(id);
            List<Grade> scienceGradeList = new ArrayList<>();
            scienceGrades.forEach(scienceGradeList::add);

            Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(id);
            List<Grade> historyGradeList = new ArrayList<>();
            historyGrades.forEach(historyGradeList::add);

            studentGrades.setHistoryGradeResults(historyGradeList);
            studentGrades.setMathGradeResults(mathGradeList);
            studentGrades.setScienceGradeResults(scienceGradeList);
            return new GradebookCollegeStudent(id, collegeStudent.getFirstName(), collegeStudent.getLastName(), collegeStudent.getEmailAddress(),studentGrades);
        }
        return null;
    }

    public void configureStudentInformationModel(int studentId, Model model){
        GradebookCollegeStudent studentEntity = studentInformation(studentId);
        if (studentEntity.getStudentGrades().getMathGradeResults().size() > 0) {
            model.addAttribute("mathAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getMathGradeResults()
            ));
        } else {
            model.addAttribute("mathAverage", "N/A");
        }
        if(studentEntity.getStudentGrades().getHistoryGradeResults().size()>0){
            model.addAttribute("historyAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getHistoryGradeResults()
            ));
        } else {
            model.addAttribute("historyAverage", "N/A");
        }
        if(studentEntity.getStudentGrades().getScienceGradeResults().size()>0){
            model.addAttribute("scienceAverage",studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getScienceGradeResults()
            ));
        }else{
            model.addAttribute("scienceAverage","N/A");
        }
    }
}
