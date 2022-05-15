package edu.uoc.epcsd.showcatalog.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.uoc.epcsd.showcatalog.model.StatusEnum;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;


@Embeddable
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Performance {

    @NotNull
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;

    @NotNull
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm")
    @Column(name = "time")
    private LocalTime time;

    @Column(name = "streaming_url")
    private String streamingUrl;

    @NotNull
    @Column(name = "remaining_seats")
    private Long remainingSeats;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum status = StatusEnum.CREATED;


}
