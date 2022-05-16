package edu.uoc.epcsd.showcatalog.service;

import edu.uoc.epcsd.showcatalog.entities.Category;
import edu.uoc.epcsd.showcatalog.model.CategoryDto;


public interface CategoryService {

    Category findOneCategory(Long id);

    Category createCategory(CategoryDto dto);

    void delete(Long id);

}
