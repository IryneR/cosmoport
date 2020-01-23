package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipTO;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/rest")
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(value = "/ships", method = RequestMethod.GET)
    public List<Ship> allShips(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String planet,
                               @RequestParam(required = false) ShipType shipType,
                               @RequestParam(required = false) Long after,
                               @RequestParam(required = false) Long before,
                               @RequestParam(required = false) Boolean isUsed,
                               @RequestParam(required = false) Double minSpeed,
                               @RequestParam(required = false) Double maxSpeed,
                               @RequestParam(required = false) Integer minCrewSize,
                               @RequestParam(required = false) Integer maxCrewSize,
                               @RequestParam(required = false) Double minRating,
                               @RequestParam(required = false) Double maxRating,
                               @RequestParam(required = false) ShipOrder order,
                               @RequestParam(required = false) Integer pageNumber,
                               @RequestParam(required = false) Integer pageSize) {

        return shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize,
                                    maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
    }

    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    public Integer allShipsCount(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String planet,
                                 @RequestParam(required = false) ShipType shipType,
                                 @RequestParam(required = false) Long after,
                                 @RequestParam(required = false) Long before,
                                 @RequestParam(required = false) Boolean isUsed,
                                 @RequestParam(required = false) Double minSpeed,
                                 @RequestParam(required = false) Double maxSpeed,
                                 @RequestParam(required = false) Integer minCrewSize,
                                 @RequestParam(required = false) Integer maxCrewSize,
                                 @RequestParam(required = false) Double minRating,
                                 @RequestParam(required = false) Double maxRating) {

        return this.shipService.getShipsCount(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating);
    }

    @RequestMapping(value = "/ships", method = RequestMethod.POST)
    public ResponseEntity<Ship> createShip(@RequestBody ShipTO shipTO) {
        if(shipService.validateShipForCreate(shipTO) ){
            return ResponseEntity.ok(shipService.createShip(shipTO));
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {
        if(id== null || id <= 0 || !this.isIdInteger(id) || !this.isIDNumber(id)){
            return ResponseEntity.badRequest().build();
            }
            Ship obtainedShip = shipService.getShip(id);
            if(obtainedShip == null ){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(obtainedShip);
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.POST)
    public ResponseEntity<Ship> updateShip(@PathVariable("id") Long id, @RequestBody ShipTO shipTO) {
       if(id == null || id <= 0 || !this.isIdInteger(id) || !this.isIDNumber(id)){
            return ResponseEntity.badRequest().build();
        }
        if(!shipService.validateShipForUpdate(shipTO) ){
            return ResponseEntity.badRequest().build();
        }
        Ship updatedShip = shipService.updateShip(id,shipTO);
        if(updatedShip == null ){
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(updatedShip);

    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void>  deleteShip(@PathVariable("id") Long id) {

        if(id == null || id <= 0 || !this.isIdInteger(id) || !this.isIDNumber(id)){
            return ResponseEntity.badRequest().build();
        }
        Ship obtainedShip = shipService.getShip(id);
        if(obtainedShip == null ){
            return ResponseEntity.notFound().build();
        }
        shipService.deleteShip(id);
        return ResponseEntity.ok().build();
    }

    public boolean isIdInteger(Long id) {
        String stringID = id.toString();
        try {
            Integer.parseInt(stringID);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private  boolean isIDNumber(Long id) {
        String stringID = id.toString();
        for (char c: stringID.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
