package md.vladdubceac.learning.springmvc.repository;

import md.vladdubceac.learning.springmvc.models.HistoryGrade;
import org.springframework.data.repository.CrudRepository;

public interface HistoryGradeDao extends CrudRepository<HistoryGrade, Integer> {
    public Iterable<HistoryGrade> findGradeByStudentId(int studentId);

    void deleteByStudentId(int id);
}
