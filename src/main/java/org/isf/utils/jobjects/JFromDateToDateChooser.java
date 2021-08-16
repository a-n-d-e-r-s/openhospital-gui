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
package org.isf.utils.jobjects;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;

/**
 * @author Nanni
 */
public class JFromDateToDateChooser extends JPanel {
	
	private static final String DATE_TIME_FORMAT = "dd/MM/yyyy";
	private static final int TEXT_SIZE = 12;
	
	private CustomJDateChooser dateFromDateChooser;
	private CustomJDateChooser dateToDateChooser;
	private Date dateTimeFrom;
	private Date dateTimeTo;

	private static final long serialVersionUID = 1L;

	public JFromDateToDateChooser() {
		FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
		layout.setHgap(5);
		this.setLayout(layout);
		this.dateTimeFrom = new Date();
		this.dateTimeTo = new Date();
		initComponents();
	}
	
	public JFromDateToDateChooser(Date dateFrom, Date dateTo) {
		FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
		layout.setHgap(5);
		this.setLayout(layout);
		this.dateTimeFrom = dateFrom;
		this.dateTimeTo = dateTo;
		initComponents();
	}
	
	private void initComponents() {
		this.add(new JLabel(MessageBundle.getMessage("angal.common.from.txt")+":"));
		this.add(getCustomJDateFrom(this.dateTimeFrom));
		this.add(new JLabel(MessageBundle.getMessage("angal.common.to.txt")+":"));
		this.add(getCustomJDateTo(this.dateTimeTo));
	}
	
	private CustomJDateChooser getCustomJDateFrom(Date dateFrom) {
		if (dateFromDateChooser == null) {
			dateFromDateChooser = new CustomJDateChooser();
			dateFromDateChooser.setDate(dateFrom);
			dateFromDateChooser.setLocale(new Locale(GeneralData.LANGUAGE));
			dateFromDateChooser.setDateFormatString(DATE_TIME_FORMAT); //$NON-NLS-1$
			dateFromDateChooser.setFont(new Font("Arial", Font.BOLD, TEXT_SIZE), false);
			dateFromDateChooser.setPreferredSize(new Dimension(100, 24));
			dateFromDateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					dateTimeFrom = dateFromDateChooser.getDate();
				}
			});
		}
		return dateFromDateChooser;
	}
	
	private CustomJDateChooser getCustomJDateTo(Date dateTo) {
		if (dateToDateChooser == null) {
			dateToDateChooser = new CustomJDateChooser();
			dateToDateChooser.setDate(dateTo);
			dateToDateChooser.setLocale(new Locale(GeneralData.LANGUAGE));
			dateToDateChooser.setDateFormatString(DATE_TIME_FORMAT); //$NON-NLS-1$
			dateToDateChooser.setFont(new Font("Arial", Font.BOLD, TEXT_SIZE), false);
			dateToDateChooser.setPreferredSize(new Dimension(100, 24));
			dateToDateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					dateTimeTo = dateToDateChooser.getDate();
				}
			});
		}
		return dateToDateChooser;
	}

	/**
	 * @param layout
	 */
	public JFromDateToDateChooser(LayoutManager layout) {
		super(layout);
	}

	/**
	 * @param isDoubleBuffered
	 */
	public JFromDateToDateChooser(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public JFromDateToDateChooser(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateTimeFrom;
	}

	/**
	 * @param dateFrom the dateFrom to set
	 */
	public void setDateFrom(CustomJDateChooser dateFrom) {
		this.dateFromDateChooser = dateFrom;
	}

	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTimeTo;
	}

	/**
	 * @param dateTo the dateTo to set
	 */
	public void setDateTo(CustomJDateChooser dateTo) {
		this.dateToDateChooser = dateTo;
	}

}
