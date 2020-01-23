package com.space.repository;

import com.space.controller.ShipOrder;
import com.space.model.ShipEntity;
import com.space.model.ShipType;

import java.util.List;

public interface ShipFilterRepository {
    List<ShipEntity> findByCriteria(String name,
                                    String planet,
                                    ShipType shipType,
                                    Long after,
                                    Long before,
                                    Boolean isUsed,
                                    Double minSpeed,
                                    Double maxSpeed,
                                    Integer minCrewSize,
                                    Integer maxCrewSize,
                                    Double minRating,
                                    Double maxRating,
                                    ShipOrder order,
                                    Integer pageNumber,
                                    Integer pageSize,
                                    boolean usePaging);
}
