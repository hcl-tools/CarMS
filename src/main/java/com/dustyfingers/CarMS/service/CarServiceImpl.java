package com.dustyfingers.CarMS.service;

import com.dustyfingers.CarMS.dao.AuditRepository;
import com.dustyfingers.CarMS.dao.CarRepository;
import com.dustyfingers.CarMS.model.Audit;
import com.dustyfingers.CarMS.model.Car;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class CarServiceImpl {

    @Autowired
    CarRepository carRepository;

    @Autowired
    AuditRepository auditRepository;

    public Car makeCar(Car car) throws Exception {
        try {
            car = carRepository.save(car);
            if (car == null) {
                throw new Exception("car not created");
            }
            // create audit
            String createdBy = "ADMIN";
            LocalDate dateCreated = LocalDate.now();
            boolean deleted = false;
            String description = "created car";
            Audit audit = new Audit(createdBy, dateCreated, deleted, description, car);
            audit = auditRepository.save(audit);
            if (audit == null) {
                throw new Exception("audit not created");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return car;
    }

    public Car findCarById(int id) throws SQLException {
        return carRepository.findById(id)
                .orElseThrow(() -> new SQLException("car not found"));
    }

    public List<Car> findAllCars() {
        Iterable<Car> cars = carRepository.findAll();
        if(cars instanceof List) {
            return (List<Car>) cars;
        }
        ArrayList<Car> list = new ArrayList<>();
        if(cars != null) {
            for(Car c: cars) {
                list.add(c);
            }
        }
        return list;
    }

    public Car updateCar(Car car) throws Exception {
        try {
            // update car
            car = carRepository.save(car);
            if (car == null) {
                throw new Exception("car not updated");
            }
            // create audit
            String createdBy = "ADMIN";
            LocalDate dateCreated = LocalDate.now();
            boolean deleted = false;
            String description = "updated car";
            Audit audit = new Audit(createdBy, dateCreated, deleted, description, car);
            System.out.println(audit.getAudit_id() + " " + audit.getCreated_by());
            audit = auditRepository.save(audit);

            if (audit == null) {
                throw new Exception("car not updated");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return car;
    }

    public boolean deleteCar(int id) throws Exception {
        boolean isDeleted = false;
        try {
            // find car to delete
            Car carToDelete = this.findCarById(id);
            // delete and check isn't there anymore as this JPA method has a void return
            carRepository.delete(carToDelete);
            try {
                carToDelete = this.findCarById(id); // throws exception
            } catch (Exception e) {
                // create audit
                String createdBy = "ADMIN";
                LocalDate dateCreated = LocalDate.now();
                boolean deleted = true;
                String description = "car deleted";
                Audit audit = new Audit(createdBy, dateCreated, deleted, description, new Car(id));
                audit = auditRepository.save(audit);
                if (audit == null) {
                    throw new Exception("car not deleted");
                }
            }
            // create audit
            String createdBy = "ADMIN";
            LocalDate dateCreated = LocalDate.now();
            boolean deleted = false;
            String description = "car deleted";
            Audit audit = new Audit(createdBy, dateCreated, deleted, description, new Car(id));
            audit = auditRepository.save(audit);
            if (audit == null) {
                throw new Exception("car not deleted");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }



        return false;
    }
}
