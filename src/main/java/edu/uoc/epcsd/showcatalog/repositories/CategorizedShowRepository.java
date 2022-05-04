package edu.uoc.epcsd.showcatalog.repositories;

import edu.uoc.epcsd.showcatalog.entities.CategorizedShow;
import edu.uoc.epcsd.showcatalog.entities.Category;
import edu.uoc.epcsd.showcatalog.entities.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategorizedShowRepository extends JpaRepository<CategorizedShow, Long> {

    void deleteByCategory(Category categoryEntity);

    void deleteByShow(Show showEntity);

    List<CategorizedShow> findAllByCategory(Category category);

    List<Show> findAllShowsByCategory (Category category);

}
