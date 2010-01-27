package com.googlecode.gmail2ldap.sandbox;

/*Could not find all the Google Data dependencies required for this template from the directory provided.

 You may not be able to execute this template (or your future application) without adding the required dependencies.*/

/* INSTRUCTION: This is a command line application. So please execute this template with the following arguments:

 arg[0] = username
 arg[1] = password
 */

/**
 * @author (Your Name Here)
 *
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * This is a test template
 */

public class Contacts {

	public static void main(String[] args) {

		try {

			// Create a new Contacts service
			ContactsService myService = new ContactsService("My Application");
			myService.setUserCredentials(args[0], args[1]);

			// Get a list of all entries
			URL metafeedUrl = new URL("http://www.google.com/m8/feeds/contacts/" + args[0] + "@gmail.com/base");
			System.out.println("Getting Contacts entries...\n");
			ContactFeed resultFeed = myService.getFeed(metafeedUrl, ContactFeed.class);
			List<ContactEntry> entries = resultFeed.getEntries();
			for (int i = 0; i < entries.size(); i++) {
				ContactEntry entry = entries.get(i);
				System.out.println("\t" + entry.getTitle().getPlainText());
			}
			System.out.println("\nTotal Entries: " + entries.size());
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
