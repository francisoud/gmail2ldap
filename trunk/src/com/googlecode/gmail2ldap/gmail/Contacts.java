package com.googlecode.gmail2ldap.gmail;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class Contacts {

	private static final String APPLICATION_NAME = "gmail2ldap";

	private final String username;

	private final ContactsService service;

	public Contacts(final String username, final String password) {
		this.username = username;
		// Create a new Contacts service
		service = new ContactsService(APPLICATION_NAME);
		try {
			service.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get a list of all entries.
	 */
	public List<ContactEntry> list() {
		try {
			final URL metafeedUrl = new URL("http://www.google.com/m8/feeds/contacts/" + username + "@gmail.com/base");
			System.out.println("Getting Contacts entries...\n");
			final ContactFeed resultFeed = service.getFeed(metafeedUrl, ContactFeed.class);
			final List<ContactEntry> entries = resultFeed.getEntries();
			return entries;
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
