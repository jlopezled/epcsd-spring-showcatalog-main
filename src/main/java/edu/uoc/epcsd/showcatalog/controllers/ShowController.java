package edu.uoc.epcsd.showcatalog.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.showcatalog.Utils;
import edu.uoc.epcsd.showcatalog.entities.CategorizedShow;
import edu.uoc.epcsd.showcatalog.entities.Category;
import edu.uoc.epcsd.showcatalog.entities.Performance;
import edu.uoc.epcsd.showcatalog.entities.Show;
import edu.uoc.epcsd.showcatalog.exceptions.InvalidDataException;
import edu.uoc.epcsd.showcatalog.exceptions.KafkaSerializationException;
import edu.uoc.epcsd.showcatalog.exceptions.NotFoundException;
import edu.uoc.epcsd.showcatalog.kafka.KafkaConstants;
import edu.uoc.epcsd.showcatalog.model.PerformanceDto;
import edu.uoc.epcsd.showcatalog.model.ShowDto;
import edu.uoc.epcsd.showcatalog.model.StatusEnum;
import edu.uoc.epcsd.showcatalog.model.Views;
import edu.uoc.epcsd.showcatalog.repositories.CategorizedShowRepository;
import edu.uoc.epcsd.showcatalog.repositories.CategoryRepository;
import edu.uoc.epcsd.showcatalog.repositories.ShowRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.metrics.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/shows")
public class ShowController {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategorizedShowRepository categorizedShowRepository;

    @Autowired
    private KafkaTemplate<String, Show> kafkaTemplate;

    @Autowired
    private ObjectMapper defaultMapper;

