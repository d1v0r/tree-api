package com.dgotal.pincode_projekt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dgotal.pincode_projekt.dto.ChildNodeResponse;
import com.dgotal.pincode_projekt.dto.CreateNodeRequest;
import com.dgotal.pincode_projekt.dto.MoveNodeRequest;
import com.dgotal.pincode_projekt.dto.NodeResponse;
import com.dgotal.pincode_projekt.dto.NodeSimpleResponse;
import com.dgotal.pincode_projekt.dto.ReorderNodeRequest;
import com.dgotal.pincode_projekt.entity.LocationNode;
import com.dgotal.pincode_projekt.repository.LocationNodeRepository;

import exception.BadRequestException;
import exception.NotFoundException;

@Service
public class LocationNodeService {

    private final LocationNodeRepository repository;

    private static final Long ROOT_NODE_ID = 1L;
    private static final String NODE_NOT_FOUND = "Node not found: %d";
    private static final String ROOT_NODE_MOVE_ERROR = "Root node cannot be moved";

    public LocationNodeService(LocationNodeRepository repository) {
        this.repository = repository;
    }

    // Dohvat jednog čvora + njegove djece
    public NodeResponse getNode(Long id) {

        @SuppressWarnings("null")
        LocationNode node = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Node not found: " + id));

        List<LocationNode> children = repository
                .findByParentNodeIdOrderBySortOrderAsc(id);

        List<ChildNodeResponse> childResponses = children.stream()
                .map(child -> {
                    ChildNodeResponse dto = new ChildNodeResponse();
                    dto.setId(child.getId());
                    dto.setTitle(child.getTitle());
                    dto.setParentNodeId(child.getParentNodeId());
                    dto.setSortOrder(child.getSortOrder());
                    dto.setHasChildren(
                            repository.existsByParentNodeId(child.getId()));
                    return dto;
                })
                .toList();

        NodeResponse response = new NodeResponse();
        response.setId(node.getId());
        response.setTitle(node.getTitle());
        response.setParentNodeId(node.getParentNodeId());
        response.setChildren(childResponses);

        return response;
    }

    // Kreiranje novog čvora ispod parenta
    @SuppressWarnings("null")
    public NodeSimpleResponse createNode(CreateNodeRequest request) {

        Long parentId = request.getParentNodeId();

        repository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent node not found: " + parentId));

        LocationNode lastChild = repository.findTopByParentNodeIdOrderBySortOrderDesc(parentId);

        int sortOrder = (lastChild == null)
                ? 1
                : lastChild.getSortOrder() + 1;

        LocationNode node = new LocationNode();
        node.setTitle(request.getTitle());
        node.setParentNodeId(parentId);
        node.setSortOrder(sortOrder);

        // return repository.save(node);
        return NodeSimpleResponse.fromEntity(repository.save(node));
    }

    @SuppressWarnings("null")
    public void deleteNode(Long id) {

        if (id == 1L) {
            throw new RuntimeException("Root node cannot be deleted");
        }

        @SuppressWarnings("null")
        LocationNode node = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Node not found: " + id));

        List<LocationNode> children = repository.findByParentNodeIdOrderBySortOrderAsc(id);

        for (LocationNode child : children) {
            deleteNode(child.getId());
        }

        repository.delete(node);
    }

    @SuppressWarnings("null")
    private boolean isDescendant(Long nodeId, Long potentialAncestorId) {
        LocationNode current = repository.findById(potentialAncestorId).orElse(null);
        while (current != null) {
            if (current.getParentNodeId() == null) {
                return false;
            }
            if (current.getParentNodeId().equals(nodeId)) {
                return true;
            }
            current = repository.findById(current.getParentNodeId()).orElse(null);
        }
        return false;
    }

    public NodeSimpleResponse moveNode(Long id, MoveNodeRequest request) {

        if (id.equals(ROOT_NODE_ID)) {
            throw new IllegalArgumentException(ROOT_NODE_MOVE_ERROR);
        }

        LocationNode node = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NODE_NOT_FOUND.formatted(id)));

        Long newParentId = request.getNewParentNodeId();

        if (newParentId == null) {
            throw new IllegalArgumentException("New parent node ID cannot be null");
        }

        if (id.equals(newParentId)) {
            throw new IllegalArgumentException("Cannot move node to itself");
        }

        if (isDescendant(id, newParentId)) {
            throw new IllegalArgumentException("Invalid move: cycle detected");
        }

        LocationNode lastChild = repository.findTopByParentNodeIdOrderBySortOrderDesc(newParentId);

        int newSortOrder = (lastChild == null)
                ? 1
                : lastChild.getSortOrder() + 1;

        node.setParentNodeId(newParentId);
        node.setSortOrder(newSortOrder);

        // return repository.save(node);
        return NodeSimpleResponse.fromEntity(repository.save(node));
    }

    public void reorderNode(Long id, ReorderNodeRequest request) {

        @SuppressWarnings("null")
        LocationNode node = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Node not found: " + id));

        if (!node.getParentNodeId().equals(request.getParentNodeId())) {
            throw new BadRequestException("Node is not child of given parent");
        }

        List<LocationNode> siblings = repository.findByParentNodeIdOrderBySortOrderAsc(request.getParentNodeId());

        siblings.removeIf(n -> n.getId().equals(id));

        int newIndex = request.getNewIndex();
        if (newIndex < 0 || newIndex > siblings.size()) {
            throw new BadRequestException("Invalid index");
        }

        siblings.add(newIndex, node);

        for (int i = 0; i < siblings.size(); i++) {
            siblings.get(i).setSortOrder(i + 1);
        }

        repository.saveAll(siblings);
    }

    public NodeSimpleResponse updateNode(Long id, com.dgotal.pincode_projekt.dto.UpdateNodeRequest request) {

        if (id.equals(ROOT_NODE_ID)) {
            throw new IllegalArgumentException("Cannot update root node title");
        }

        LocationNode node = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Node not found: " + id));

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Title cannot be empty");
        }

        node.setTitle(request.getTitle().trim());
        return NodeSimpleResponse.fromEntity(repository.save(node));
    }

}