package edu.uoc.epcsd.showcatalog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowDto {

    @NotNull(message = "Show name is required")
    @NotBlank(message = "Show name cannot be blank")
    @Size(max = 50, message = "Show name must be shorter than 50 characters")
    private String name;

    @Size(max = 255, message = "Image attribute must be shorter than 256 characters")
    private String image;

    @Min(value = 0, message = "The price must be positive")
    @Column(name = "price")
    private Float price;

    @Min(value = 0L, message = "The duration must be positive")
    @Column(name="duration")
    private Long duration;

    @Min(value = 0L, message = "The capacity must be positive")
    @Column(name="capacity")
    private Long capacity;

    @JsonFormat(pattern="yyyy-MM-dd")
    @NotNull(message = "On sale date is required")
    private Date onSaleDate;

    @NotEmpty
    private List<Long> categoriesIds;

}