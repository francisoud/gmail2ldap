package com.googlecode.gmail2ldap.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class Config {

	private static Config SINGLETON;

	private Digester digester;

	private Properties properties = new Properties();

	public static String GMAIL_MAX_CONTACTS = "gmail.max.contacts";

	public static final String STORE_FOLDER = "store.folder";

	private static final Logger logger = LoggerFactory.getLogger(Config.class);

	public static Config getSingleton() {
		if (SINGLETON == null) {
			SINGLETON = new Config();
		}
		return SINGLETON;
	}

	private Config() {
		final URL rules = getClass().getResource("rules.xml");
		digester = DigesterLoader.createDigester(rules);

		try {
			final FileInputStream input = new FileInputStream("config/config.properties");
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Account> parse(final InputStream input) {
		final List<Account> beans = new ArrayList<Account>();
		digester.push(beans);
		try {
			return (List<Account>) digester.parse(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public String getStoreFolder() {
		final String sep = System.getProperty("file.separator");

		String storeFolder = SINGLETON.getProperties().getProperty(Config.STORE_FOLDER);
		if (storeFolder == null) {
			storeFolder = System.getProperty("java.io.tmpdir");
		}
		if (!storeFolder.endsWith(sep)) {
			storeFolder = storeFolder + sep;
		}
		logger.debug(storeFolder);
		return storeFolder;
	}
}
