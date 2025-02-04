package com.pakal.cloud.controller;

import com.pakal.cloud.dto.BlogFormDTO;
import com.pakal.cloud.model.BlogForm;
import com.pakal.cloud.service.BlogFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;


// Permitir CORS en este controlador
@CrossOrigin(origins = {
    "http://localhost:8082",
    "https://java-mongo.onrender.com",
    "http://localhost:8083",
    "http://localhost:5173",
    "https://astonishing-lamington-2bfbd2.netlify.app"}) 
@RestController
@RequestMapping("/api/blog-forms")
@RequiredArgsConstructor
@Tag(name = "Blog Form API", description = "Operations for blog form management")
public class BlogFormController {
    
    private final BlogFormService service;
    
    @PostMapping
    @Operation(summary = "Create a blog form", description = "Creates a new blog form entry")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Blog form created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<BlogForm> create(@Valid @RequestBody BlogFormDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all blog forms with pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved blog forms"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved blog form"),
        @ApiResponse(responseCode = "404", description = "Blog form not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<BlogForm> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a blog form")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blog form updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Blog form not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<BlogForm> update(@PathVariable String id, @Valid @RequestBody BlogFormDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a blog form")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Blog form deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Blog form not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}