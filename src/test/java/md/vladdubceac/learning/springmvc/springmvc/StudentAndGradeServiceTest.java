package md.vladdubceac.learning.springmvc.springmvc;

import md.vladdubceac.learning.springmvc.models.CollegeStudent;
import md.vladdubceac.learning.springmvc.models.HistoryGrade;
import md.vladdubceac.learning.springmvc.models.MathGrade;
import md.vladdubceac.learning.springmvc.models.ScienceGrade;
import md.vladdubceac.learning.springmvc.repository.HistoryGradeDao;
import md.vladdubceac.learning.springmvc.repository.MathGradeDao;
import md.vladdubceac.learning.springmvc.repository.ScienceGradeDao;
import md.vladdubceac.learning.springmvc.repository.StudentDao;
import md.vladdubceac.learning.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @BeforeEach
    public void setupDatabase() {
        jdbcTemplate.execute("INSERT INTO student(first_name, last_name, email_address) " +
                " VALUES('Test', 'User', 'test.user@myemail.com') ");

        jdbcTemplate.execute("INSERT INTO math_grade(student_id, grade) VALUES(1, 99.00)");
        jdbcTemplate.execute("INSERT INTO science_grade(student_id, grade) VALUES(1, 100.00)");
        jdbcTemplate.execute("INSERT INTO history_grade(student_id, grade) VALUES(1, 98.00)");
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

        assertTrue(deletedCollegeStudent.isPresent(), "Return True");

        studentService.deleteStudent(1);

        deletedCollegeStudent = studentDao.findById(1);

        assertFalse(deletedCollegeStudent.isPresent(), "Return False");
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

    @AfterEach
    public void cleanup() {
        jdbcTemplate.execute("DELETE FROM student");
        jdbcTemplate.execute("DELETE FROM math_grade");
        jdbcTemplate.execute("DELETE FROM science_grade");
        jdbcTemplate.execute("DELETE FROM history_grade");

        jdbcTemplate.execute("ALTER TABLE student ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE math_grade ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE science_grade ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE history_grade ALTER COLUMN id RESTART WITH 1");
    }
}
