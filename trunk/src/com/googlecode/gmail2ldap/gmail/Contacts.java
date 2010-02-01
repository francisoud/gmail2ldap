package com.googlecode.gmail2ldap.gmail;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class Contacts {

	private static final String APPLICATION_NAME = "gmail2ldap";

	private final String username;

	private final String password;

	private ContactsService service;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public Contacts(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Get a list of all entries.
	 */
	public List<ContactEntry> list() {
		try {
			final URL metafeedUrl = new URL("http://www.google.com/m8/feeds/contacts/" + username + "@gmail.com/base");
			logger.debug("Getting Contacts entries from " + metafeedUrl.toString());
			final ContactFeed resultFeed = getService().getFeed(metafeedUrl, ContactFeed.class);
			final List<ContactEntry> entries = resultFeed.getEntries();
			return entries;
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ContactsService getService() {
		if (service == null) {
			// Create a new Contacts service
			service = new ContactsService(APPLICATION_NAME);
			try {
				service.setUserCredentials(username, password);
			} catch (AuthenticationException e) {
				throw new RuntimeException(e);
			}
		}
		return service;
	}
}
