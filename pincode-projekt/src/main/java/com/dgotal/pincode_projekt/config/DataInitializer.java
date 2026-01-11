package com.dgotal.pincode_projekt.config;

import com.dgotal.pincode_projekt.entity.LocationNode;
import com.dgotal.pincode_projekt.repository.LocationNodeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LocationNodeRepository repository;

    public DataInitializer(LocationNodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {

        if (!repository.existsById(1L)) {
            LocationNode root = new LocationNode();
            root.setId(1L);
            root.setTitle("Root");
            root.setParentNodeId(null);
            root.setSortOrder(1);

            repository.save(root);
        }
    }
}
