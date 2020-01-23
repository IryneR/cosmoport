package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipEntity;
import com.space.model.ShipTO;
import com.space.model.ShipType;
import com.space.repository.ShipFilterRepository;
import com.space.repository.ShipFilterRepositoryImpl;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ShipService {
    private Double SPEED_MIN = 0.01;
    private Double SPEED_MAX = 0.99;
    private Integer CREW_MAX = 9999;
    private Integer CREW_MIN = 1;
    private Long PROD_DATA_MIN = 26192288041000L;
    private Long PROD_DATA_MAX = 33134787169000L;
    private int CURRENT_YEAR = 3019;

    private ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public List<Ship> getShips(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                               Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating,
                               Double maxRating, ShipOrder order, Integer pageNumber, Integer pageSize) {

        return convert(this.shipRepository.findByCriteria(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize, true));
    }

    public Integer getShipsCount(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                                 Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating,
                                 Double maxRating) {
        return convert(this.shipRepository.findByCriteria(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating, null, 0, 0, false)).size();

    }

    private ShipEntity convert(ShipTO shipTO){
        ShipEntity shipEntity = new ShipEntity();
        shipEntity.setName(shipTO.getName());
        shipEntity.setCrewSize(shipTO.getCrewSize());
        shipEntity.setPlanet(shipTO.getPlanet());
        shipEntity.setProdDate(new Date(shipTO.getProdDate()));
        shipEntity.setShipType(shipTO.getShipType().name());
        shipEntity.setSpeed(shipTO.getSpeed());
        shipEntity.setUsed(shipTO.isUsed());
        return shipEntity;

    }

    private List<Ship> convert(List<ShipEntity> shipEntityList) {
        List<Ship> result = new ArrayList<>();
        for (ShipEntity entity : shipEntityList) {
            result.add(convert(entity));
        }
        return result;
    }

    private Ship convert(ShipEntity shipEntity){
        Ship ship = new Ship();
        ship.setId(shipEntity.getId());
        ship.setName(shipEntity.getName());
        ship.setCrewSize(shipEntity.getCrewSize());
        ship.setPlanet(shipEntity.getPlanet());
        ship.setProdDate(shipEntity.getProdDate().getTime());
        ship.setShipType(ShipType.valueOf(shipEntity.getShipType()));
        ship.setSpeed(shipEntity.getSpeed());
        ship.setUsed(shipEntity.getUsed());
        ship.setRating(shipEntity.getRating());
        return ship;
    }

    public Ship createShip(ShipTO shipTO) {
        if (shipTO.isUsed()==null){
            shipTO.setUsed(false);
        }
        ShipEntity entity = this.convert(shipTO);
        entity.setRating(countShipRating(shipTO.getSpeed(),shipTO.isUsed(),shipTO.getProdDate()));
        return convert(shipRepository.save(entity));
    }

    public Ship getShip(Long id) {
        Optional<ShipEntity> entity = shipRepository.findById(id);
        if (entity.isPresent()) {
            return convert(entity.get());
        }
        return null;
    }

    public Ship updateShip(Long id, ShipTO shipTO) {
        Optional<ShipEntity> oldShipOptional = shipRepository.findById(id);

        if(!oldShipOptional.isPresent()) {
            return null;
        }
        ShipEntity oldShip = oldShipOptional.get();
        if(shipTO.getName() != null ){
            oldShip.setName(shipTO.getName());
        }
        if(shipTO.getProdDate() != null){
            oldShip.setProdDate(new Date(shipTO.getProdDate()));
        }
        if(shipTO.getCrewSize() != null){
            oldShip.setCrewSize(shipTO.getCrewSize());
        }
        if(shipTO.getSpeed() != null){
            oldShip.setSpeed(shipTO.getSpeed());
        }
        if(shipTO.getPlanet() != null){
            oldShip.setPlanet(shipTO.getPlanet());
        }
        if(shipTO.getShipType() != null){
            oldShip.setShipType(shipTO.getShipType().name());
        }
        if(shipTO.isUsed() != null){
            oldShip.setUsed(shipTO.isUsed());
        }
        Double speed = shipTO.getSpeed() == null ? oldShip.getSpeed() : shipTO.getSpeed();
        Boolean isUsed = shipTO.isUsed() == null ? oldShip.getUsed() : shipTO.isUsed();
        Long prodDate = shipTO.getProdDate() == null ? oldShip.getProdDate().getTime() : shipTO.getProdDate();

        oldShip.setRating(countShipRating(speed, isUsed, prodDate));
        return convert(shipRepository.save(oldShip));
    }

    public void deleteShip(Long id) {
        shipRepository.deleteById(id);
    }

    public Boolean validateShipForCreate(ShipTO shipTO){
        if (shipTO.getCrewSize() == null || shipTO.getProdDate() == null ||
                shipTO.getShipType() == null || shipTO.getSpeed() == null){
            return false;
        }
        if (shipTO.getName() == null || shipTO.getName().length() > 50 || shipTO.getName().isEmpty()){
            return false;
        }
        if (shipTO.getPlanet() == null || shipTO.getPlanet().length() > 50 || shipTO.getPlanet().isEmpty()){
            return false;
        }
        if (shipTO.getSpeed()< SPEED_MIN || shipTO.getSpeed()> SPEED_MAX ){
            return false;
        }
        if (shipTO.getCrewSize()< CREW_MIN || shipTO.getCrewSize()> CREW_MAX ){
            return false;
        }
        if (shipTO.getProdDate()< PROD_DATA_MIN || shipTO.getProdDate()> PROD_DATA_MAX || shipTO.getProdDate() < 0 ){
            return false;
        }

        return true;
    }

    public Boolean validateShipForUpdate(ShipTO shipTO){

       /* if (shipTO.getCrewSize() == null || shipTO.getProdDate() == null ||
                shipTO.getShipType() == null || shipTO.getSpeed() == null){
            return false;
        }*/
        if (shipTO.getName() != null && (shipTO.getName().length() > 50 || shipTO.getName().isEmpty())){
            return false;
        }
        if (shipTO.getPlanet() != null && (shipTO.getPlanet().length() > 50 || shipTO.getPlanet().isEmpty())){
            return false;
        }
        if (shipTO.getSpeed() != null && (shipTO.getSpeed() < SPEED_MIN || shipTO.getSpeed() > SPEED_MAX )){
            return false;
        }
        if (shipTO.getCrewSize() != null &&(shipTO.getCrewSize() < CREW_MIN || shipTO.getCrewSize() > CREW_MAX) ){
            return false;
        }
        if (shipTO.getProdDate() != null && (shipTO.getProdDate() < PROD_DATA_MIN || shipTO.getProdDate() > PROD_DATA_MAX )){
            return false;
        }

        return true;
    }


    private Double countShipRating(Double speed, Boolean isUsed, Long prodDate){
        double rating = 0.0;
        double k = 1.0;
        if(isUsed){
            k = 0.5;
        }

        Calendar prodDateCalendar = Calendar.getInstance();
        prodDateCalendar.setTimeInMillis(prodDate);

        rating = 80 * speed * k / ( CURRENT_YEAR -  prodDateCalendar.get(Calendar.YEAR) + 1 );

        return this.round(BigDecimal.valueOf(rating)).doubleValue();
    }


    private final BigDecimal round(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_EVEN);
    }
}
