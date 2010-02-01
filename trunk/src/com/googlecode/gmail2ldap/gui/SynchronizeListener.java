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
		final List<ContactEntry> entries = contacts.list();
		for (final ContactEntry entry : entries) {
			logger.debug(ToStringBuilder.reflectionToString(entry, ToStringStyle.MULTI_LINE_STYLE));

			final List<Email> emails = entry.getEmailAddresses();
			if (emails.size() > 0) {
				final User user = new User();
				for (final Email email : emails) {
					logger.debug(ToStringBuilder.reflectionToString(email, ToStringStyle.MULTI_LINE_STYLE));
					user.setEmail(email.getAddress());
					// user.setNickName(email.getDisplayName());
					break; // only keep first email
				}
				user.setNickName(entry.getNickname().getValue());
				user.setFirstName(entry.getName().getGivenName().getValue());
				user.setLastName(entry.getName().getFamilyName().getValue());
				loader.addUser(user);
			}
		}
		logger.info("Total Entries: " + entries.size());
	}
}
