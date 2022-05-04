package edu.uoc.epcsd.showcatalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "First name must be shorter than 50 characters")
    private String name;

}