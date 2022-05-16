package edu.uoc.epcsd.showcatalog.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.uoc.epcsd.showcatalog.model.StatusEnum;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@ToString
@Getter
@Setter
//@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "price")
    private Float price;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "capacity")
    private Long capacity;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd")
    @Column(name = "on_sale_date")
    private Date onSaleDate;


    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum status = StatusEnum.CREATED;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "PERFORMANCE", joinColumns = @JoinColumn(name = "ID_SHOW"))
    private Set<Performance> performances = new HashSet<>();


    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "CATEGORY_SHOW",
            joinColumns = @JoinColumn(name = "SHOW_ID"),
            inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID")
    )

    private Set<Category> categories = new HashSet<>();

    public void addPerformance(Performance performance) {
        this.performances.add(performance);
    }

    public void addCategory(Category c) {
        this.categories.add(c);

    }

    public void removeCategory(Category c) {
        this.categories.remove(c);
    }
    

}
