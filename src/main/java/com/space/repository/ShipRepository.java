package com.space.repository;

import com.space.model.ShipEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipRepository extends CrudRepository<ShipEntity, Long>, ShipFilterRepository {
}
