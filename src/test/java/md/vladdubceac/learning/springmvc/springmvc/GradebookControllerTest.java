package md.vladdubceac.learning.springmvc.springmvc;

import md.vladdubceac.learning.springmvc.models.CollegeStudent;
import md.vladdubceac.learning.springmvc.models.GradebookCollegeStudent;
import md.vladdubceac.learning.springmvc.models.MathGrade;
import md.vladdubceac.learning.springmvc.repository.MathGradeDao;
import md.vladdubceac.learning.springmvc.repository.StudentDao;
import md.vladdubceac.learning.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @Autowired
    private StudentDao studentDao;

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

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private MathGradeDao mathGradeDao;

    @BeforeAll
    public static void setup(){
        request = new MockHttpServletRequest();
        request.setParameter("firstName", "Vlad");
        request.setParameter("lastName", "Dubceac");
        request.setParameter("emailAddress", "dubceacvlad@gmail.com");
    }

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.execute(sqlAddStudent);
        jdbcTemplate.execute(sqlAddHistoryGrade);
        jdbcTemplate.execute(sqlAddMathGrade);
        jdbcTemplate.execute(sqlAddScienceGrade);
    }

    @Test
    public void getStudentsHttpRequest() throws Exception {
        CollegeStudent studentOne = new GradebookCollegeStudent("first", "student", "first.student@mail.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent("second", "student", "second.student@mail.com");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(List.of(studentOne, studentTwo));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");
    }

    @Test
    public void createStudentHttpRequest() throws Exception{
        CollegeStudent studentOne = new CollegeStudent("test","student","test_student@test.com" );

        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = this.mockMvc.perform(post("/").content(MediaType.APPLICATION_JSON_VALUE)
                .param("firstName", request.getParameterValues("firstName"))
                .param("lastName",request.getParameterValues("lastName"))
                .param("emailAddress",request.getParameterValues("emailAddress"))
                ).andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        CollegeStudent verifyStudent = studentDao.findByEmailAddress("dubceacvlad@gmail.com");

        assertNotNull(verifyStudent,"Student should be found");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView,"index");

        assertFalse(studentDao.findById(1).isPresent());
    }

    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/delete/student/{id}",0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView,"error");
    }

    @Test
    public void studentInformationHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}",1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView,"studentInformation");
    }

    @Test
    public void studentInformationHttpStudentDoesNotExistRequest() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}",0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void createValidGradeHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());

        GradebookCollegeStudent student = studentService.studentInformation(1);

        assertEquals(1, student.getStudentGrades().getMathGradeResults().size());

        MvcResult mvcResult = mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade","85.00")
                .param("gradeType","math")
                .param("studentId","1")).andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView,"studentInformation");

        student = studentService.studentInformation(1);

        assertEquals(2, student.getStudentGrades().getMathGradeResults().size());
    }

    @Test
    public void createNonValidGradeHttpRequestStudentDoesNotExistEmptyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade","85.00")
                .param("gradeType","history")
                .param("studentId","0"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void createNonValidGradeHttpRequestGradeTypeDoesNotExistEmptyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade","85.00")
                .param("gradeType","literature")
                .param("studentId","1"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void deleteAValidGradeHttpRequest() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);

        assertTrue(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}",1,"math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");

        mathGrade = mathGradeDao.findById(1);

        assertTrue(mathGrade.isEmpty());
    }

    @Test
    public void deleteAValidGradeHttpRequestStudentIdDoesNotExistEmptyResponse() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDao.findById(2);

        assertFalse(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}",2,"math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView,"error");
    }

    @Test
    public void deleteANonValidGradeHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}",1,"literature"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView,"error");
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbcTemplate.execute(sqlDeleteStudent);
        jdbcTemplate.execute(sqlDeleteMathGrade);
        jdbcTemplate.execute(sqlDeleteScienceGrade);
        jdbcTemplate.execute(sqlDeleteHistoryGrade);
    }
}
