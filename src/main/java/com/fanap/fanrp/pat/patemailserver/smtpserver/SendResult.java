package com.fanap.fanrp.pat.patemailserver.smtpserver;

import com.fanap.fanrp.pat.patemailserver.core.message.EmailAddress;

import java.util.ArrayList;
import java.util.List;


public class SendResult {
	/**
	 * The addresses that could not be delivered to.
	 */
	public final List<EmailAddress> failedAddresses = new ArrayList<EmailAddress>();

	/**
	 * The addresses that successfully received the email.
	 */
	public final List<EmailAddress> successfulAddresses = new ArrayList<EmailAddress>();

	/**
	 * The specific error messages that the server returned for the failed
	 * addresses.
	 */
	public final List<String> failedAddressesMessages = new ArrayList<String>();
}
