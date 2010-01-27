package com.googlecode.gmail2ldap.ldap;

import java.util.HashSet;

import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.xdbm.Index;

public class DirectoryUtil {

	private final DirectoryService service;

	public DirectoryUtil(final DirectoryService service) {
		this.service = service;
	}

	/**
	 * Add a new partition to the server
	 * 
	 * @param partitionId
	 *            The partition Id
	 * @param partitionDn
	 *            The partition DN
	 * @return The newly added partition
	 * @throws Exception
	 *             If the partition can't be added
	 */
	public Partition addPartition(String partitionId, String partitionDn) {
		Partition partition = new JdbmPartition();
		partition.setId(partitionId);
		partition.setSuffix(partitionDn);
		try {
			service.addPartition(partition);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return partition;
	}

	/**
	 * Add a new set of index on the given attributes
	 * 
	 * @param partition
	 *            The partition on which we want to add index
	 * @param attrs
	 *            The list of attributes to index
	 */
	public void addIndex(Partition partition, String... attrs) {
		// Index some attributes on the partition
		HashSet<Index<?, ServerEntry>> indexedAttributes = new HashSet<Index<?, ServerEntry>>();

		for (String attribute : attrs) {
			indexedAttributes.add(new JdbmIndex<String, ServerEntry>(attribute));
		}

		((JdbmPartition) partition).setIndexedAttributes(indexedAttributes);
	}
}
