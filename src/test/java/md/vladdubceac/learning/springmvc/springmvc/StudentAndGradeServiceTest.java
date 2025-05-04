package md.vladdubceac.learning.springmvc.springmvc;

import md.vladdubceac.learning.springmvc.models.*;
import md.vladdubceac.learning.springmvc.repository.HistoryGradeDao;
import md.vladdubceac.learning.springmvc.repository.MathGradeDao;
import md.vladdubceac.learning.springmvc.repository.ScienceGradeDao;
import md.vladdubceac.learning.springmvc.repository.StudentDao;
import md.vladdubceac.learning.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grades}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grades}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grades}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grades}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grades}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grades}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setupDatabase() {
        jdbcTemplate.execute(sqlAddStudent);
        jdbcTemplate.execute(sqlAddHistoryGrade);
        jdbcTemplate.execute(sqlAddMathGrade);
        jdbcTemplate.execute(sqlAddScienceGrade);
    }

    @Test
    public void createStudentService() {
        studentService.createStudent("Vlad", "Dubceac", "dubceacvlad@gmail.com");

        CollegeStudent student = studentDao.findByEmailAddress("dubceacvlad@gmail.com");

        assertEquals("dubceacvlad@gmail.com", student.getEmailAddress(), "Find by email");
    }

    @Test
    public void isStudentNullCheck() {
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);
        Optional<MathGrade> deletedMathGrade = mathGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);

        assertTrue(deletedCollegeStudent.isPresent(), "Return True");
        assertTrue(deletedMathGrade.isPresent());
        assertTrue(deletedScienceGrade.isPresent());
        assertTrue(deletedHistoryGrade.isPresent());

        studentService.deleteStudent(1);
        deletedMathGrade = mathGradeDao.findById(1);
        deletedScienceGrade = scienceGradeDao.findById(1);
        deletedHistoryGrade = historyGradeDao.findById(1);

        deletedCollegeStudent = studentDao.findById(1);

        assertFalse(deletedCollegeStudent.isPresent(), "Return False");
        assertFalse(deletedMathGrade.isPresent());
        assertFalse(deletedScienceGrade.isPresent());
        assertFalse(deletedHistoryGrade.isPresent());
    }

    @Sql("/insertData.sql")
    @Test
    public void getGradebookService() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();

        List<CollegeStudent> collegeStudentList = new ArrayList<>();

        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudentList.add(collegeStudent);
        }

        assertEquals(5, collegeStudentList.size());
    }

    @Test
    public void createGradeService() {

        // Create the grade
        assertTrue(studentService.createGrade(80.50, 1, "math"));
        assertTrue(studentService.createGrade(81.00, 1, "science"));
        assertTrue(studentService.createGrade(80.75, 1, "history"));

        // Get all grades with studentId
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        // Verify there is grades
        assertEquals(2, ((Collection<MathGrade>) mathGrades).size());
        assertEquals(2, ((Collection<ScienceGrade>) scienceGrades).size());
        assertEquals(2, ((Collection<HistoryGrade>) historyGrades).size());
    }

    @Test
    public void createGradeServiceReturnFalse() {
        assertFalse(studentService.createGrade(102, 1, "math"));
        assertFalse(studentService.createGrade(-10, 1, "math"));
        assertFalse(studentService.createGrade(80.51, 2, "math"));
        assertFalse(studentService.createGrade(80.51, 1, "literature"));
    }

    @Test
    public void deleteGradeService(){
        assertEquals(1, studentService.deleteGrade(1,"math"), "Returns student's ID after delete Math grade");
        assertEquals(1, studentService.deleteGrade(1,"science"), "Returns student's ID after delete Science grade");
        assertEquals(1, studentService.deleteGrade(1,"history"), "Returns student's ID after delete History grade");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero(){
        assertEquals(0, studentService.deleteGrade(0, "science"), "No student should have 0 ID");
        assertEquals(0, studentService.deleteGrade(0, "literature"), "No student has literature");
    }

    @Test
    public void studentInformation(){
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Test", gradebookCollegeStudent.getFirstName());
        assertEquals("User", gradebookCollegeStudent.getLastName());
        assertEquals("test.user@myemail.com", gradebookCollegeStudent.getEmailAddress());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size());
    }

    @Test
    public void studentInformationServiceReturnNull(){
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);
        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    public void cleanup() {
        jdbcTemplate.execute(sqlDeleteStudent);
        jdbcTemplate.execute(sqlDeleteMathGrade);
        jdbcTemplate.execute(sqlDeleteScienceGrade);
        jdbcTemplate.execute(sqlDeleteHistoryGrade);
    }
}
