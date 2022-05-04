package edu.uoc.epcsd.showcatalog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceDto {

    @JsonFormat(pattern="yyyy-MM-dd")
    @NotNull(message = "Date is required")
    private Date date;

    @JsonFormat(pattern="HH:mm")
    @NotNull(message = "Time is required")
    private Date time;

    @NotBlank(message = "Streaming URL is required")
    @Size(max = 255, message = "Streaming URL must be shorter than 256 characters")
    private String streamingUrl;


}