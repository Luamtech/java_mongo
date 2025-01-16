package com.pakal.cloud.service;

import com.pakal.cloud.dto.BlogFormDTO;
import com.pakal.cloud.model.BlogForm;
import com.pakal.cloud.repository.BlogFormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class BlogFormService {
    private final BlogFormRepository repository;
    
    public BlogForm create(BlogFormDTO dto) {
        log.info("Creating new blog form for email: {}", dto.getEmail());
        BlogForm form = new BlogForm();
        form.setEmail(dto.getEmail());
        form.setFullName(dto.getFullName());
        form.setDescription(dto.getDescription());
        form.setCountry(dto.getCountry());
        form.setCreatedAt(LocalDateTime.now());
        form.setUpdatedAt(LocalDateTime.now());
        form.setDeleted(false);
        return repository.save(form);
    }
    
    public Page<BlogForm> findAll(int page, int size, String sortBy, String direction) {
        log.info("Retrieving blog forms page {} with size {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findByIsDeletedFalse(pageable);
    }

    public Page<BlogForm> findByFilters(
            String country, 
            String fullName, 
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page, 
            int size, 
            String sortBy, 
            String direction) {
        
        log.info("Retrieving filtered blog forms page {} with size {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (country != null && !country.isEmpty()) {
            return repository.findByCountryAndIsDeletedFalse(country, pageable);
        }
        
        if (fullName != null && !fullName.isEmpty()) {
            return repository.findByFullNameContainingAndIsDeletedFalse(fullName, pageable);
        }
        
        if (startDate != null && endDate != null) {
            return repository.findByCreatedAtBetweenAndIsDeletedFalse(startDate, endDate, pageable);
        }
        
        return repository.findByIsDeletedFalse(pageable);
    }
    
    public BlogForm findById(String id) {
        log.info("Retrieving blog form with id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog form not found"));
    }
    
    public BlogForm update(String id, BlogFormDTO dto) {
        log.info("Updating blog form with id: {}", id);
        BlogForm form = findById(id);
        form.setEmail(dto.getEmail());
        form.setFullName(dto.getFullName());
        form.setDescription(dto.getDescription());
        form.setCountry(dto.getCountry());
        form.setUpdatedAt(LocalDateTime.now());
        return repository.save(form);
    }
    
    public void delete(String id) {
        log.info("Performing logical deletion of blog form with id: {}", id);
        BlogForm form = findById(id);
        form.setDeleted(true);
        form.setUpdatedAt(LocalDateTime.now());
        repository.save(form);
    }
}