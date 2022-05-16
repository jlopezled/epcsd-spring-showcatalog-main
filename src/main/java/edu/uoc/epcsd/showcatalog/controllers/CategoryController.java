package edu.uoc.epcsd.showcatalog.controllers;

import edu.uoc.epcsd.showcatalog.entities.Category;
import edu.uoc.epcsd.showcatalog.entities.Show;
import edu.uoc.epcsd.showcatalog.exceptions.InvalidDataException;
import edu.uoc.epcsd.showcatalog.model.CategoryDto;
import edu.uoc.epcsd.showcatalog.repositories.CategoryRepository;
import edu.uoc.epcsd.showcatalog.service.CategoryService;
import edu.uoc.epcsd.showcatalog.service.ShowService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@Log4j2
@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ShowService showService;


    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        log.trace("getAllCategories");
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/{id}/shows")
    public ResponseEntity<List<Show>> findShowsByCategory(@Valid @PathVariable("id") Long categoryId) {
        log.trace("findShowsByCategory");
        Category categoryEntity = categoryService.findOneCategory(categoryId);
        return ResponseEntity.ok(showService.findShowsByCategory(categoryEntity));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDto dto, @NotNull BindingResult result) {
        log.trace("createCategory");

        if (result.hasErrors()) {
            throw new InvalidDataException(result);
        } else {
            return ResponseEntity.ok(categoryService.createCategory(dto));
        }

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK, reason = "Deleted")
    @Transactional
    public void delete(@PathVariable Long id) {

        Category categoryEntity = categoryService.findOneCategory(id);
        showService.deleteCategoryAssociated(categoryEntity);
        categoryRepository.deleteById(id);


    }


}
