package com.googlecode.gmail2ldap.ldap;

import java.util.List;
import java.util.Set;

import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.shared.ldap.exception.LdapNameNotFoundException;
import org.apache.directory.shared.ldap.name.LdapDN;

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.PhoneNumber;

public class Loader {

	private static final String ROOT_DN = "dc=gmail2ldap,dc=googlecode,dc=com";
	private Partition partition;
	private final DirectoryService service;

	public Loader(final DirectoryService service, final String partitionId) {
		this.service = service;
		final Set<? extends Partition> partitions = service.getPartitions();
		for (final Partition tmp : partitions) {
			if (Constants.PARTITION_ID.equals(tmp.getId())) {
				partition = tmp;
				break;
			}
		}
		if (partition == null) {
			throw new IllegalStateException("no partition found with id: " + Constants.PARTITION_ID);
		}
	}

	/**
	 * Inject the gmail2ldap root entry.
	 */
	public void createRoot(final String username) {
		// FIXME use a ldif or code ?
		try {
			service.getAdminSession().lookup(partition.getSuffixDn());
		} catch (LdapNameNotFoundException lnnfe) {
			System.out.println("No root dn found, adding root entries...");
			addRootEntries(username);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addUser(final ContactEntry entry) {
		try {
			if (entry.getNickname() == null) {
				System.err.println("Ignoring: " + entry.getId());
				return;
			}
			System.out.println("Adding: " + entry.getId());

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
			// uid=[userid],ou=Users,ou=benjamin.francisoud,o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final String dn = entry.getNickname().getValue() + ",ou=Users,ou=benjamin.francisoud,o=gmail," + ROOT_DN;
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

	private void addRootEntries(final String username) {
		try {
			// dn: dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN dnGmail2ldap = new LdapDN(ROOT_DN);
			final ServerEntry entryGmail2ldap = service.newEntry(dnGmail2ldap);
			entryGmail2ldap.add("objectClass", "top", "domain", "extensibleObject");
			entryGmail2ldap.add("dc", "gmail2ldap");
			service.getAdminSession().add(entryGmail2ldap);

			// dn: o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN dnGmail = new LdapDN("o=gmail," + ROOT_DN);
			final ServerEntry entryGmail = service.newEntry(dnGmail);
			entryGmail.add("objectClass", "top", "organization", "extensibleObject");
			entryGmail.add("o", "gmail");
			service.getAdminSession().add(entryGmail);

			// dn:
			// ou=benjamin.francisoud,o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN dnUsername = new LdapDN("ou=benjamin.francisoud,o=gmail," + ROOT_DN);
			final ServerEntry entryUsername = service.newEntry(dnUsername);
			entryUsername.add("objectClass", "top", "organizationalUnit");
			entryUsername.add("ou", "benjamin.francisoud");
			service.getAdminSession().add(entryUsername);

			// dn:
			// ou=Users,ou=benjamin.francisoud,o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN dnUsers = new LdapDN("ou=Users,ou=benjamin.francisoud,o=gmail," + ROOT_DN);
			final ServerEntry entryUsers = service.newEntry(dnUsers);
			entryUsers.add("objectClass", "top", "organizationalUnit");
			entryUsers.add("ou", "Users");
			service.getAdminSession().add(entryUsers);

			// dn:
			// ou=Groups,ou=benjamin.francisoud,o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN dnGroups = new LdapDN("ou=Groups,ou=benjamin.francisoud,o=gmail," + ROOT_DN);
			final ServerEntry entryGroups = service.newEntry(dnGroups);
			entryGroups.add("objectClass", "top", "organizationalUnit");
			entryGroups.add("ou", "Groups");
			service.getAdminSession().add(entryUsers);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
