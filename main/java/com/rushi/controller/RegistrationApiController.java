package com.rushi.controller;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.rushi.model.Registration;
import com.rushi.repository.RegistrationRepository;

@RestController
public class RegistrationApiController {

   
    private static final Logger logger = LoggerFactory.getLogger(RegistrationApiController.class);
   
    @Autowired
    private RegistrationRepository registrationRepository;

    // Get all registrations
    @GetMapping("/api/registrations")
    public List<Registration> getRegistrations() {
        logger.info("Fetching all registrations");
        return registrationRepository.findAll();
    }

    // Get a registration by ID
    @GetMapping("/api/registrations/{id}")
    public Registration getRegistration(@PathVariable Long id) {
        logger.info("Fetching registration with id: {}", id);
        return registrationRepository.findById(id).orElse(null);
    }

    // Create a new registration
    @PostMapping("/api/registrations")
    public Registration createRegistration(@RequestBody Registration registration) {
        logger.info("Creating new registration for: {}", registration.getName());
        return registrationRepository.save(registration);
    }

    // Update an existing registration
    @PutMapping("/api/registrations/{id}")
    public Registration updateRegistration(@PathVariable Long id, @RequestBody Registration registration) {
        logger.info("Updating registration with id: {}", id);
        registration.setId(id);
        return registrationRepository.save(registration);
    }

    // Delete a registration
    @DeleteMapping("/api/registrations/{id}")
    public void deleteRegistration(@PathVariable Long id) {
        logger.info("Deleting registration with id: {}", id);
        registrationRepository.deleteById(id);
    }
    
   
}
