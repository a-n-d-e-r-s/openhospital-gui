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
package org.isf.medicalstock.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medstockmovtype.manager.MedicaldsrstockmovTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.Context;
import org.isf.stat.gui.report.GenericReportPharmaceuticalStockCard;
import org.isf.supplier.manager.SupplierBrowserManager;
import org.isf.supplier.model.Supplier;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.isf.utils.jobjects.DateTextField;
import org.isf.utils.jobjects.MessageDialog;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.StockCardDialog;
import org.isf.utils.jobjects.StockLedgerDialog;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ------------------------------------------
 * MovStockBrowser - list medicals movement. let the user search for movements
 * 					  and insert a new movements
 * -----------------------------------------
 * modification history
 * 30/03/2006 - Theo - first beta version
 * 03/11/2006 - ross - changed title, removed delete all button
 *                   - corrected an error in datetextfield class (the month displayed in the filter was -1
 * 			         - version is now  1.0
 * ------------------------------------------
 */
public class MovStockBrowser extends ModalJFrame {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(MovStockBrowser.class);

	private final JFrame myFrame;
	private JPanel contentPane;
	private JPanel buttonPanel;
	private JPanel tablePanel;
	private JButton closeButton;
	private JButton chargeButton;
	private JButton dischargeButton;
	private JButton filterButton;
	private JButton exportToExcel;
	private JButton stockCardButton;
	private JButton stockLedgerButton;
	private JPanel filterPanel;
	private JCheckBox jCheckBoxKeepFilter;
	private JComboBox medicalBox;
	private JComboBox medicalTypeBox;
	private JComboBox typeBox;
	private JComboBox wardBox;
	private DateTextField movDateFrom;
	private DateTextField movDateTo;
	private DateTextField lotPrepFrom;
	private DateTextField lotPrepTo;
	private DateTextField lotDueFrom;
	private DateTextField lotDueTo;
	private JTable movTable;
	private JTable jTableTotal;
	private int totalQti;
	private BigDecimal totalAmount;
	private MovBrowserModel model;
	private ArrayList<Movement> moves;
	private String[] pColumns = {
			MessageBundle.getMessage("angal.medicalstock.refno.col").toUpperCase(),
			MessageBundle.getMessage("angal.common.date.txt").toUpperCase(),        //1
			MessageBundle.getMessage("angal.common.type.txt").toUpperCase(),       //2
			MessageBundle.getMessage("angal.common.ward.txt").toUpperCase(),            //3
			MessageBundle.getMessage("angal.common.qty.txt").toUpperCase(),            //4
			MessageBundle.getMessage("angal.medicalstock.pharmaceutical.col").toUpperCase(),    //5
			MessageBundle.getMessage("angal.medicalstock.medtype.col").toUpperCase(),        //6
			MessageBundle.getMessage("angal.medicalstock.lot.col").toUpperCase(),            //7
			MessageBundle.getMessage("angal.medicalstock.prepdate.col").toUpperCase(),        //8
			MessageBundle.getMessage("angal.medicalstock.duedate.col").toUpperCase(),        //9
			MessageBundle.getMessage("angal.medicalstock.origin.col").toUpperCase(),            //10
			MessageBundle.getMessage("angal.medicalstock.cost.col").toUpperCase(),            //11
			MessageBundle.getMessage("angal.common.total.txt").toUpperCase()            //12
	};
	private boolean[] pColumnBold = { true, false, false, false, false, false, false, false, false, false, false, false, false };
	private int[] columnAlignment = { SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER,
			SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.CENTER,
			SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.RIGHT, SwingConstants.RIGHT };
	private boolean[] pColumnVisible = { true, true, true, true, true, true, true, !GeneralData.AUTOMATICLOT_IN, !GeneralData.AUTOMATICLOT_IN, true, true,
			GeneralData.LOTWITHCOST, GeneralData.LOTWITHCOST, true };

	private int[] pColumnWidth = { 50, 80, 45, 130, 50, 150, 70, 70, 80, 65, 50, 50, 70 };
	private static final String DATE_FORMAT_DD_MM_YY = "dd/MM/yy";
	private static final String DATE_FORMAT_DD_MM_YY_HH_MM = "dd/MM/yy HH:mm";

	private String currencyCod;

	/*
	 * Adds to facilitate the selection of products
	 */
	private JPanel searchPanel;
	private JTextField searchTextField;
	private JButton searchButton;

	private HashMap<Integer, String> supMap = new HashMap<>();

	private MedicalBrowsingManager medicalManager = Context.getApplicationContext().getBean(MedicalBrowsingManager.class);
	private MedicalTypeBrowserManager medicalTypeBrowserManager = Context.getApplicationContext().getBean(MedicalTypeBrowserManager.class);
	private MedicaldsrstockmovTypeBrowserManager medicaldsrstockmovTypeBrowserManager = Context.getApplicationContext()
			.getBean(MedicaldsrstockmovTypeBrowserManager.class);
	private MovBrowserManager movBrowserManager = Context.getApplicationContext().getBean(MovBrowserManager.class);
	private HospitalBrowsingManager hospitalManager = Context.getApplicationContext().getBean(HospitalBrowsingManager.class);
	private SupplierBrowserManager supplierBrowserManager = Context.getApplicationContext().getBean(SupplierBrowserManager.class);

	public MovStockBrowser() {
		myFrame = this;
		setTitle(MessageBundle.getMessage("angal.medicalstock.stockmovementbrowser.title"));
		try {
			supMap = supplierBrowserManager.getHashMap(true);
		} catch (OHServiceException e) {
			OHServiceExceptionUtil.showMessages(e);
		}

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		final int pfrmBase = 30;
		final int pfrmWidth = 24;
		final int pfrmHeight = 22;
		this.setBounds((screensize.width - screensize.width * pfrmWidth
				/ pfrmBase) / 2, (screensize.height - screensize.height
				* pfrmHeight / pfrmBase) / 2, screensize.width * pfrmWidth
				/ pfrmBase, screensize.height * pfrmHeight / pfrmBase);
		setContentPane(getContentpane());

		//setResizable(false);
		updateTotals();
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private JPanel getContentpane() {
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(getFilterPanel(), BorderLayout.WEST);
		contentPane.add(getTablesPanel(), BorderLayout.CENTER);
		contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
		return contentPane;
	}

	/**
	 * This method controls if the automaticlot option is on
	 *
	 * @return
	 */
	private boolean isAutomaticLot() {
		return GeneralData.AUTOMATICLOT_IN;
	}

	private JPanel getButtonPanel() {
		buttonPanel = new JPanel();
		if (MainMenu.checkUserGrants("btnpharmstockcharge"))
			buttonPanel.add(getChargeButton());
		if (MainMenu.checkUserGrants("btnpharmstockdischarge"))
			buttonPanel.add(getDischargeButton());
		buttonPanel.add(getExportToExcelButton());
		buttonPanel.add(getStockCardButton());
		buttonPanel.add(getStockLedgerButton());
		buttonPanel.add(getCloseButton());
		return buttonPanel;
	}

	private JButton getStockCardButton() {
		stockCardButton = new JButton(MessageBundle.getMessage("angal.common.stockcard.btn"));
		stockCardButton.setMnemonic(MessageBundle.getMnemonic("angal.common.stockcard.btn.key"));
		stockCardButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Medical medical = null;
				if (movTable.getSelectedRow() > -1) {
					Movement movement = (Movement) (model.getValueAt(movTable.getSelectedRow(), -1));
					medical = movement.getMedical();
				}

				StockCardDialog stockCardDialog = new StockCardDialog(MovStockBrowser.this,
						medical,
						movDateFrom.getCompleteDate().getTime(),
						movDateTo.getCompleteDate().getTime());
				medical = stockCardDialog.getMedical();
				Date dateFrom = stockCardDialog.getDateFrom();
				Date dateTo = stockCardDialog.getDateTo();
				boolean toExcel = stockCardDialog.isExcel();

				if (!stockCardDialog.isCancel()) {
					if (medical == null) {
						MessageDialog.error(MovStockBrowser.this, "angal.medicalstock.chooseamedical.msg");
						return;
					}
					new GenericReportPharmaceuticalStockCard("ProductLedger", dateFrom, dateTo, medical, null, toExcel);
				}
			}
		});
		return stockCardButton;
	}

	private JButton getStockLedgerButton() {
		stockLedgerButton = new JButton(MessageBundle.getMessage("angal.common.stockledger.btn"));
		stockLedgerButton.setMnemonic(MessageBundle.getMnemonic("angal.common.stockledger.btn.key"));
		stockLedgerButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				StockLedgerDialog stockCardDialog = new StockLedgerDialog(MovStockBrowser.this, movDateFrom.getCompleteDate().getTime(),
						movDateTo.getCompleteDate().getTime());
				Date dateFrom = stockCardDialog.getDateFrom();
				Date dateTo = stockCardDialog.getDateTo();

				if (!stockCardDialog.isCancel()) {
					new GenericReportPharmaceuticalStockCard("ProductLedger_multi", dateFrom, dateTo, null, null, false);
				}
			}
		});
		return stockLedgerButton;
	}

	private JPanel getTablesPanel() {
		tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(getTable(), BorderLayout.CENTER);
		tablePanel.add(getTableTotal(), BorderLayout.SOUTH);
		return tablePanel;
	}

	private JScrollPane getTable() {
		JScrollPane scrollPane = new JScrollPane(getMovTable());
		int totWidth = 0;
		for (int colWidth : pColumnWidth) {
			totWidth += colWidth;
		}
		scrollPane.setPreferredSize(new Dimension(totWidth, 450));
		return scrollPane;
	}

	private JScrollPane getTableTotal() {
		JScrollPane scrollPane = new JScrollPane(getJTableTotal());
		int totWidth = 0;
		for (int colWidth : pColumnWidth) {
			totWidth += colWidth;
		}
		scrollPane.setPreferredSize(new Dimension(totWidth, 20));
		scrollPane.setColumnHeaderView(null);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		return scrollPane;
	}

	public void updateTotals() {
		if (jTableTotal == null)
			return;
		totalQti = 0;
		totalAmount = new BigDecimal(0);

		// quantity
		if (!medicalBox.getSelectedItem().equals(MessageBundle.getMessage("angal.common.all.txt"))) {
			for (Movement mov : moves) {
				if (mov.getType().getType().contains("+")) {
					totalQti += mov.getQuantity();
				} else {
					totalQti -= mov.getQuantity();
				}
			}
			jTableTotal.getModel().setValueAt(totalQti, 0, 4);
		} else {
			jTableTotal.getModel().setValueAt(MessageBundle.getMessage("angal.common.notapplicable.txt"), 0, 4);
		}

		// amount
		for (Movement mov : moves) {
			BigDecimal itemAmount = new BigDecimal(mov.getQuantity());
			if (GeneralData.LOTWITHCOST && mov.getLot().getCost() != null) {
				if (mov.getType().getType().contains("+")) {
					totalAmount = totalAmount.add(itemAmount.multiply(mov.getLot().getCost()));
				} else {
					totalQti -= mov.getQuantity();
					totalAmount = totalAmount.subtract(itemAmount.multiply(mov.getLot().getCost()));
				}
			}
		}
		jTableTotal.getModel().setValueAt(totalAmount, 0, 12);
	}

	private JPanel getFilterPanel() {
		if (filterPanel == null) {
			filterPanel = new JPanel();
			filterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.medicalstock.selectionpanel")));
			filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
			filterPanel.add(getMedicalPanel());
			filterPanel.add(getMovementPanel());
			if (!isAutomaticLot()) {
				filterPanel.add(getLotPreparationDatePanel());
			}
			filterPanel.add(getLotDueDatePanel());
			JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			filterButtonPanel.add(getFilterButton());
			filterButtonPanel.add(getJCheckBoxKeepFilter());
			filterPanel.add(filterButtonPanel);
		}
		return filterPanel;
	}

	private JCheckBox getJCheckBoxKeepFilter() {
		if (jCheckBoxKeepFilter == null) {
			jCheckBoxKeepFilter = new JCheckBox(MessageBundle.getMessage("angal.medicalstock.keep"));
		}
		return jCheckBoxKeepFilter;
	}

	private JPanel getMedicalPanel() {
		JPanel medicalPanel = new JPanel();
		medicalPanel.setLayout(new BoxLayout(medicalPanel, BoxLayout.Y_AXIS));
		medicalPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.medicalstock.pharmaceutical")));
		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel(MessageBundle.getMessage("angal.common.description.txt")));
		medicalPanel.add(label1Panel);
		medicalPanel.add(getMedicalSearchPanel());
		JPanel medicalDescPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		medicalDescPanel.add(getMedicalBox());
		medicalPanel.add(medicalDescPanel);
		JPanel label2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label2Panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.type")));
		medicalPanel.add(label2Panel);
		JPanel medicalTypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		medicalTypePanel.add(getMedicalTypeBox());
		medicalPanel.add(medicalTypePanel);
		return medicalPanel;
	}

	private JPanel getMovementPanel() {
		JPanel movementPanel = new JPanel();
		movementPanel.setLayout(new BoxLayout(movementPanel, BoxLayout.Y_AXIS));
		movementPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.medicalstock.movement")));
		JPanel label3Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label3Panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.type")));
		movementPanel.add(label3Panel);
		JPanel movementTypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		movementTypePanel.add(getMovementTypeBox());
		movementPanel.add(movementTypePanel);

		JPanel label2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label2Panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.ward")));
		movementPanel.add(label2Panel);
		JPanel wardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		wardPanel.add(getWardBox());
		movementPanel.add(wardPanel);

		JPanel label4Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label4Panel.add(new JLabel(MessageBundle.getMessage("angal.common.date.txt")));
		movementPanel.add(label4Panel);

		JPanel moveFromPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(MessageBundle.getMessage("angal.common.from.txt"));
		label.setVerticalAlignment(SwingConstants.TOP);
		moveFromPanel.add(label, BorderLayout.WEST);
		moveFromPanel.add(getMovDateFrom(), BorderLayout.EAST);
		movementPanel.add(moveFromPanel);
		JPanel moveToPanel = new JPanel(new BorderLayout());
		JLabel label_1 = new JLabel(MessageBundle.getMessage("angal.common.to.txt"));
		label_1.setVerticalAlignment(SwingConstants.TOP);
		moveToPanel.add(label_1, BorderLayout.WEST);
		moveToPanel.add(getMovDateTo(), BorderLayout.EAST);
		movementPanel.add(moveToPanel);
		return movementPanel;
	}

	private JPanel getLotPreparationDatePanel() {
		JPanel lotPreparationDatePanel = new JPanel();
		lotPreparationDatePanel.setLayout(new BoxLayout(
				lotPreparationDatePanel, BoxLayout.Y_AXIS));
		lotPreparationDatePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.GRAY),
				MessageBundle.getMessage("angal.medicalstock.lotpreparationdate")));

		JPanel lotPrepFromPanel = new JPanel(new BorderLayout());
		lotPrepFromPanel.add(new JLabel(MessageBundle.getMessage("angal.common.from.txt")), BorderLayout.WEST);
		lotPrepFromPanel.add(getLotPrepFrom(), BorderLayout.EAST);
		lotPreparationDatePanel.add(lotPrepFromPanel);
		JPanel lotPrepToPanel = new JPanel(new BorderLayout());
		lotPrepToPanel.add(new JLabel(MessageBundle.getMessage("angal.common.to.txt")), BorderLayout.WEST);
		lotPrepToPanel.add(getLotPrepTo(), BorderLayout.EAST);
		lotPreparationDatePanel.add(lotPrepToPanel);

		return lotPreparationDatePanel;
	}

	private JPanel getLotDueDatePanel() {
		JPanel lotDueDatePanel = new JPanel();
		lotDueDatePanel.setLayout(new BoxLayout(lotDueDatePanel,
				BoxLayout.Y_AXIS));
		lotDueDatePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.medicalstock.lotduedate")));

		JPanel lotDueFromPanel = new JPanel(new BorderLayout());
		lotDueFromPanel.add(new JLabel(MessageBundle.getMessage("angal.common.from.txt")), BorderLayout.WEST);
		lotDueFromPanel.add(getLotDueFrom(), BorderLayout.EAST);
		lotDueDatePanel.add(lotDueFromPanel);
		JPanel lotDueToPanel = new JPanel(new BorderLayout());
		lotDueToPanel.add(new JLabel(MessageBundle.getMessage("angal.common.to.txt")), BorderLayout.WEST);
		lotDueToPanel.add(getLotDueTo(), BorderLayout.EAST);
		lotDueDatePanel.add(lotDueToPanel);

		return lotDueDatePanel;
	}

	private JComboBox getWardBox() {
		WardBrowserManager wbm = Context.getApplicationContext().getBean(WardBrowserManager.class);
		wardBox = new JComboBox();
		wardBox.setPreferredSize(new Dimension(130, 25));
		wardBox.addItem(MessageBundle.getMessage("angal.common.all.txt"));
		ArrayList<Ward> wardList;
		try {
			wardList = wbm.getWards();
		} catch (OHServiceException e) {
			wardList = new ArrayList<>();
			OHServiceExceptionUtil.showMessages(e);
		}
		for (org.isf.ward.model.Ward elem : wardList) {
			wardBox.addItem(elem);
		}
		wardBox.setEnabled(false);
		return wardBox;
	}

	private JPanel getMedicalSearchPanel() {
		searchButton = new JButton();
		searchButton.setPreferredSize(new Dimension(20, 20));
		searchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				medicalBox.removeAllItems();
				ArrayList<Medical> medicals;
				try {
					medicals = medicalManager.getMedicals();
				} catch (OHServiceException e1) {
					medicals = null;
					OHServiceExceptionUtil.showMessages(e1);
				}
				if (null != medicals) {
					ArrayList<Medical> results = getSearchMedicalsResults(searchTextField.getText(), medicals);
					int originalSize = medicals.size();
					int resultsSize = results.size();
					if (originalSize == resultsSize) {
						medicalBox.addItem(MessageBundle.getMessage("angal.common.all.txt"));
					}
					for (Medical aMedical : results) {
						medicalBox.addItem(aMedical);
					}
				}
			}
		});

		searchTextField = new JTextField(10);
		searchTextField.setToolTipText(MessageBundle.getMessage("angal.medicalstock.pharmaceutical"));
		searchTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					searchButton.doClick();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		searchPanel.add(searchTextField);
		searchPanel.add(searchButton);
		searchPanel.setMaximumSize(new Dimension(150, 25));
		searchPanel.setMinimumSize(new Dimension(150, 25));
		searchPanel.setPreferredSize(new Dimension(150, 25));
		return searchPanel;
	}

	private JComboBox getMedicalBox() {
		medicalBox = new JComboBox();
		medicalBox.setMaximumSize(new Dimension(150, 25));
		medicalBox.setMinimumSize(new Dimension(150, 25));
		medicalBox.setPreferredSize(new Dimension(150, 25));
		ArrayList<Medical> medical;
		try {
			medical = medicalManager.getMedicals();
		} catch (OHServiceException e1) {
			medical = null;
			OHServiceExceptionUtil.showMessages(e1);
		}
		medicalBox.addItem(MessageBundle.getMessage("angal.common.all.txt"));
		if (null != medical) {
			for (Medical aMedical : medical) {
				medicalBox.addItem(aMedical);
			}
		}
		medicalBox.addMouseListener(new MouseListener() {

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
				medicalBox.setEnabled(true);
				medicalTypeBox.setSelectedIndex(0);
				medicalTypeBox.setEnabled(false);
			}
		});
		return medicalBox;

	}

	private JComboBox getMedicalTypeBox() {
		medicalTypeBox = new JComboBox();
		medicalTypeBox.setPreferredSize(new Dimension(130, 25));
		ArrayList<MedicalType> medical;

		medicalTypeBox.addItem(MessageBundle.getMessage("angal.common.all.txt"));

		try {
			medical = medicalTypeBrowserManager.getMedicalType();

			for (MedicalType aMedicalType : medical) {
				medicalTypeBox.addItem(aMedicalType);
			}
		} catch (OHServiceException e1) {
			OHServiceExceptionUtil.showMessages(e1);
		}

		medicalTypeBox.addMouseListener(new MouseListener() {

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
				medicalTypeBox.setEnabled(true);
				medicalBox.setSelectedIndex(0);
				medicalBox.setEnabled(false);
			}
		});
		medicalTypeBox.setEnabled(false);
		return medicalTypeBox;
	}

	private JComboBox getMovementTypeBox() {
		typeBox = new JComboBox();
		typeBox.setPreferredSize(new Dimension(130, 25));
		ArrayList<MovementType> type;
		try {
			type = medicaldsrstockmovTypeBrowserManager.getMedicaldsrstockmovType();
		} catch (OHServiceException e1) {
			type = null;
			OHServiceExceptionUtil.showMessages(e1);
		}
		typeBox.addItem(MessageBundle.getMessage("angal.common.all.txt"));
		if (null != type) {
			for (MovementType movementType : type) {
				typeBox.addItem(movementType);
			}
		}
		typeBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!(typeBox.getSelectedItem() instanceof String)) {
					MovementType selected = (MovementType) typeBox
							.getSelectedItem();
					if (selected.getType().contains("-")) {
						wardBox.setEnabled(true);
					} else {
						wardBox.setSelectedIndex(0);
						wardBox.setEnabled(false);
					}
				} else {
					wardBox.setSelectedIndex(0);
					wardBox.setEnabled(false);
				}
			}
		});
		return typeBox;
	}

	private JTable getMovTable() {
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar old = new GregorianCalendar();
		old.add(GregorianCalendar.WEEK_OF_YEAR, -1);

		model = new MovBrowserModel(null, null, null, null, old, now, null, null, null, null);
		movTable = new JTable(model);

		for (int i = 0; i < pColumns.length; i++) {
			movTable.getColumnModel().getColumn(i).setCellRenderer(new EnabledTableCellRenderer());
			movTable.getColumnModel().getColumn(i).setPreferredWidth(pColumnWidth[i]);
			//			if (!pColumnResizable[i]) {
			//				movTable.getColumnModel().getColumn(i).setResizable(pColumnResizable[i]);
			//				movTable.getColumnModel().getColumn(i).setMaxWidth(pColumnWidth[i]);
			//			}
			if (!pColumnVisible[i]) {
				movTable.getColumnModel().getColumn(i).setMinWidth(0);
				movTable.getColumnModel().getColumn(i).setMaxWidth(0);
				movTable.getColumnModel().getColumn(i).setWidth(0);
			}
		}

		TableColumn costColumn = movTable.getColumnModel().getColumn(11);
		costColumn.setCellRenderer(new DecimalFormatRenderer());

		TableColumn totalColumn = movTable.getColumnModel().getColumn(12);
		totalColumn.setCellRenderer(new DecimalFormatRenderer());

		return movTable;
	}

	private JTable getJTableTotal() {
		if (jTableTotal == null) {
			jTableTotal = new JTable();

			String currencyCod;
			try {
				currencyCod = hospitalManager.getHospitalCurrencyCod();
			} catch (OHServiceException e) {
				currencyCod = null;
				OHServiceExceptionUtil.showMessages(e);
			}

			jTableTotal.setModel(new DefaultTableModel(
					new Object[][] {
							{ "", "", "", "<html><b>Total Qty: </b></html>", totalQti, "", "", "", "", "", "<html><b>"
									+ MessageBundle.getMessage("angal.common.total.txt") + ": </b></html>", currencyCod,
									totalAmount }
					}, new String[pColumns.length]) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTableTotal.setTableHeader(null);
			jTableTotal.setShowVerticalLines(false);
			jTableTotal.setShowHorizontalLines(false);
			jTableTotal.setRowSelectionAllowed(false);
			jTableTotal.setCellSelectionEnabled(false);
			jTableTotal.setColumnSelectionAllowed(false);

			for (int i = 0; i < pColumns.length; i++) {
				jTableTotal.getColumnModel().getColumn(i).setCellRenderer(new EnabledTableCellRenderer());
				jTableTotal.getColumnModel().getColumn(i).setPreferredWidth(pColumnWidth[i]);
				if (!pColumnVisible[i]) {
					jTableTotal.getColumnModel().getColumn(i).setMinWidth(0);
					jTableTotal.getColumnModel().getColumn(i).setMaxWidth(0);
					jTableTotal.getColumnModel().getColumn(i).setWidth(0);
				}
			}

			jTableTotal.getColumnModel().getColumn(3).setCellRenderer(new RightAlignCellRenderer());
			TableColumn totalColumn = jTableTotal.getColumnModel().getColumn(4);
			totalColumn.setCellRenderer(new DecimalFormatRenderer());

			jTableTotal.getColumnModel().getColumn(11).setCellRenderer(new RightAlignCellRenderer());
			TableColumn totalAmountColumn = jTableTotal.getColumnModel().getColumn(12);
			totalAmountColumn.setCellRenderer(new DecimalFormatRenderer());
		}
		return jTableTotal;
	}

	private DateTextField getMovDateFrom() {
		GregorianCalendar time = new GregorianCalendar();
		//time.roll(GregorianCalendar.WEEK_OF_YEAR, false);
		time.add(GregorianCalendar.WEEK_OF_YEAR, -1);
		movDateFrom = new DateTextField(time);
		return movDateFrom;
	}

	private DateTextField getMovDateTo() {
		movDateTo = new DateTextField(new GregorianCalendar());
		return movDateTo;
	}

	private DateTextField getLotPrepFrom() {
		lotPrepFrom = new DateTextField();
		return lotPrepFrom;
	}

	private DateTextField getLotPrepTo() {
		lotPrepTo = new DateTextField();
		return lotPrepTo;
	}

	private DateTextField getLotDueFrom() {
		lotDueFrom = new DateTextField();
		return lotDueFrom;
	}

	private DateTextField getLotDueTo() {
		lotDueTo = new DateTextField();
		return lotDueTo;
	}

	/**
	 * This method creates the button that filters the data
	 *
	 * @return
	 */
	private JButton getFilterButton() {
		filterButton = new JButton(MessageBundle.getMessage("angal.common.filter.btn"));
		filterButton.setMnemonic(MessageBundle.getMnemonic("angal.common.filter.btn.key"));
		filterButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Integer medicalSelected = null;
				String medicalTypeSelected = null;
				String typeSelected = null;
				String wardSelected = null;
				boolean dateOk = true;

				GregorianCalendar movFrom = movDateFrom.getCompleteDate();
				GregorianCalendar movTo = movDateTo.getCompleteDate();
				if ((movFrom == null) || (movTo == null)) {
					if (!((movFrom == null) && (movTo == null))) {
						MessageDialog.error(null, "angal.medicalstock.chooseavalidmovementdate.msg");
						dateOk = false;
					}
				} else if (movFrom.compareTo(movTo) > 0) {
					MessageDialog.error(null, "angal.medicalstock.movementdatefromcannotbelaterthanmovementdateto");
					dateOk = false;
				}

				if (!isAutomaticLot()) {
					GregorianCalendar prepFrom = lotPrepFrom.getCompleteDate();
					GregorianCalendar prepTo = lotPrepTo.getCompleteDate();
					if ((prepFrom == null) || (prepTo == null)) {
						if (!((prepFrom == null) && (prepTo == null))) {
							MessageDialog.error(null, "angal.medicalstock.chooseavalidpreparationdate");
							dateOk = false;
						}
					} else if (prepFrom.compareTo(prepTo) > 0) {
						MessageDialog.error(null, "angal.medicalstock.preparationdatefromcannotbelaterpreparationdateto");
						dateOk = false;
					}
				}

				GregorianCalendar dueFrom = lotDueFrom.getCompleteDate();
				GregorianCalendar dueTo = lotDueTo.getCompleteDate();
				if ((dueFrom == null) || (dueTo == null)) {
					if (!((dueFrom == null) && (dueTo == null))) {
						MessageDialog.error(null, "angal.medicalstock.chooseavalidduedate.msg");
						dateOk = false;
					}
				} else if (dueFrom.compareTo(dueTo) > 0) {
					MessageDialog.error(null, "angal.medicalstock.duedatefromcannotbelaterthanduedateto");
					dateOk = false;
				}

				if (dateOk) {
					if (medicalBox.isEnabled()) {
						if (!(medicalBox.getSelectedItem() instanceof String)) {
							medicalSelected = ((Medical) medicalBox
									.getSelectedItem()).getCode();
						}
					} else {
						if (!(medicalTypeBox.getSelectedItem() instanceof String)) {
							medicalTypeSelected = ((MedicalType) medicalTypeBox
									.getSelectedItem()).getCode();
						}
					}
					if (!(typeBox.getSelectedItem() instanceof String)) {
						typeSelected = ((MovementType) typeBox
								.getSelectedItem()).getCode();
					}
					if (!(wardBox.getSelectedItem() instanceof String)) {
						wardSelected = ((Ward) wardBox.getSelectedItem())
								.getCode();
					}
					if (!isAutomaticLot()) {
						model = new MovBrowserModel(medicalSelected,
								medicalTypeSelected, wardSelected, typeSelected,
								movDateFrom.getCompleteDate(),
								movDateTo.getCompleteDate(),
								lotPrepFrom.getCompleteDate(),
								lotPrepTo.getCompleteDate(),
								lotDueFrom.getCompleteDate(),
								lotDueTo.getCompleteDate());
					} else {
						model = new MovBrowserModel(medicalSelected,
								medicalTypeSelected, wardSelected, typeSelected,
								movDateFrom.getCompleteDate(),
								movDateTo.getCompleteDate(),
								null,
								null,
								lotDueFrom.getCompleteDate(),
								lotDueTo.getCompleteDate());
					}

					if (moves != null) {
						model.fireTableDataChanged();
						movTable.updateUI();
					}
					updateTotals();
				}
			}

		});
		return filterButton;
	}

	/**
	 * This method creates the button that close the mask
	 *
	 * @return
	 */
	private JButton getCloseButton() {
		closeButton = new JButton(MessageBundle.getMessage("angal.common.close.btn"));
		closeButton.setMnemonic(MessageBundle.getMnemonic("angal.common.close.btn.key"));
		closeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dispose();
			}

		});
		return closeButton;
	}

	/**
	 * This method creates the button that load the charging movement mask
	 *
	 * @return
	 */
	private JButton getChargeButton() {
		chargeButton = new JButton(MessageBundle.getMessage("angal.medicalstock.charge.btn"));
		chargeButton.setMnemonic(MessageBundle.getMnemonic("angal.medicalstock.charge.btn.key"));
		chargeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new MovStockMultipleCharging(myFrame);
				model = new MovBrowserModel();
				//model.fireTableDataChanged();
				movTable.updateUI();
				updateTotals();
				if (jCheckBoxKeepFilter.isSelected())
					filterButton.doClick();
			}
		});
		return chargeButton;
	}

	/**
	 * This method creates the button that load the discharging movement mask
	 *
	 * @return
	 */
	private JButton getDischargeButton() {
		dischargeButton = new JButton(MessageBundle.getMessage("angal.medicalstock.discharge.btn"));
		dischargeButton.setMnemonic(MessageBundle.getMnemonic("angal.medicalstock.discharge.btn.key"));
		dischargeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new MovStockMultipleDischarging(myFrame);
				model = new MovBrowserModel();
				//model.fireTableDataChanged();
				movTable.updateUI();
				updateTotals();
				if (jCheckBoxKeepFilter.isSelected())
					filterButton.doClick();
			}
		});
		return dischargeButton;
	}

	private JButton getExportToExcelButton() {
		exportToExcel = new JButton(MessageBundle.getMessage("angal.medicalstock.exporttoexcel.btn"));
		exportToExcel.setMnemonic(MessageBundle.getMnemonic("angal.medicalstock.exporttoexcel.btn.key"));
		exportToExcel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String fileName = compileFileName();
				File defaultFileName = new File(fileName);
				JFileChooser fcExcel = ExcelExporter.getJFileChooserExcel(defaultFileName);

				int iRetVal = fcExcel.showSaveDialog(MovStockBrowser.this);
				if (iRetVal == JFileChooser.APPROVE_OPTION) {
					File exportFile = fcExcel.getSelectedFile();
					if (!exportFile.getName().endsWith("xls"))
						exportFile = new File(exportFile.getAbsoluteFile() + ".xls");
					ExcelExporter xlsExport = new ExcelExporter();
					try {
						xlsExport.exportTableToExcel(movTable, exportFile);
					} catch (IOException exc) {
						JOptionPane.showMessageDialog(MovStockBrowser.this,
								exc.getMessage(),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						LOGGER.info("Export to excel error : {}", exc.getMessage());
					}
				}
			}
		});
		return exportToExcel;
	}

	private String compileFileName() {
		StringBuilder filename = new StringBuilder("Stock Ledger");
		if (medicalBox.isEnabled()
				&& !medicalBox.getSelectedItem().equals(
				MessageBundle.getMessage("angal.common.all.txt"))) {

			filename.append('_').append(medicalBox.getSelectedItem());
		}
		if (medicalTypeBox.isEnabled()
				&& !medicalTypeBox.getSelectedItem().equals(
				MessageBundle.getMessage("angal.common.all.txt"))) {

			filename.append('_').append(medicalTypeBox.getSelectedItem());
		}
		if (typeBox.isEnabled() &&
				!typeBox.getSelectedItem().equals(
						MessageBundle.getMessage("angal.common.all.txt"))) {

			filename.append('_').append(typeBox.getSelectedItem());
		}
		if (wardBox.isEnabled() &&
				!wardBox.getSelectedItem().equals(
						MessageBundle.getMessage("angal.common.all.txt"))) {

			filename.append('_').append(wardBox.getSelectedItem());
		}
		filename.append('_').append(TimeTools.formatDateTime(movDateFrom.getCompleteDate(), "yyyyMMdd"))
				.append('_').append(TimeTools.formatDateTime(movDateTo.getCompleteDate(), "yyyyMMdd"));
		return filename.toString();
	}

	private ArrayList<Medical> getSearchMedicalsResults(String s, ArrayList<Medical> medicalsList) {
		String query = s.trim();
		ArrayList<Medical> results = new ArrayList<>();
		for (Medical medoc : medicalsList) {
			if (!query.equals("")) {
				String[] patterns = query.split(" ");
				String code = medoc.getProd_code().toLowerCase();
				String description = medoc.getDescription().toLowerCase();
				boolean patternFound = false;
				for (String pattern : patterns) {
					if (code.contains(pattern.toLowerCase()) || description.contains(pattern.toLowerCase())) {
						patternFound = true;
						//It is sufficient that only one pattern matches the query
						break;
					}
				}
				if (patternFound) {
					results.add(medoc);
				}
			} else {
				results.add(medoc);
			}
		}
		return results;
	}

	/**
	 * This is the table model
	 */
	class MovBrowserModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		public MovBrowserModel() {
			GregorianCalendar now = new GregorianCalendar();
			GregorianCalendar old = new GregorianCalendar();
			old.add(GregorianCalendar.WEEK_OF_YEAR, -1);

			new MovBrowserModel(null, null, null, null, old, now, null, null, null, null);
			updateTotals();
		}

		public MovBrowserModel(Integer medicalCode, String medicalType,
				String ward, String movType, GregorianCalendar movFrom,
				GregorianCalendar movTo, GregorianCalendar lotPrepFrom,
				GregorianCalendar lotPrepTo, GregorianCalendar lotDueFrom,
				GregorianCalendar lotDueTo) {
			try {
				moves = movBrowserManager.getMovements(medicalCode, medicalType, ward,
						movType, movFrom, movTo, lotPrepFrom, lotPrepTo,
						lotDueFrom, lotDueTo);
			} catch (OHServiceException e) {
				OHServiceExceptionUtil.showMessages(e);
			}
			updateTotals();
		}

		public int getRowCount() {
			if (moves == null)
				return 0;
			return moves.size();
		}

		public String getColumnName(int c) {
			return pColumns[c];
		}

		public int getColumnCount() {
			return pColumns.length;
		}

		/**
		 * Note: We must get the objects in a reversed way because of the query
		 *
		 * @see org.isf.medicalstock.service.MedicalStockIoOperations
		 */
		public Object getValueAt(int r, int c) {
			Movement movement = moves.get(r);
			Lot lot = movement.getLot();
			BigDecimal cost = lot.getCost();
			int qty = movement.getQuantity();
			int col = -1;
			if (c == col) {
				return movement;
			} else if (c == ++col) {
				return movement.getRefNo();
			} else if (c == ++col) {
				return formatDateTime(movement.getDate());
			} else if (c == ++col) {
				return movement.getType().toString();
			} else if (c == ++col) {
				Ward ward = movement.getWard();
				if (ward != null)
					return ward;
				else
					return "";
			} else if (c == ++col) {
				return qty;
			} else if (c == ++col) {
				return movement.getMedical().getDescription();
			} else if (c == ++col) {
				return movement.getMedical().getType().getDescription();
			} else if (c == ++col) {
				if (isAutomaticLot())
					return MessageBundle.getMessage("angal.medicalstock.generated");
				else
					return lot;
			} else if (c == ++col) {
				return formatDate(lot.getPreparationDate());
			} else if (c == ++col) {
				return formatDate(lot.getDueDate());
			} else if (c == ++col) {
				Supplier origin = movement.getOrigin();
				return origin != null ? supMap.get(origin.getSupId()) : "";
			} else if (c == ++col) {
				return cost;
			} else if (c == ++col && cost != null) {
				return cost.multiply(new BigDecimal(qty));
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	private String formatDate(GregorianCalendar time) {
		if (time == null)
			return MessageBundle.getMessage("angal.medicalstock.nodate");
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YY);
		return sdf.format(time.getTime());
	}

	private String formatDateTime(GregorianCalendar time) {
		if (time == null)
			return MessageBundle.getMessage("angal.medicalstock.nodate");
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YY_HH_MM);
		return sdf.format(time.getTime());
	}

	class EnabledTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setHorizontalAlignment(columnAlignment[column]);
			if (pColumnBold[column]) {
				cell.setFont(new Font(null, Font.BOLD, 12));
			}
			return cell;
		}
	}

	class RightAlignCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setHorizontalAlignment(SwingConstants.RIGHT);
			return cell;
		}
	}

	class DecimalFormatRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;
		private final DecimalFormat formatter100 = new DecimalFormat("#,##0.000");
		private final DecimalFormat formatter10 = new DecimalFormat("#,##0.00");
		private final DecimalFormat formatter1 = new DecimalFormat("#,##0");

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setHorizontalAlignment(columnAlignment[column]);
			if (column == 4 && value instanceof Number)
				value = formatter1.format((Number) value);
			if (column == 11 && value instanceof Number)
				value = formatter100.format((Number) value);
			if (column == 12 && value instanceof Number)
				value = formatter10.format((Number) value);
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
