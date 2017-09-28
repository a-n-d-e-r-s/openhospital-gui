package org.isf.disctype.service;

import java.util.List;

import org.isf.disctype.model.DischargeType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DischargeTypeIoOperationRepository extends JpaRepository<DischargeType, String> {
    public List<DischargeType> findAllOrderByDescriptionAsc();
}