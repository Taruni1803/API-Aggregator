package com.Taruni.Api_Aggregator.repository;

import com.Taruni.Api_Aggregator.model.ApiSource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface ApiSourceRepository extends JpaRepository<ApiSource,UUID>{
  Optional<ApiSource> findByName(String name);
  Optional<ApiSource> findByNameAndIsActive(String name, boolean isActive);
}
