package md.vladdubceac.learning.springmvc.controller;

import md.vladdubceac.learning.springmvc.models.CollegeStudent;
import md.vladdubceac.learning.springmvc.models.Gradebook;
import md.vladdubceac.learning.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @PostMapping(value = "/")
    public String createStudent(@ModelAttribute("student") CollegeStudent student, Model model) {
        studentAndGradeService.createStudent(student.getFirstName(), student.getLastName(), student.getEmailAddress());
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
        model.addAttribute("students", collegeStudents);
        return "index";
    }

    @GetMapping(value = "/delete/student/{id}")
    public String deleteStudent(@PathVariable int id, Model model){
        if(!studentAndGradeService.checkIfStudentIsNull(id)) {
            return "error";
        }

        studentAndGradeService.deleteStudent(id);
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
        model.addAttribute("students", collegeStudents);
        return "index";
    }

    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        return "studentInformation";
    }

}
