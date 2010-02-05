package com.googlecode.gmail2ldap.gmail;

import static com.google.gdata.client.ClientLoginAccountType.HOSTED;

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
import com.googlecode.gmail2ldap.config.Account;
import com.googlecode.gmail2ldap.config.Config;

public class Contacts {

	private static final String APP_NAME_SUFFIX = "-gmail2ldap.code.google.com-1.x";

	private final Account account;

	private final Config config;

	private ContactsService service;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public Contacts(final Account account) {
		this.account = account;
		this.config = Config.getSingleton();
	}

	/**
	 * Get a list of all entries.
	 */
	public List<ContactEntry> list() {
		try {
			final String protocol;
			if (account.isHosted()) {
				protocol = "https";
			} else {
				protocol = "http";
			}
			final StringBuilder sb = new StringBuilder(protocol);
			sb.append("://www.google.com/m8/feeds/contacts/");
			sb.append(account.getEmail());
			sb.append("/full?max-results="); // base
			final String maxResults = config.getProperties().getProperty(Config.GMAIL_MAX_CONTACTS);
			sb.append(maxResults);
			final String url = sb.toString();
			final URL metafeedUrl = new URL(url);
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

	public Account getAccount() {
		return account;
	}

	private ContactsService getService() {
		if (service == null) {
			// Create a new Contacts service
			try {
				final String applicationName = account.getEmail() + APP_NAME_SUFFIX;
				if (account.isHosted()) {
					service = new ContactsService(applicationName, "https", "www.google.com");
					service.setUserCredentials(account.getEmail(), account.getPassword(), HOSTED);
				} else {
					service = new ContactsService(applicationName);
					service.setUserCredentials(account.getUsername(), account.getPassword());
				}
			} catch (AuthenticationException e) {
				throw new RuntimeException(e);
			}
		}
		return service;
	}
}
