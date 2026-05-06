package com.Taruni.Api_Aggregator.repository;

import com.Taruni.Api_Aggregator.model.AggregatedData;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AggregatedDataRepository  extends JpaRepository<AggregatedData, UUID>{
  Optional<AggregatedData> findByCacheKey(String cacheKey);
}
