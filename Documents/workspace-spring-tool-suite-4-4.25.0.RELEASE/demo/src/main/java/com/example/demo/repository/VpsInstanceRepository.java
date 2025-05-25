package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.VpsInstance;

@Repository
public interface VpsInstanceRepository extends JpaRepository<VpsInstance, Long> {

}
