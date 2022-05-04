package edu.uoc.epcsd.showcatalog.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.showcatalog.entities.*;
import edu.uoc.epcsd.showcatalog.exceptions.DataIntegrityException;
import edu.uoc.epcsd.showcatalog.exceptions.InvalidDataException;
import edu.uoc.epcsd.showcatalog.exceptions.NotFoundException;
import edu.uoc.epcsd.showcatalog.model.CategoryDto;
import edu.uoc.epcsd.showcatalog.model.Views;
import edu.uoc.epcsd.showcatalog.repositories.CategorizedShowRepository;
import edu.uoc.epcsd.showcatalog.repositories.CategoryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Log4j2
@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategorizedShowRepository categorizedShowRepository;

    @Autowired
    private ObjectMapper defaultMapper;

    public CategoryController() {
    }


    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        log.trace("getAllCategories");

        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/{id}/shows")
    public ResponseEntity<List<Show>> findShowsByCategory(@Valid @PathVariable("id") Long categoryId,
                                                          @RequestParam Optional<Boolean> extended) {
        log.trace("findShowsByCategory");

       /* if (extended.isEmpty())
            defaultMapper.setConfig(defaultMapper.getSerializationConfig().withView(Views.CategorizedShow.Basic.class));
        else {
            if (extended.get()) {
                defaultMapper.setConfig(defaultMapper.getSerializationConfig().withView(Views.CategorizedShow.Extended.class));
            } else {
                defaultMapper.setConfig(defaultMapper.getSerializationConfig().withView(Views.CategorizedShow.Basic.class));
            }
        }*/
        Category categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(("Find show by category use case. Cannot find Category " +
                        "with id:").concat(categoryId.toString())));

        List<Show> result = new ArrayList<Show>();
        categorizedShowRepository.findAllByCategory(categoryEntity).forEach(categorizedShow -> {
        result.add(categorizedShow.getShow());});
        return ResponseEntity.ok(result);

   }


    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDto dto, @NotNull BindingResult result) {
        log.trace("createCategory");

        if (result.hasErrors()) {
            throw new InvalidDataException(result);
        } else {
            Category entity = new Category();
            entity.setName(dto.getName());

            try {
                return ResponseEntity.ok(categoryRepository.save(entity));
            } catch (DataIntegrityViolationException e) {
                throw new DataIntegrityException(("Category name must be unique. ").concat(dto.getName()));
            }
        }

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK, reason = "Deleted")
    @Transactional
    public void delete(@PathVariable Long id) {

        Category categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(("Delete Category use case. Cannot find Category " +
                        "with id:").concat(id.toString())));

        // Before deleting the category, it is necessary to delete the category from de joined table shows_categories
        categorizedShowRepository.deleteByCategory(categoryEntity);

        categoryRepository.deleteById(id);


    }


}
