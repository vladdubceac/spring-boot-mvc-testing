package md.vladdubceac.learning.springmvc.repository;

import md.vladdubceac.learning.springmvc.models.MathGrade;
import org.springframework.data.repository.CrudRepository;

public interface MathGradeDao extends CrudRepository<MathGrade, Integer> {
    public Iterable<MathGrade> findGradeByStudentId(int studentId);

    void deleteByStudentId(int id);
}
