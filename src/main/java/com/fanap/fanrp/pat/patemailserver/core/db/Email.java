package com.fanap.fanrp.pat.patemailserver.core.db;

import com.fanap.fanrp.pat.patemailserver.core.message.EmailAddress;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailData;

import java.util.ArrayList;
import java.util.List;

/**
 * Database DTO for "emails" table.
 * @author Mike Angstadt [mike.angstadt@gmail.com]
 */
public class Email {
	/**
	 * The row primary key.
	 */
	public Integer id;
	
	/**
	 * The "from" address.
	 */
	public EmailAddress sender;
	
	/**
	 * The "to" addresses.
	 */
	public List<EmailAddress> recipients = new ArrayList<EmailAddress>();
	
	/**
	 * The data portion of the email.
	 */
	public EmailData data;
}
