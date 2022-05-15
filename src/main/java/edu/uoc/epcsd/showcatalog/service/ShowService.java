package edu.uoc.epcsd.showcatalog.service;

import edu.uoc.epcsd.showcatalog.entities.Category;
import edu.uoc.epcsd.showcatalog.entities.Show;
import edu.uoc.epcsd.showcatalog.model.PerformanceDto;
import edu.uoc.epcsd.showcatalog.model.ShowDto;

import java.util.List;

public interface ShowService {


    Show createShow(ShowDto dto);

    List<Show> findShowsByName(String name);

    List<Show> findShowsByCategory(Category category);

    Show findOneShow(Long showId);

    void delete(Long id);

    Show createPerformance(Long showId, PerformanceDto dto);

    Show openShow(Long showId);

    Show cancelShow(Long showId);

    void deleteCategoryAssociated(Category category);


}
