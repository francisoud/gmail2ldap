package com.googlecode.gmail2ldap.ldap;

import java.util.Set;

import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.shared.ldap.exception.LdapNameNotFoundException;
import org.apache.directory.shared.ldap.name.LdapDN;

public class Loader {

	private Partition partition;
	private final DirectoryService service;

	public Loader(final DirectoryService service, final String partitionId) {
		this.service = service;

		// this.partition =
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

	public void load() {
		try {
			// FIXME use a ldif or code ?
			// Inject the gmail2ldap root entry
			try {
				service.getAdminSession().lookup(partition.getSuffixDn());
			} catch (LdapNameNotFoundException lnnfe) {
				LdapDN dnApache = new LdapDN("dc=gmail2ldap,dc=googlecode,dc=com");
				ServerEntry entryApache = service.newEntry(dnApache);
				entryApache.add("objectClass", "top", "domain", "extensibleObject");
				entryApache.add("dc", "gmail2ldap");
				service.getAdminSession().add(entryApache);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
