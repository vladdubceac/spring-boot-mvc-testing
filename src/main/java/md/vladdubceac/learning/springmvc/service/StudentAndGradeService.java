package md.vladdubceac.learning.springmvc.service;

import jakarta.transaction.Transactional;
import md.vladdubceac.learning.springmvc.models.CollegeStudent;
import md.vladdubceac.learning.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    public void createStudent(String firstName, String lastName, String emailAddress){
        CollegeStudent student = new CollegeStudent(firstName,lastName,emailAddress);
        studentDao.save(student);}

    public boolean checkIfStudentIsNull(int id){
        Optional<CollegeStudent>  student = studentDao.findById(id);
        return student.isPresent();
    }

    public void deleteStudent(int id){
        if(checkIfStudentIsNull(id)){
            studentDao.deleteById(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook(){
        return studentDao.findAll();
    }
}
