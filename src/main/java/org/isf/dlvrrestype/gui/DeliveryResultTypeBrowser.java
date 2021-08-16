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
package org.isf.dlvrrestype.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.isf.dlvrrestype.gui.DeliveryResultTypeBrowserEdit.DeliveryResultTypeListener;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.jobjects.MessageDialog;
import org.isf.utils.jobjects.ModalJFrame;

/**
 * Browsing of table DeliveryResultType
 *
 * @author Furlanetto, Zoia, Finotto
 */
public class DeliveryResultTypeBrowser extends ModalJFrame implements DeliveryResultTypeListener{

	private static final long serialVersionUID = 1L;
	private ArrayList<DeliveryResultType> pDeliveryResultType;
	private String[] pColumns = {
			MessageBundle.getMessage("angal.common.code.txt").toUpperCase(),
			MessageBundle.getMessage("angal.common.description.txt").toUpperCase()
	};
	private int[] pColumnWidth = {80, 200};

	private JPanel jContainPanel = null;
	private JPanel jButtonPanel = null;
	private JButton jNewButton = null;
	private JButton jEditButton = null;
	private JButton jCloseButton = null;
	private JButton jDeleteButton = null;
	private JTable jTable = null;
	private DeliveryResultTypeBrowserModel model;
	private int selectedrow;
	private DeliveryResultTypeBrowserManager manager = Context.getApplicationContext().getBean(DeliveryResultTypeBrowserManager.class);
	private DeliveryResultType deliveryresultType = null;
	private final JFrame myFrame;
	
