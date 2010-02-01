package com.googlecode.gmail2ldap.ldap;

import java.util.Set;
import java.util.UUID;

import org.apache.directory.server.core.CoreSession;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ClonedServerEntry;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.filtering.EntryFilteringCursor;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.gmail2ldap.model.User;

public class Loader {

	private static final String TX_ERROR_MSG = "Start a Transaction first !";

	private static final String ROOT_DN = "dc=gmail2ldap,dc=googlecode,dc=com";

	private final String username;

	private Partition partition;

	private final DirectoryService service;

	private final String dnUsers;

	private final String dnGroups;

	private final String dnUsername;

	private String dnTmp;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public Loader(final DirectoryService service, final String username) {
		this.service = service;
		this.username = username;
		dnUsername = "ou=" + username + ",o=gmail," + ROOT_DN;
		dnUsers = "ou=Users," + dnUsername;
		dnGroups = "ou=Groups," + dnUsername;
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
	public void createRoot() {
		try {
			final LdapDN dnUsername = new LdapDN("ou=" + username + ",o=gmail," + ROOT_DN);
			if (!service.getAdminSession().exists(dnUsername)) {
				logger.debug("No root dn found, adding root entries...");
				addRootEntries();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void addRootEntries() {
		try {
			final CoreSession adminSession = service.getAdminSession();

			// dn: dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN dnGmail2ldap = new LdapDN(ROOT_DN);
			if (!adminSession.exists(dnGmail2ldap)) {
				final ServerEntry entryGmail2ldap = service.newEntry(dnGmail2ldap);
				entryGmail2ldap.add("objectClass", "top", "domain", "extensibleObject");
				entryGmail2ldap.add("dc", "gmail2ldap");
				adminSession.add(entryGmail2ldap);
			}

			// dn: o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN dnGmail = new LdapDN("o=gmail," + ROOT_DN);
			if (!adminSession.exists(dnGmail)) {
				final ServerEntry entryGmail = service.newEntry(dnGmail);
				entryGmail.add("objectClass", "top", "organization", "extensibleObject");
				entryGmail.add("o", "gmail");
				adminSession.add(entryGmail);
			}
			// dn:
			// ou=[username],o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN dnUsername = new LdapDN("ou=" + username + ",o=gmail," + ROOT_DN);
			if (!adminSession.exists(dnUsername)) {
				final ServerEntry entryUsername = service.newEntry(dnUsername);
				entryUsername.add("objectClass", "top", "organizationalUnit");
				entryUsername.add("ou", username);
				adminSession.add(entryUsername);
			}
			// dn:
			// ou=Users,ou=[username],o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN ldapDnUsers = new LdapDN(dnUsers);
			if (!adminSession.exists(ldapDnUsers)) {
				final ServerEntry entryUsers = service.newEntry(ldapDnUsers);
				entryUsers.add("objectClass", "top", "organizationalUnit");
				entryUsers.add("ou", "Users");
				adminSession.add(entryUsers);
			}
			// dn:
			// ou=Groups,ou=[username],o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final LdapDN ldapDnGroups = new LdapDN(dnGroups);
			if (!adminSession.exists(ldapDnGroups)) {
				final ServerEntry entryGroups = service.newEntry(ldapDnGroups);
				entryGroups.add("objectClass", "top", "organizationalUnit");
				entryGroups.add("ou", "Groups");
				adminSession.add(entryGroups);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addUser(final User user) {
		if (dnTmp == null) {
			throw new IllegalStateException(TX_ERROR_MSG);
		}
		final ServerEntry entryUser;
		try {
			// dn:
			// uid=[userid],ou=Users,ou=Tmp_123,o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final String dn = "uid=" + user.getUid() + "," + dnTmp;
			logger.debug(dn);
			final LdapDN dnUser = new LdapDN(dn.replace('+', '_'));
			entryUser = service.newEntry(dnUser);
			entryUser.add("objectClass", "top", "inetOrgPerson", "uidObject", "extensibleObject", "person",
					"organizationalPerson");

			// uid: mplanck
			entryUser.add("uid", user.getUid());
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
			// TODO handle same uid exception
			// FIXME go on as if nothing had append
			// throw new RuntimeException(e);
			logger.error(e.getMessage(), e);
		}
	}

	public void createTmpNode() {
		try {
			final CoreSession adminSession = service.getAdminSession();

			// dn:
			// ou=Tmp_1234,ou=[username],o=gmail,dc=gmail2ldap,dc=googlecode,dc=com
			final String tmp = "Tmp_" + UUID.randomUUID();
			dnTmp = "ou=" + tmp + ",ou=" + username + ",o=gmail," + ROOT_DN;
			final LdapDN ldapDnTmp = new LdapDN(dnTmp);
			if (!adminSession.exists(ldapDnTmp)) {
				final ServerEntry entryTmp = service.newEntry(ldapDnTmp);
				entryTmp.add("objectClass", "top", "organizationalUnit");
				entryTmp.add("ou", tmp);
				adminSession.add(entryTmp);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void replaceUsersNode() {
		if (dnTmp == null) {
			throw new IllegalStateException(TX_ERROR_MSG);
		}
		try {
			final CoreSession adminSession = service.getAdminSession();

			final LdapDN ldapDnUsers = new LdapDN(dnUsers);
			final LdapDN ldapDnTmp = new LdapDN(dnTmp);
			if (adminSession.exists(ldapDnUsers)) {
				adminSession.delete(ldapDnUsers);
				final boolean deleteOldRdn = true;
				final Rdn newRdn = new Rdn("ou=Users");
				adminSession.rename(ldapDnTmp, newRdn, deleteOldRdn);
				// adminSession.moveAndRename(ldapDnTmp, new LdapDN(dnUsername),
				// newRdn, deleteOldRdn);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteTmpNode() {
		if (dnTmp == null) {
			throw new IllegalStateException(TX_ERROR_MSG);
		}
		try {
			final CoreSession adminSession = service.getAdminSession();

			final LdapDN ldapDnTmp = new LdapDN(dnTmp);
			if (adminSession.exists(ldapDnTmp)) {
				final String filter = "+(objectClass=person)";
				final EntryFilteringCursor cursor = adminSession.search(ldapDnTmp, filter);
				while (cursor.next()) {
					ClonedServerEntry entry = cursor.get();
					logger.debug("deleting: " + entry.getDn().toString());
					adminSession.delete(entry.getDn());
				}
				adminSession.delete(ldapDnTmp);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
