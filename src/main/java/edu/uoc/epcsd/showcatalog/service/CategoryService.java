package edu.uoc.epcsd.showcatalog.service;

import edu.uoc.epcsd.showcatalog.entities.Category;
import edu.uoc.epcsd.showcatalog.model.CategoryDto;

import java.util.List;


public interface CategoryService {

    List<Category> findAllCategories();

    Category findOneCategory(Long id);

    Category createCategory(CategoryDto dto);

    void delete(Long id);

}
