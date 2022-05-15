package edu.uoc.epcsd.showcatalog.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uoc.epcsd.showcatalog.model.StatusEnum;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;


@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

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

    @OneToMany(mappedBy = "show")
    @JsonProperty(value = "listOfCategories")
    private Set<CategorizedShow> categorizedShows = new HashSet<>();

    @Transient
    private String categories;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "PERFORMANCE", joinColumns = @JoinColumn(name = "ID_SHOW"))
    private Set<Performance> performances = new HashSet<>();

    public void addPerformance(Performance performance) {
        this.performances.add(performance);
    }


    public void addCategorizedShow(CategorizedShow c) {
        this.categorizedShows.add(c);
    }

    /**
     * @return A String containing all categories delimited by '|'
     */
    public String getCategories() {
        StringJoiner res = new StringJoiner("|");
        for (CategorizedShow c : categorizedShows) {
            res.add(c.getCategory().getName());
        }
        return res.toString();

    }

}
