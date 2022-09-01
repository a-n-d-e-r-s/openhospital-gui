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
package org.isf.menu.gui;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import org.isf.generaldata.GeneralData;
import org.isf.menu.model.User;
import org.isf.utils.jobjects.DelayTimerCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutEventListener implements AWTEventListener, Login.LoginListener, DelayTimerCallback {

	@Override
	public void eventDispatched(AWTEvent e) {
		if (!GeneralData.getGeneralData().getSINGLEUSER() && e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) e;
			if (Arrays.asList(MouseEvent.MOUSE_MOVED).contains(me.getID())) {
				if (UserSession.getTimer() != null) {
					UserSession.getTimer().startTimer();
				}
			}
		} else if (!GeneralData.getGeneralData().getSINGLEUSER() && e instanceof KeyEvent) {
			KeyEvent key = (KeyEvent) e;
			if (Arrays.asList(KeyEvent.KEY_PRESSED).contains(key.getID())) {
				if (UserSession.getTimer() != null) {
					UserSession.getTimer().startTimer();
				}
			}
		}

	}

	@Override
	public void loginInserted(AWTEvent e) {
		if (e.getSource() instanceof User) {
			User myUser = (User) e.getSource();
			UserSession.setUser(myUser);
			UserSession.getTimer().startTimer();
		}

	}

	@Override
	public void trigger() {
		if (!GeneralData.getGeneralData().getSINGLEUSER() && UserSession.isLoggedIn()) {
				UserSession.restartSession();
				UserSession.getTimer().startTimer();
				System.out.println("RESTARTED...");
		}

	}

}

