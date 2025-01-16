package com.pakal.cloud.repository;

import com.pakal.cloud.model.BlogForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BlogFormRepository extends MongoRepository<BlogForm, String> {
    Page<BlogForm> findByIsDeletedFalse(Pageable pageable);
    Page<BlogForm> findByCountryAndIsDeletedFalse(String country, Pageable pageable);
    Page<BlogForm> findByFullNameContainingAndIsDeletedFalse(String fullName, Pageable pageable);
    Page<BlogForm> findByCreatedAtBetweenAndIsDeletedFalse(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
