package com.space.repository;

import com.space.controller.ShipOrder;
import com.space.model.ShipEntity;
import com.space.model.ShipType;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShipFilterRepositoryImpl implements ShipFilterRepository  {

    @PersistenceContext
    private EntityManager manager;

    public List<ShipEntity> findByCriteria(String name,
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
                                           boolean usePaging) {
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;

        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<ShipEntity> cq = cb.createQuery(ShipEntity.class);
        Root<ShipEntity> ship = cq.from(ShipEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (name != null) {
            Predicate namePredicate = cb.like(ship.get("name"), "%" + name + "%");
            predicates.add(namePredicate);
            //cq.where(namePredicate);
        }
        if (planet != null) {
            Predicate planetPredicate = cb.like(ship.get("planet"), "%" + planet + "%");
            predicates.add(planetPredicate);
            //cq.where(planetPredicate);
        }

        if (shipType != null ) {
            Predicate predicate = cb.equal(ship.get("shipType"), shipType.name());
            predicates.add(predicate);
            //cq.where(predicate);
        }

        if (before != null && after != null) {
            Date beforeDate = new Date(before);
            Date afterDate = new Date(after);

            Predicate less = cb.lessThan(ship.get("prodDate"), beforeDate);
            Predicate greater = cb.greaterThan(ship.get("prodDate"), afterDate);

            //Predicate predicateBeforeAfter = cb.between(ship.get("prodDate"), afterDate, beforeDate);

            //cq.where(predicateBeforeAfter);
            predicates.add(less);
            predicates.add(greater);
        }

        Long PROD_DATA_MAX = 33134787169000L;
        if (before == null && after != null) {
            Date afterDate = new Date(after);
            Predicate predicate = cb.between(ship.get("prodDate"), afterDate, new Date(PROD_DATA_MAX));
            predicates.add(predicate);
            //cq.where(predicate);
        }

        Long PROD_DATA_MIN = 26192288041000L;
        if (before != null && after == null) {
            Date beforeDate = new Date(before);
            Predicate predicate = cb.between(ship.get("prodDate"), new Date(PROD_DATA_MIN), beforeDate);
            predicates.add(predicate);
        }
        if (isUsed != null) {
            Predicate predicate = cb.equal(ship.get("isUsed"), isUsed);
            predicates.add(predicate);
        }

        if (minSpeed != null && maxSpeed != null) {
            Predicate predicate = cb.between(ship.get("speed"), minSpeed, maxSpeed);
            predicates.add(predicate);
        }

        Double SPEED_MAX = 0.99;
        if (minSpeed != null && maxSpeed == null) {
            Predicate predicate = cb.between(ship.get("speed"), minSpeed, SPEED_MAX);
            predicates.add(predicate);
        }

        Double SPEED_MIN = 0.01;
        if (minSpeed == null && maxSpeed != null) {
            Predicate predicate = cb.between(ship.get("speed"), SPEED_MIN, maxSpeed);
            predicates.add(predicate);
        }

        if (minRating != null && maxRating != null) {
            Predicate predicate = cb.between(ship.get("rating"), minRating, maxRating);
            predicates.add(predicate);
        }

        if (minRating != null && maxRating == null) {
            Predicate predicate = cb.greaterThanOrEqualTo(ship.get("rating"), minRating);
            predicates.add(predicate);
        }

        if (minRating == null && maxRating != null) {
            Predicate predicate = cb.lessThanOrEqualTo(ship.get("rating"), maxRating);
            predicates.add(predicate);
        }

        if (minCrewSize != null && maxCrewSize != null) {
            Predicate predicate = cb.between(ship.get("crewSize"), minCrewSize, maxCrewSize);
            predicates.add(predicate);
        }

        Integer CREW_MAX = 9999;
        if (minCrewSize != null && maxCrewSize == null) {
            Predicate predicate = cb.between(ship.get("crewSize"), minCrewSize, CREW_MAX);
            predicates.add(predicate);
        }

        Integer CREW_MIN = 1;
        if (minCrewSize == null && maxCrewSize != null) {
            Predicate predicate = cb.between(ship.get("crewSize"), CREW_MIN, maxCrewSize);
            predicates.add(predicate);
        }

        cq.where(predicates.stream().toArray(Predicate[]::new));

        if (order != null) {
            cq.orderBy(cb.asc(ship.get(order.getFieldName())));
        }

        TypedQuery<ShipEntity> query = manager.createQuery(cq);

        if (usePaging) {
            if (pageNumber == 0) {
                query.setFirstResult(0);
            } else {
                query.setFirstResult(((pageNumber - 1) * pageSize) + pageSize);
            }

            query.setMaxResults(pageSize);
        }
        return query.getResultList();
    }
}
