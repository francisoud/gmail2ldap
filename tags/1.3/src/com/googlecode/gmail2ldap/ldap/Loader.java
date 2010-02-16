package com.googlecode.gmail2ldap.ldap;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;
import com.googlecode.gmail2ldap.gmail.Contacts;
import com.googlecode.gmail2ldap.model.User;

public class Loader {

	// private static final String TX_ERROR_MSG = "Start a Transaction first !";

	private final SchemaAdministrator administrator;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public Loader(final SchemaAdministrator administrator) {
		this.administrator = administrator;
	}

	public void addUser(final User user) {
		final DirectoryService service = administrator.getService();
		if (!administrator.isTransactionStarted()) {
			throw new IllegalStateException(SchemaAdministrator.TX_ERROR_MSG);
		}

		final ServerEntry entryUser;
		try {
			final String uid = user.getUid().toString();
			final String dnTmp = administrator.getDnTmp();
			// dn:
			// uid=[userid],ou=Users,ou=Tmp_123,o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final String dn = "uid=" + uid + "," + dnTmp;
			logger.debug(dn);
			final LdapDN dnUser = new LdapDN(dn.replace('+', '_'));
			entryUser = service.newEntry(dnUser);
			entryUser.add("objectClass", "top", "inetOrgPerson", "uidObject", "extensibleObject", "person",
					"organizationalPerson");

			// uid: mplanck
			entryUser.add("uid", uid);
			// cn: Max Planck
			entryUser.add("cn", user.getFullName());
			// givenname: Max
			entryUser.add("givenname", user.getFirstName());
			// sn: Planck
			entryUser.add("sn", user.getLastName());

			// mail: mplanck@example.com
			entryUser.add("mail", user.getEmail());

			service.getAdminSession().add(entryUser);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int addUsers(final Contacts contacts) {
		final List<ContactEntry> entries = contacts.list();
		for (final ContactEntry entry : entries) {
			final List<Email> emails = entry.getEmailAddresses();
			if (emails.size() > 0) {
				final User user = new User();
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
				for (final Email email : emails) {
					user.setEmail(email.getAddress());
					// user.setNickName(email.getDisplayName());
					break; // only keep first email
				}
				logger.debug(ToStringBuilder.reflectionToString(user, ToStringStyle.MULTI_LINE_STYLE));
				addUser(user);
			}
		}
		return entries.size();
	}

	public SchemaAdministrator getAdministrator() {
		return administrator;
	}
}
