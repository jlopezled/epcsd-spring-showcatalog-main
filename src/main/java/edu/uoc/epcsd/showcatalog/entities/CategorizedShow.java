package edu.uoc.epcsd.showcatalog.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "SHOW_CATEGORIES")
@Immutable
public class CategorizedShow {
    @EmbeddedId
    private Id id = new Id();
    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            name = "ID_SHOW",
            insertable = false, updatable = false)
    private Show show;
    @ManyToOne
    @JoinColumn(
            name = "ID_CATEGORY",
            insertable = false, updatable = false)
    private Category category;

    public CategorizedShow() {
    }


    public CategorizedShow(
            Show show,
            Category category) {

        this.show = show;
        this.category = category;
        this.id.showId = show.getId();
        this.id.categoryId = category.getId();
        show.addCategorizedShow(this);
        category.addCategorizedShow(this);

    }

    public Category getCategory() {
        return this.category;
    }

    public Show getShow() {
        return this.show;
    }

    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "ID_SHOW")
        private Long showId;

        @Column(name = "ID_CATEGORY")
        private Long categoryId;

        public Id() {
        }

        public Id(Long showId, Long categoryId) {
            this.showId = showId;
            this.categoryId = categoryId;
        }

    }

}