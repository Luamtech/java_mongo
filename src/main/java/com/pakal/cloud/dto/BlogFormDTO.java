// src/main/java/com/pakal/cloud/dto/BlogFormDTO.java
package com.pakal.cloud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlogFormDTO {
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private String country;
}