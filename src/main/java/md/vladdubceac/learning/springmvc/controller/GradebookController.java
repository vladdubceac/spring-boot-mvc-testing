package md.vladdubceac.learning.springmvc.controller;

import md.vladdubceac.learning.springmvc.models.CollegeStudent;
import md.vladdubceac.learning.springmvc.models.Grade;
import md.vladdubceac.learning.springmvc.models.Gradebook;
import md.vladdubceac.learning.springmvc.models.GradebookCollegeStudent;
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
    public String deleteStudent(@PathVariable int id, Model model) {
        if (!studentAndGradeService.checkIfStudentIsNull(id)) {
            return "error";
        }

        studentAndGradeService.deleteStudent(id);
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
        model.addAttribute("students", collegeStudents);
        return "index";
    }

    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        if (!studentAndGradeService.checkIfStudentIsNull(id)) {
            return "error";
        }

        studentAndGradeService.configureStudentInformationModel(id, m);

        return "studentInformation";
    }

    @PostMapping(value = "/grades")
    public String createGrade(@RequestParam("grade")double grade, @RequestParam("gradeType")String gradeType,
                              @RequestParam("studentId")int studentId, Model model){
        if(!studentAndGradeService.checkIfStudentIsNull(studentId)){
            return "error";
        }
        boolean success = studentAndGradeService.createGrade(grade, studentId, gradeType);

        if(!success){
            return "error";
        }

        studentAndGradeService.configureStudentInformationModel(studentId,model);

        return "studentInformation";
    }

    @GetMapping(value = "/grades/{id}/{gradeType}")
    public String deleteGrade(@PathVariable("id")int id, @PathVariable("gradeType")String gradeType, Model model){
        int studentId = studentAndGradeService.deleteGrade(id, gradeType);
        if(studentId==0){
            return "error";
        }
        studentAndGradeService.configureStudentInformationModel(studentId,model);
        return "studentInformation";
    }
}
