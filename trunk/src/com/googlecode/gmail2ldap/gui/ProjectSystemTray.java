package com.googlecode.gmail2ldap.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectSystemTray {

	private final Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("system-users.png"));

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private List<MenuItem> synchronizeItems = new ArrayList<MenuItem>();

	public List<MenuItem> getSynchronizeItems() {
		return synchronizeItems;
	}

	public void setSynchronizeItems(List<MenuItem> synchronizeItems) {
		this.synchronizeItems = synchronizeItems;
	}

	public void start() {
		final TrayIcon trayIcon;

		if (SystemTray.isSupported()) {
			final SystemTray tray = SystemTray.getSystemTray();

			final PopupMenu popup = new PopupMenu();

			popup.add("Synchronize:");
			for (final MenuItem synchronizeItem : synchronizeItems) {
				popup.add(synchronizeItem);
			}
			popup.add("");

			final MenuItem exitItem = getExitMenu();
			popup.add(exitItem);

			trayIcon = new TrayIcon(image, "gmail2ldap", popup);

			final ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					trayIcon.displayMessage("Action Event", "An Action Event Has Been Performed!",
							TrayIcon.MessageType.INFO);
				}
			};

			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(actionListener);

			final MouseListener mouseListener = new TrayMouseListener();
			trayIcon.addMouseListener(mouseListener);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				throw new RuntimeException("TrayIcon could not be added.");
			}
		} else {
			throw new RuntimeException("System Tray is not supported");
		}
	}

	private MenuItem getExitMenu() {
		final ActionListener exitListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("Exiting...");
				System.exit(0);
			}
		};
		final MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(exitListener);
		return exitItem;
	}
}
