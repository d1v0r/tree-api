package com.dgotal.pincode_projekt.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dgotal.pincode_projekt.entity.LocationNode;

public interface LocationNodeRepository
        extends JpaRepository<LocationNode, Long> {

    List<LocationNode> findByParentNodeIdOrderBySortOrderAsc(Long parentNodeId);

    boolean existsByParentNodeId(Long parentNodeId);

    LocationNode findTopByParentNodeIdOrderBySortOrderDesc(Long parentNodeId);
}
