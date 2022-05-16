package edu.uoc.epcsd.showcatalog.controllers;

import edu.uoc.epcsd.showcatalog.entities.Category;
import edu.uoc.epcsd.showcatalog.entities.Show;
import edu.uoc.epcsd.showcatalog.exceptions.InvalidDataException;
import edu.uoc.epcsd.showcatalog.model.CategoryDto;
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


/**
 * The type Category controller.
 */
@Log4j2
@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ShowService showService;


    /**
     * Gets all categories.
     *
     * @return a list containing all categories
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        log.trace("getAllCategories");
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    /**
     * Find shows by category.
     *
     * @param categoryId the category id
     * @return a list containing all shows that match the category provided
     */
    @GetMapping("/{id}/shows")
    public ResponseEntity<List<Show>> findShowsByCategory(@Valid @PathVariable("id") Long categoryId) {
        log.trace("findShowsByCategory");
        Category categoryEntity = categoryService.findOneCategory(categoryId);
        return ResponseEntity.ok(showService.findShowsByCategory(categoryEntity));
    }

    /**
     * Create category.
     *
     * @param dto    the dto
     * @param result the result
     * @return the new category created
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDto dto, @NotNull BindingResult result) {
        log.trace("createCategory");

        if (result.hasErrors()) {
            throw new InvalidDataException(result);
        } else {
            return ResponseEntity.ok(categoryService.createCategory(dto));
        }

    }

    /**
     * Delete a category and all its associations with shows
     *
     * @param id the id
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK, reason = "Deleted")
    @Transactional
    public void delete(@PathVariable Long id) {
        log.trace("deleteCategory");
        Category categoryEntity = categoryService.findOneCategory(id);
        log.trace("Delete all associations with shows");
        showService.deleteCategoryAssociated(categoryEntity);
        categoryService.delete(id);


    }


}
