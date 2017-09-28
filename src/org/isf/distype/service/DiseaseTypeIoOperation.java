package org.isf.distype.service;

import java.util.ArrayList;

import org.isf.distype.model.DiseaseType;
import org.isf.utils.exception.OHException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the DisType module.
 */
@Component
@Transactional
public class DiseaseTypeIoOperation {

	@Autowired
	private DiseaseTypeIoOperationRepository repository;
	
	/**
	 * Returns all the stored {@link DiseaseType}s.
	 * @return a list of disease type.
	 * @throws OHException if an error occurs retrieving the diseases list.
	 */
	public ArrayList<DiseaseType> getDiseaseTypes() throws OHException 
	{
		return new ArrayList<DiseaseType>(repository.findAllOrderByDescriptionAsc());
	}

	/**
	 * Updates the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to update.
	 * @return <code>true</code> if the disease type has been updated, false otherwise.
	 * @throws OHException if an error occurs during the update operation.
	 */
	public boolean updateDiseaseType(
			DiseaseType diseaseType) throws OHException 
	{
		boolean result = true;
	

		DiseaseType savedDiseaseType = repository.save(diseaseType);
		result = (savedDiseaseType != null);
		
		return result;
	}

	/**
	 * Store the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to store.
	 * @return <code>true</code> if the {@link DiseaseType} has been stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the store operation.
	 */
	public boolean newDiseaseType(
			DiseaseType diseaseType) throws OHException 
	{
		boolean result = true;
	
		
		DiseaseType savedDiseaseType = repository.save(diseaseType);
		result = (savedDiseaseType != null);
		
		return result;
	}

	/**
	 * Deletes the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to remove.
	 * @return <code>true</code> if the disease has been removed, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the delete procedure.
	 */
	public boolean deleteDiseaseType(
			DiseaseType diseaseType) throws OHException 
	{
		boolean result = true;
	
		
		repository.delete(diseaseType);
		
		return result;
	}

	/**
	 * Checks if the specified code is already used by any {@link DiseaseType}.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, false otherwise.
	 * @throws OHException if an error occurs during the check.
	 */
	public boolean isCodePresent(
			String code) throws OHException
	{
		boolean result = true;
	
		
		result = repository.exists(code);
		
		return result;
	}
}
