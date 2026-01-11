package com.dgotal.pincode_projekt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dgotal.pincode_projekt.dto.CreateNodeRequest;
import com.dgotal.pincode_projekt.dto.MoveNodeRequest;
import com.dgotal.pincode_projekt.dto.NodeResponse;
import com.dgotal.pincode_projekt.dto.NodeSimpleResponse;
import com.dgotal.pincode_projekt.dto.ReorderNodeRequest;
import com.dgotal.pincode_projekt.dto.UpdateNodeRequest;
import com.dgotal.pincode_projekt.service.LocationNodeService;

@RestController
@RequestMapping("/nodes")
public class LocationNodeController {

    private final LocationNodeService service;

    public LocationNodeController(LocationNodeService service) {
        this.service = service;
    }

    @GetMapping
    public NodeResponse getRoot() {
        return service.getNode(1L);
    }

    @GetMapping("/{id}")
    public NodeResponse getNode(@PathVariable Long id) {
        return service.getNode(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteNode(id);
    }

    @PostMapping
    public NodeSimpleResponse create(@RequestBody CreateNodeRequest request) {
        return service.createNode(request);
    }

    @PutMapping("/{id}")
    public NodeSimpleResponse update(@PathVariable Long id,
            @RequestBody UpdateNodeRequest request) {
        return service.updateNode(id, request);
    }

    @PostMapping("/{id}/move")
    public NodeSimpleResponse move(@PathVariable Long id,
            @RequestBody MoveNodeRequest request) {
        return service.moveNode(id, request);
    }

    @PostMapping("/{id}/reorder")
    public ResponseEntity<Void> reorder(@PathVariable Long id,
            @RequestBody ReorderNodeRequest request) {
        service.reorderNode(id, request);
        return ResponseEntity.noContent().build();
    }

}
