package com.googlecode.gmail2ldap;

import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.server.core.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.gmail2ldap.config.Account;
import com.googlecode.gmail2ldap.config.Config;
import com.googlecode.gmail2ldap.gmail.Contacts;
import com.googlecode.gmail2ldap.gui.ProjectSystemTray;
import com.googlecode.gmail2ldap.gui.SynchronizeListener;
import com.googlecode.gmail2ldap.ldap.Loader;
import com.googlecode.gmail2ldap.ldap.Server;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		final Config config = Config.getSingleton();
		final FileInputStream input;
		try {
			input = new FileInputStream("config/accounts.xml");
		} catch (FileNotFoundException e) {
			System.err.println("Go to the 'config' dir and rename accounts-sample.xml to accounts.xml.");
			System.err.println("Edit accounts.xml with your own email/password.");
			System.exit(-1);
			return;
		}
		final List<Account> accounts = config.parse(input);
		final List<MenuItem> synchronizeItems = new ArrayList<MenuItem>();

		final Server server = new Server();
		server.start();

		final DirectoryService service = server.getLdapServer().getDirectoryService();
		final ProjectSystemTray systemTray = new ProjectSystemTray();

		logger.info("Accounts:");
		for (final Account account : accounts) {
			logger.info("\t" + account.getUsername() + " " + account.getEmail());
			final Loader loader = new Loader(service, account.getUsername());
			loader.createRoot();

			final Contacts contacts = new Contacts(account);
			final ActionListener synchronizeListener = new SynchronizeListener(contacts, loader);

			final MenuItem synchronizeItem = new MenuItem(account.getEmail());
			synchronizeItem.addActionListener(synchronizeListener);
			synchronizeItems.add(synchronizeItem);
		}
		systemTray.setSynchronizeItems(synchronizeItems);
		systemTray.start();
	}
}
