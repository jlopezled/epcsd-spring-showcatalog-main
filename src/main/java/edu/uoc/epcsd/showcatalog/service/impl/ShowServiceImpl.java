package edu.uoc.epcsd.showcatalog.service.impl;

import edu.uoc.epcsd.showcatalog.Utils;
import edu.uoc.epcsd.showcatalog.entities.Category;
import edu.uoc.epcsd.showcatalog.entities.Performance;
import edu.uoc.epcsd.showcatalog.entities.Show;
import edu.uoc.epcsd.showcatalog.exceptions.KafkaSerializationException;
import edu.uoc.epcsd.showcatalog.exceptions.NotFoundException;
import edu.uoc.epcsd.showcatalog.kafka.KafkaConstants;
import edu.uoc.epcsd.showcatalog.model.PerformanceDto;
import edu.uoc.epcsd.showcatalog.model.ShowDto;
import edu.uoc.epcsd.showcatalog.model.StatusEnum;
import edu.uoc.epcsd.showcatalog.repositories.CategoryRepository;
import edu.uoc.epcsd.showcatalog.repositories.ShowRepository;
import edu.uoc.epcsd.showcatalog.service.CategoryService;
import edu.uoc.epcsd.showcatalog.service.ShowService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
import java.util.List;


@Log4j2
@Service
public class ShowServiceImpl implements ShowService {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private KafkaTemplate<String, Show> kafkaTemplate;

    @Transactional
    public Show createShow(ShowDto dto) {
        Show entity = new Show();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setImage(dto.getImage());
        entity.setPrice(dto.getPrice());
        entity.setDuration(dto.getDuration());
        entity.setCapacity(dto.getCapacity());
        entity.setOnSaleDate(dto.getOnSaleDate());

        entity = showRepository.save(entity);

        // Create associations with Categories
        if (dto.getCategoriesIds() != null && !dto.getCategoriesIds().isEmpty()) {
            for (Long id : dto.getCategoriesIds()) {
                Category categoryEntity = categoryService.findOneCategory(id);
                // Create associations with Categories
                categoryEntity.addShow(entity);
                entity.addCategory(categoryEntity);
                categoryRepository.save(categoryEntity);
            }
        }


        try {
            kafkaTemplate.send(KafkaConstants.SHOW_TOPIC + KafkaConstants.SEPARATOR + KafkaConstants.COMMAND_ADD, entity);
            log.trace("New show message sent to Kakfa");
        } catch (org.apache.kafka.common.errors.SerializationException e) {
            throw new KafkaSerializationException(e.toString());
        }

        return entity;
    }

    public List<Show> findShowsByName(String name) {
        return showRepository.findByNameContaining(name);
    }

    public Show findOneShow(Long showId) {
        Show showEntity;
        showEntity = showRepository.findById(showId)
                .orElseThrow(() -> new NotFoundException(("Find one show use case. Cannot find Show " +
                        "with id:").concat(showId.toString())));
        return showEntity;
    }

    public void delete(@PathVariable Long id) {
        log.trace("Delete show");
        showRepository.deleteById(id);

    }

    public Show createPerformance(Long showId, PerformanceDto dto) {

        Show showEntity = showRepository.findById(showId)
                .orElseThrow(() -> new NotFoundException(("Create Performance use case. Cannot find Show " +
                        "with id:").concat(showId.toString())));
        Performance entity = new Performance();
        entity.setDate(Utils.dateToLocalDate(dto.getDate()));
        entity.setTime(Utils.dateToLocalTime(dto.getTime()));
        entity.setStreamingUrl(dto.getStreamingUrl());
        entity.setRemainingSeats(showEntity.getCapacity());
        showEntity.addPerformance(entity);
        return showRepository.save(showEntity);

    }

    public Show openShow(Long showId) {
        Show showEntity = findOneShow(showId);
        showEntity.setStatus(StatusEnum.OPENED);
        log.trace("Update  all performances for the show to status 'Opened'");
        for (Performance p : showEntity.getPerformances()) {
            p.setStatus(StatusEnum.OPENED);
        }
        return showRepository.save(showEntity);

    }

    public Show cancelShow(Long showId) {
        Show showEntity = findOneShow(showId);
        showEntity.setStatus(StatusEnum.CANCELLED);
        log.trace("Update all performances for the show to status 'Cancelled'");
        for (Performance p : showEntity.getPerformances()) {
            p.setStatus(StatusEnum.CANCELLED);
        }
        return showRepository.save(showEntity);

    }

    public List<Show> findShowsByCategory(Category category) {
        return showRepository.findAllByCategory(category);
    }


    public void deleteCategoryAssociated(Category category) {
        for (Show s : category.getShows()) {
            log.trace("Remove the category from shows");
            s.removeCategory(category);
            showRepository.save(s);
        }

    }


}

