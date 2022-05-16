package edu.uoc.epcsd.showcatalog.service.impl;

import edu.uoc.epcsd.showcatalog.entities.Category;
import edu.uoc.epcsd.showcatalog.exceptions.DataIntegrityException;
import edu.uoc.epcsd.showcatalog.exceptions.NotFoundException;
import edu.uoc.epcsd.showcatalog.model.CategoryDto;
import edu.uoc.epcsd.showcatalog.repositories.CategoryRepository;
import edu.uoc.epcsd.showcatalog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findOneCategory(Long id) {

        Category categoryEntity;
        categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(("Find one category use case. Cannot find Category " +
                        "with id:").concat(id.toString())));
        return categoryEntity;
    }

    public Category createCategory(CategoryDto dto) {
        Category entity = new Category();
        entity.setName(dto.getName());

        try {
            return categoryRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException(("Category name must be unique. ").concat(dto.getName()));
        }
    }

    public void delete(Long id) {

        categoryRepository.deleteById(id);
    }


}


