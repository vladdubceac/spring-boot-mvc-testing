package md.vladdubceac.learning.springmvc.repository;

import md.vladdubceac.learning.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

public interface ScienceGradeDao extends CrudRepository<ScienceGrade, Integer> {
    Iterable<ScienceGrade> findGradeByStudentId(int studentId);

    void deleteByStudentId(int id);
}
