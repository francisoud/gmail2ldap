package com.googlecode.gmail2ldap;

import java.util.List;

import org.apache.directory.server.core.DirectoryService;

import com.google.gdata.data.contacts.ContactEntry;
import com.googlecode.gmail2ldap.gmail.Contacts;
import com.googlecode.gmail2ldap.ldap.Constants;
import com.googlecode.gmail2ldap.ldap.Loader;
import com.googlecode.gmail2ldap.ldap.Server;

public class Main {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: username password");
			System.exit(-1);
		}
		final String username = args[0];
		final String password = args[1];

		// TODO make this configurable (system property)
		// delete previous ldap store
		// final String tmpDir = System.getProperty("java.io.tmpdir") + "/" +
		// Constants.PARTITION_ID;
		// final File workingDir = (File) new File(tmpDir);
		// workingDir.delete();

		Server server = new Server();
		server.start();

		final DirectoryService service = server.getLdapServer().getDirectoryService();
		final Loader loader = new Loader(service, Constants.PARTITION_ID);
		loader.createRoot(username);

		ProjectSystemTray systemTray = new ProjectSystemTray();

		final Contacts contacts = new Contacts(username, password);
		final List<ContactEntry> entries = contacts.list();
		for (int i = 0; i < entries.size(); i++) {
			final ContactEntry entry = entries.get(i);
			System.out.println("\t" + entry.getTitle().getPlainText());
			loader.addUser(entry);
		}
		System.out.println("\nTotal Entries: " + entries.size());

	}
}
