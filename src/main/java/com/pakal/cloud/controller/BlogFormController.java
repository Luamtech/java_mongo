package com.pakal.cloud.controller;

import com.pakal.cloud.dto.BlogFormDTO;
import com.pakal.cloud.model.BlogForm;
import com.pakal.cloud.service.BlogFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/blog-forms")
@RequiredArgsConstructor
@Tag(name = "Blog Form API", description = "Operations for blog form management")
public class BlogFormController {
    
    private final BlogFormService service;
    
    @PostMapping
    @Operation(summary = "Create a new blog form")
    public ResponseEntity<BlogForm> create(@Valid @RequestBody BlogFormDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all blog forms with pagination")
    public ResponseEntity<Page<BlogForm>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (country != null || fullName != null || startDate != null || endDate != null) {
            return ResponseEntity.ok(service.findByFilters(country, fullName, startDate, endDate, page, size, sortBy, direction));
        }
        
        return ResponseEntity.ok(service.findAll(page, size, sortBy, direction));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a blog form by ID")
    public ResponseEntity<BlogForm> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a blog form")
    public ResponseEntity<BlogForm> update(@PathVariable String id, @Valid @RequestBody BlogFormDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}