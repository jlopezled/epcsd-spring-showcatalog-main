package edu.uoc.epcsd.showcatalog.controllers;

import edu.uoc.epcsd.showcatalog.entities.Performance;
import edu.uoc.epcsd.showcatalog.entities.Show;
import edu.uoc.epcsd.showcatalog.exceptions.InvalidDataException;
import edu.uoc.epcsd.showcatalog.exceptions.NotFoundException;
import edu.uoc.epcsd.showcatalog.model.PerformanceDto;
import edu.uoc.epcsd.showcatalog.model.ShowDto;
import edu.uoc.epcsd.showcatalog.service.ShowService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @GetMapping
    public ResponseEntity<List<Show>> findShowsByName(@RequestParam String name) {
        log.trace("findShowsByName");
        return ResponseEntity.ok(showService.findShowsByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> findOneShow(@PathVariable("id") Long showId) {
        log.trace("findOneShow");
        return ResponseEntity.ok(showService.findOneShow(showId));
    }

    @GetMapping("/{id}/performances")
    public ResponseEntity<List<Performance>> findOneShowPerformances(@PathVariable("id") Long showId) {
        log.trace("findOneShowPerformances");
        Show showEntity = showService.findOneShow(showId);
        return ResponseEntity.ok(showEntity.getPerformances().stream().toList());

    }

    @PostMapping
    @Transactional
    public ResponseEntity<Show> createShow(@Valid @RequestBody ShowDto dto, @NotNull BindingResult result) {
        log.trace("createCategory");

        if (result.hasErrors()) {
            throw new InvalidDataException(result);
        } else {
            showService.createShow(dto);
            return ResponseEntity.ok(showService.createShow(dto));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK, reason = "Deleted")
    public void delete(@PathVariable Long id) {
        showService.delete(id);
    }

    /**
     * Create performance show.
     *
     * @param dto the dto
     * @return the show
     * @throws NotFoundException the not found exception
     */
    @PostMapping("/{id}/performances")
    public ResponseEntity<Show> createPerformance(@Valid @PathVariable("id") Long showId,
                                                  @RequestBody PerformanceDto dto,
                                                  BindingResult result) {
        log.trace("createPerformance");

        if (result.hasErrors()) {
            throw new InvalidDataException(result);
        } else {

            return ResponseEntity.ok(showService.createPerformance(showId, dto));

        }

    }

    @PutMapping("/{id}/open")
    public ResponseEntity<Show> openShow(@PathVariable("id") Long showId) {
        log.trace("openShow");
        return ResponseEntity.ok(showService.openShow(showId));

    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Show> cancelShow(@PathVariable("id") Long showId) {
        log.trace("openShow");
        return ResponseEntity.ok(showService.cancelShow(showId));

    }


}