	/**
	 * This method initializes
	 */
	public DeliveryResultTypeBrowser() {
		super();
		myFrame=this;
		initialize();
		setVisible(true);
	}
	
	
	private void initialize() {
		this.setTitle(MessageBundle.getMessage("angal.dlvrrestype.deliveryresulttypebrowser.title"));
		this.setContentPane(getJContainPanel());
		pack();
		setLocationRelativeTo(null);
	}
	
	
	private JPanel getJContainPanel() {
		if (jContainPanel == null) {
			jContainPanel = new JPanel(new BorderLayout());
			jContainPanel.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContainPanel.add(new JScrollPane(getJTable()), java.awt.BorderLayout.CENTER);
			validate();
		}
		return jContainPanel;
	}
	
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.add(getJNewButton(), null);
			jButtonPanel.add(getJEditButton(), null);
			jButtonPanel.add(getJDeleteButton(), null);
			jButtonPanel.add(getJCloseButton(), null);
		}
		return jButtonPanel;
	}
	
	
	private JButton getJNewButton() {
		if (jNewButton == null) {
			jNewButton = new JButton(MessageBundle.getMessage("angal.common.new.btn"));
			jNewButton.setMnemonic(MessageBundle.getMnemonic("angal.common.new.btn.key"));
			jNewButton.addActionListener(event -> {
				deliveryresultType = new DeliveryResultType("","");
				DeliveryResultTypeBrowserEdit newrecord = new DeliveryResultTypeBrowserEdit(myFrame,deliveryresultType, true);
				newrecord.addDeliveryResultTypeListener(DeliveryResultTypeBrowser.this);
				newrecord.setVisible(true);
			});
		}
		return jNewButton;
	}
	
	/**
	 * This method initializes jEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJEditButton() {
		if (jEditButton == null) {
			jEditButton = new JButton(MessageBundle.getMessage("angal.common.edit.btn"));
			jEditButton.setMnemonic(MessageBundle.getMnemonic("angal.common.edit.btn.key"));
			jEditButton.addActionListener(event -> {
				if (jTable.getSelectedRow() < 0) {
					MessageDialog.error(null, "angal.common.pleaseselectarow.msg");
				} else {
					selectedrow = jTable.getSelectedRow();
					deliveryresultType = (DeliveryResultType) (model.getValueAt(selectedrow, -1));
					DeliveryResultTypeBrowserEdit newrecord = new DeliveryResultTypeBrowserEdit(myFrame,deliveryresultType, false);
					newrecord.addDeliveryResultTypeListener(DeliveryResultTypeBrowser.this);
					newrecord.setVisible(true);
				}
			});
		}
		return jEditButton;
	}
	
	/**
	 * This method initializes jCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton(MessageBundle.getMessage("angal.common.close.btn"));
			jCloseButton.setMnemonic(MessageBundle.getMnemonic("angal.common.close.btn.key"));
			jCloseButton.addActionListener(arg0 -> dispose());
		}
		return jCloseButton;
	}
	
	/**
	 * This method initializes jDeleteButton
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJDeleteButton() {
		if (jDeleteButton == null) {
			jDeleteButton = new JButton(MessageBundle.getMessage("angal.common.delete.btn"));
			jDeleteButton.setMnemonic(MessageBundle.getMnemonic("angal.common.delete.btn.key"));
			jDeleteButton.addActionListener(event -> {
				if (jTable.getSelectedRow() < 0) {
					MessageDialog.error(null, "angal.common.pleaseselectarow.msg");
				} else {
					DeliveryResultType resultType = (DeliveryResultType) (model.getValueAt(jTable.getSelectedRow(), -1));
					int answer = MessageDialog.yesNo(null, "angal.dlvrrestype.deletedeliveryresulttype.fmt.msg",resultType.getDescription());
					try {
						if ((answer == JOptionPane.YES_OPTION) && (manager.deleteDeliveryResultType(resultType))) {
							pDeliveryResultType.remove(jTable.getSelectedRow());
							model.fireTableDataChanged();
							jTable.updateUI();
						}
					} catch(OHServiceException ohServiceException) {
						MessageDialog.showExceptions(ohServiceException);
					}
				}
			});
		}
		return jDeleteButton;
	}
	
	public JTable getJTable() {
		if (jTable == null) {
			model = new DeliveryResultTypeBrowserModel();
			jTable = new JTable(model);
			jTable.getColumnModel().getColumn(0).setMinWidth(pColumnWidth[0]);
			jTable.getColumnModel().getColumn(1).setMinWidth(pColumnWidth[1]);
		}
		return jTable;
	}

	
class DeliveryResultTypeBrowserModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	private DeliveryResultTypeBrowserManager manager = Context.getApplicationContext().getBean(DeliveryResultTypeBrowserManager.class);

		public DeliveryResultTypeBrowserModel() {
			try {
				pDeliveryResultType = manager.getDeliveryResultType();
			} catch(OHServiceException ohServiceException) {
				MessageDialog.showExceptions(ohServiceException);
			}
		}
		
		@Override
		public int getRowCount() {
			if (pDeliveryResultType == null) {
				return 0;
			}
			return pDeliveryResultType.size();
		}
		
		@Override
		public String getColumnName(int c) {
			return pColumns[c];
		}

		@Override
		public int getColumnCount() {
			return pColumns.length;
		}

		@Override
		public Object getValueAt(int r, int c) {
			DeliveryResultType deliveryResultType = pDeliveryResultType.get(r);
			if (c == 0) {
				return deliveryResultType.getCode();
			} else if (c == -1) {
				return deliveryResultType;
			} else if (c == 1) {
				return deliveryResultType.getDescription();
			}
			return null;
		}
		
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	@Override
	public void deliveryresultTypeUpdated(AWTEvent e) {
		pDeliveryResultType.set(selectedrow, deliveryresultType);
		((DeliveryResultTypeBrowserModel) jTable.getModel()).fireTableDataChanged();
		jTable.updateUI();
		if ((jTable.getRowCount() > 0) && selectedrow > -1) {
			jTable.setRowSelectionInterval(selectedrow, selectedrow);
		}
	}
	
	
	@Override
	public void deliveryresultTypeInserted(AWTEvent e) {
		pDeliveryResultType.add(0, deliveryresultType);
		((DeliveryResultTypeBrowserModel) jTable.getModel()).fireTableDataChanged();
		if (jTable.getRowCount() > 0) {
			jTable.setRowSelectionInterval(0, 0);
		}
	}
	
}
