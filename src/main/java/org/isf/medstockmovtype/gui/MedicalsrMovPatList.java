/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.medstockmovtype.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.menu.manager.Context;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.jobjects.OhDefaultCellRenderer;
import org.isf.utils.jobjects.OhTableDrugsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MedicalsrMovPatList extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicalsrMovPatList.class);

	private Patient myPatient;
	private ArrayList<MovementWard> drugsData;
	private JDialog dialogDrug;
	private JTable JtableData;
	private OhTableDrugsModel<MovementWard> modelMedWard;
	private OhDefaultCellRenderer cellRenderer = new OhDefaultCellRenderer();
	private MovWardBrowserManager movManager = Context.getApplicationContext().getBean(MovWardBrowserManager.class);
	public MedicalsrMovPatList(Object object) {
		
		setLayout(new BorderLayout(0, 0));
		JPanel panelData = new JPanel();
		add(panelData);
		panelData.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPaneData = new JScrollPane();
		panelData.add(scrollPaneData);
	
		if (object instanceof Patient) {
			myPatient = (Patient) object;
		}
		
		if (myPatient != null) {
			MovWardBrowserManager movManager = Context.getApplicationContext().getBean(MovWardBrowserManager.class);
			try {
				ArrayList<MovementWard> movPat = movManager.getMovementToPatient(myPatient);
				drugsData = new ArrayList<>();
				for (MovementWard mov : movPat) {
					drugsData.add(mov);
				}
			} catch (OHServiceException ohServiceException) {
				LOGGER.error(ohServiceException.getMessage(), ohServiceException);
			} 
			
		}
		JtableData = new JTable();
		scrollPaneData.setViewportView(JtableData);
		/* ** apply default oh cellRender **** */
		JtableData.setDefaultRenderer(Object.class, cellRenderer);
		JtableData.setDefaultRenderer(Double.class, cellRenderer);
		
		
		modelMedWard = new OhTableDrugsModel<>(drugsData);

		JtableData.setModel(modelMedWard);
		dialogDrug = new JDialog();
		dialogDrug.setLocationRelativeTo(null);
		dialogDrug.setSize(450, 280);
		dialogDrug.setLocationRelativeTo(null);
		dialogDrug.setModal(true);
	}
	
	public List<MovementWard> getDrugsData() {
		return drugsData;
	}
}
