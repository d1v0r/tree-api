package com.dgotal.pincode_projekt.dto;

import com.dgotal.pincode_projekt.entity.LocationNode;

import lombok.Data;

@Data
public class NodeSimpleResponse {

    private Long id;
    private String title;
    private Long parentNodeId;
    private Integer sortOrder;

    public static NodeSimpleResponse fromEntity(LocationNode node) {
        NodeSimpleResponse dto = new NodeSimpleResponse();
        dto.setId(node.getId());
        dto.setTitle(node.getTitle());
        dto.setParentNodeId(node.getParentNodeId());
        dto.setSortOrder(node.getSortOrder());
        return dto;
    }
}
