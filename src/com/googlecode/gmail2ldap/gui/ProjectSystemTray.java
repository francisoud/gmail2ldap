package com.googlecode.gmail2ldap.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.gmail2ldap.config.Account;
import com.googlecode.gmail2ldap.gmail.Contacts;
import com.googlecode.gmail2ldap.ldap.Loader;
import com.googlecode.gmail2ldap.ldap.Transaction;

public class ProjectSystemTray {

	private final Image defaultImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("system-users.png"));

	private final Image busyImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("view-refresh.png"));

	private final Image warningImage = Toolkit.getDefaultToolkit().getImage(
			getClass().getResource("dialog-warning.png"));

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private List<Account> accounts = new ArrayList<Account>();

	private final Loader loader;

	public ProjectSystemTray(final Loader loader) {
		this.loader = loader;
	}

	public void start() {
		final TrayIcon trayIcon;

		if (SystemTray.isSupported()) {
			final SystemTray tray = SystemTray.getSystemTray();

			final PopupMenu popup = new PopupMenu();

			trayIcon = new TrayIcon(defaultImage, "gmail2ldap", popup);

			popup.add("Synchronize:");

			for (final Account account : accounts) {
				final Contacts contacts = new Contacts(account);
				final ActionListener synchronizeListener = new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						trayIcon.setImage(busyImage);
						trayIcon.setToolTip("Synchronising...");
						logger.info("Synchronizing...");
						loader.getAdministrator().setUsername(account.getUsername());
						final Transaction transaction = new Transaction(loader);
						try {
							transaction.begin();
							final int entries = loader.addUsers(contacts);
							logger.info("Total Entries: " + entries);
							transaction.commit();
							trayIcon.displayMessage("gmail2ldap", entries + " contacts synchronized", MessageType.INFO);
							trayIcon.setImage(defaultImage);
							trayIcon.setToolTip("gmail2ldap");
						} catch (Throwable t) {
							logger.error("Rollback changes: " + t.getMessage(), t);
							transaction.rollback();
							trayIcon.setImage(warningImage);
							trayIcon.setToolTip("gmail2ldap");
							trayIcon.displayMessage("gmail2ldap", "An error occured while synchronizing",
									MessageType.WARNING);
						}

					}
				};

				final MenuItem synchronizeItem = new MenuItem(account.getEmail());
				synchronizeItem.addActionListener(synchronizeListener);
				popup.add(synchronizeItem);
			}
			loader.getAdministrator().reset();
			popup.add("");

			final MenuItem exitItem = getExitMenu();
			popup.add(exitItem);

			trayIcon.setImageAutoSize(true);

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

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
}
