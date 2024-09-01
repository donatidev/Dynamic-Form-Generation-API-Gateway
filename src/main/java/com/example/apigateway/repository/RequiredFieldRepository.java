package com.example.apigateway.repository;

import com.example.apigateway.model.RequiredField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequiredFieldRepository extends JpaRepository<RequiredField, Long> {
}