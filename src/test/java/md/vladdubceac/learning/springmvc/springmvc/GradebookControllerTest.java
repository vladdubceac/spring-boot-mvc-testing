package md.vladdubceac.learning.springmvc.springmvc;

import md.vladdubceac.learning.springmvc.models.CollegeStudent;
import md.vladdubceac.learning.springmvc.models.GradebookCollegeStudent;
import md.vladdubceac.learning.springmvc.repository.StudentDao;
import md.vladdubceac.learning.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentAndGradeServiceMock;

    @Autowired
    private StudentDao studentDao;

    @BeforeAll
    public static void setup(){
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Vlad");
        request.setParameter("lastname", "Dubceac");
        request.setParameter("emailAddress", "dubceacvlad@gmail.com");
    }

    @BeforeEach
    public void beforeEach() {
        jdbc.execute("INSERT INTO student(first_name, last_name, email_address) " +
                " VALUES('Test', 'User', 'test.user@myemail.com') ");
    }

    @Test
    public void getStudentsHttpRequest() throws Exception {
        CollegeStudent studentOne = new GradebookCollegeStudent("first", "student", "first.student@mail.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent("second", "student", "second.student@mail.com");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(List.of(studentOne, studentTwo));

        when(studentAndGradeServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentAndGradeServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");
    }

    @Test
    public void createStudentHttpRequest() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(post("/").content(MediaType.APPLICATION_JSON_VALUE)
                .param("firstName", request.getParameterValues("firstname"))
                .param("lastName",request.getParameterValues("lastname"))
                .param("emailAddress",request.getParameterValues("emailAddress"))
                ).andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        CollegeStudent verifyStudent = studentDao.findByEmailAddress("dubceacvlad@gmail.com");

        assertNotNull(verifyStudent,"Student should be found");
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute("DELETE FROM student");
        jdbc.execute("ALTER TABLE student ALTER COLUMN id RESTART WITH 1");
    }
}
