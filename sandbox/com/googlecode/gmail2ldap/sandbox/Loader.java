package com.googlecode.gmail2ldap.sandbox;

import java.util.List;

import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.PhoneNumber;

public class Loader {

	private static final String ROOT_DN = "dc=gmail2ldap,dc=googlecode,dc=com";

	private DirectoryService service;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void addUser(final ContactEntry entry) {
		try {
			if (entry.getNickname() == null) {
				logger.warn("Ignoring: " + entry.getId());
				return;
			}
			logger.debug("Adding: " + entry.getId());

			// dn: uid=mplanck,ou=Users,dc=example,dc=com
			// objectClass: organizationalPerson
			// objectClass: person
			// objectClass: extensibleObject
			// objectClass: uidObject
			// objectClass: inetOrgPerson
			// objectClass: top
			// cn: Max Planck
			// facsimiletelephonenumber: +1 904 982 6883
			// givenname: Max
			// mail: mplanck@example.com
			// ou: Users
			// roomnumber: 666
			// sn: Planck
			// telephonenumber: +1 904 982 6882
			// uid: mplanck
			// userpassword:: c2VjcmV0

			// dn:
			// uid=[userid],ou=Users,ou=[username],o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final String dn = entry.getNickname().getValue() + ",ou=Users,ou=[username],o=gmail," + ROOT_DN;
			final LdapDN dnUser = new LdapDN(dn);
			final ServerEntry entryUser = service.newEntry(dnUser);
			entryUser.add("objectClass", "top", "inetOrgPerson", "uidObject", "extensibleObject", "person",
					"organizationalPerson");
			// FIXME
			entryUser.add("cn", entry.getShortName() + " " + entry.getName());
			entryUser.add("givenname", entry.getShortName().getValue());
			entryUser.add("sn", entry.getNickname().getValue());
			entryUser.add("uid", entry.getNickname().getValue());
			final List<Email> emailAddresses = entry.getEmailAddresses();
			for (final Email email : emailAddresses) {
				entryUser.add("mail", email.getAddress());
			}
			final List<PhoneNumber> phoneNumbers = entry.getPhoneNumbers();
			for (final PhoneNumber phoneNumber : phoneNumbers) {
				entryUser.add("telephonenumber", phoneNumber.getPhoneNumber());
			}
			service.getAdminSession().add(entryUser);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
