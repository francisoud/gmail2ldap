package com.googlecode.gmail2ldap.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;
import com.googlecode.gmail2ldap.gmail.Contacts;
import com.googlecode.gmail2ldap.ldap.Loader;
import com.googlecode.gmail2ldap.ldap.Transaction;
import com.googlecode.gmail2ldap.model.User;

public class SynchronizeListener implements ActionListener {

	private final Contacts contacts;

	private final Loader loader;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public SynchronizeListener(final Contacts contacts, final Loader loader) {
		this.contacts = contacts;
		this.loader = loader;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		logger.info("Synchronizing...");
		final Transaction transaction = new Transaction(loader);
		try {
			transaction.begin();
			final List<ContactEntry> entries = contacts.list();
			for (final ContactEntry entry : entries) {
				final List<Email> emails = entry.getEmailAddresses();
				if (emails.size() > 0) {
					final User user = new User();
					for (final Email email : emails) {
						user.setEmail(email.getAddress());
						// user.setNickName(email.getDisplayName());
						break; // only keep first email
					}
					if (entry.getNickname() != null) {
						user.setNickName(entry.getNickname().getValue());
					}
					if (entry.getName() != null) {
						if (entry.getName().getGivenName() != null) {
							user.setFirstName(entry.getName().getGivenName().getValue());
						}
						if (entry.getName().getFamilyName() != null) {
							user.setLastName(entry.getName().getFamilyName().getValue());
						}
					}
					logger.debug(ToStringBuilder.reflectionToString(user, ToStringStyle.MULTI_LINE_STYLE));
					loader.addUser(user);
				}
			}
			logger.info("Total Entries: " + entries.size());
			transaction.commit();
		} catch (Throwable t) {
			logger.error("Rollback changes: " + t.getMessage(), t);
			transaction.rollback();
		}
	}
}
