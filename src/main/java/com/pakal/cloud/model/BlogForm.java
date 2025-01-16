package com.pakal.cloud.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "blog_forms")
public class BlogForm {
    @Id
    private String id;
    private String email;
    private String fullName;
    private String description;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
}
