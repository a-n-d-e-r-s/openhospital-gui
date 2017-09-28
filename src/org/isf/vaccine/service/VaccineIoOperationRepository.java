package org.isf.vaccine.service;

import java.util.List;

import org.isf.vaccine.model.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface VaccineIoOperationRepository extends JpaRepository<Vaccine, String> {
    public List<Vaccine> findAllOrderByDescriptionAsc();    

    @Query(value = "SELECT * FROM VACCINE JOIN VACCINETYPE ON VAC_VACT_ID_A = VACT_ID_A WHERE VAC_VACT_ID_A = :id ORDER BY VAC_DESC", nativeQuery= true)
    public List<Vaccine> findAllWhereIdByOrderDescriptionAsc(@Param("id") String id);
}