    @GetMapping
    public ResponseEntity<List<Show>> findShowsByName(@RequestParam String name,
                                                      @RequestParam Optional<Boolean> extended) {
        log.trace("findShowsByName");

 /*       if (extended.isEmpty())
            defaultMapper.setConfig(defaultMapper.getSerializationConfig().withView(Views.Shows.Basic.class));
        else {
            if (extended.get()) {
                defaultMapper.setConfig(defaultMapper.getSerializationConfig().withView(Views.Shows.Extended.class));
            } else {
                defaultMapper.setConfig(defaultMapper.getSerializationConfig().withView(Views.Shows.Basic.class));
            }
        }*/
        return ResponseEntity.ok(showRepository.findByNameContaining(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> findOneShow(@PathVariable("id") Long showId,
                                            @RequestParam Optional<Boolean> extended) {
        log.trace("findOneShow");

 /*       if (extended.isEmpty())
            defaultMapper.setConfig(defaultMapper.getSerializationConfig().withView(Views.OneShow.Basic.class));
        else {
            if (extended.get()) {
                defaultMapper.setConfig(defaultMapper.getSerializationConfig().withView(Views.OneShow.Extended.class));
            } else {
                defaultMapper.setConfig(defaultMapper.getSerializationConfig().withView(Views.OneShow.Basic.class));
            }
        }*/
        Show showEntity = showRepository.findById(showId)
                .orElseThrow(() -> new NotFoundException(("Find one show use case. Cannot find Show " +
                        "with id:").concat(showId.toString())));
        return ResponseEntity.ok(showEntity);

    }

    @GetMapping("/{id}/performances")
    public ResponseEntity<List<Performance>> findOneShowPerformances(@PathVariable("id") Long showId) {
        log.trace("findOneShowPerformances");

        Show showEntity = showRepository.findById(showId)
                .orElseThrow(() -> new NotFoundException(("Find one show use case. Cannot find Show " +
                        "with id:").concat(showId.toString())));

        return ResponseEntity.ok(showEntity.getPerformances().stream().toList());

    }

    @PostMapping
    @Transactional
    public ResponseEntity<Show> createShow(@Valid @RequestBody ShowDto dto, @NotNull BindingResult result) {
        log.trace("createCategory");

        ResponseEntity<Show> responseEntity;
        if (result.hasErrors()) {
            throw new InvalidDataException(result);
        } else {
            Show entity = new Show();
            entity.setName(dto.getName());
            entity.setImage(dto.getImage());
            entity.setPrice(dto.getPrice());
            entity.setDuration(dto.getDuration());
            entity.setCapacity(dto.getCapacity());
            entity.setOnSaleDate(dto.getOnSaleDate());

            entity = showRepository.save(entity);

            // Create associations with Categories
            if (dto.getCategoriesIds() != null && !dto.getCategoriesIds().isEmpty()) {
                for (Long id : dto.getCategoriesIds()) {
                    Category categoryEntity = categoryRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException(("Create Show use case. Cannot find Category " +
                                    "with id:").concat(id.toString())));
                    //entity.addCategory(categoryEntity);
                    CategorizedShow category = new CategorizedShow(entity, categoryEntity);
                    entity.addCategorizedShow(category);
                    categorizedShowRepository.save(category);
                }
            }
            try {
                kafkaTemplate.send(KafkaConstants.COMMAND_ADD, entity);
            } catch (org.apache.kafka.common.errors.SerializationException e) {
                throw new KafkaSerializationException(e.toString());
            }

            responseEntity = ResponseEntity.ok(entity);
            return responseEntity;
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK, reason = "Deleted")
    public void delete(@PathVariable Long id) {

        Show showEntity = showRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(("Delete Show use case. Cannot find Show " +
                        "with id:").concat(id.toString())));

        categorizedShowRepository.deleteByShow(showEntity);
        showRepository.deleteById(id);

    }

    /**
     * Create performance show.
     *
     * @param dto the dto
     * @return the show
     * @throws NotFoundException the not found exception
     */
    @PostMapping("/{id}/performances")
    //@JsonView(Views.Performances.Basic.class)
    public ResponseEntity<Show> createPerformance(@Valid @PathVariable("id") Long showId,
                                                  @RequestBody PerformanceDto dto,
                                                  BindingResult result) {
        log.trace("createPerformance");

        ResponseEntity<Show> responseEntity;
        if (result.hasErrors()) {
            throw new InvalidDataException(result);
        } else {

            Show showEntity = showRepository.findById(showId)
                    .orElseThrow(() -> new NotFoundException(("Create Performance use case. Cannot find Show " +
                            "with id:").concat(showId.toString())));
            Performance entity = new Performance();
            entity.setDate(Utils.dateToLocalDate(dto.getDate()));
            entity.setTime(Utils.dateToLocalTime(dto.getTime()));
            entity.setStreamingUrl(dto.getStreamingUrl());
            showEntity.addPerformance(entity);
            responseEntity = ResponseEntity.ok(showRepository.save(showEntity));

            return responseEntity;
        }


    }

    @PutMapping("/{id}/open")
    @Transactional
    public ResponseEntity<Show> openShow(@PathVariable("id") Long showId) {
        log.trace("openShow");

        Show showEntity = showRepository.findById(showId)
                .orElseThrow(() -> new NotFoundException(("Open show use case. Cannot find Show " +
                        "with id:").concat(showId.toString())));

        showEntity.setStatus(StatusEnum.OPENED);
        for(Performance p:showEntity.getPerformances()) {
            p.setStatus(StatusEnum.OPENED);
        }
        return ResponseEntity.ok(showRepository.save(showEntity));

    }

    @PutMapping("/{id}/cancel")
    @Transactional
    public ResponseEntity<Show> cancelShow(@PathVariable("id") Long showId) {
        log.trace("openShow");

        Show showEntity = showRepository.findById(showId)
                .orElseThrow(() -> new NotFoundException(("Open show use case. Cannot find Show " +
                        "with id:").concat(showId.toString())));

        showEntity.setStatus(StatusEnum.CANCELLED);
        for(Performance p:showEntity.getPerformances()) {
            p.setStatus(StatusEnum.CANCELLED);
        }
        return ResponseEntity.ok(showRepository.save(showEntity));

    }



}
