package com.fanap.fanrp.pat.patemailserver.core.db;

import com.fanap.fanrp.pat.patemailserver.core.message.EmailAddress;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class OutboundEmailGroup {
	public Integer id;
	public String host;
	public Email email;
	public List<EmailAddress> recipients = new LinkedList<EmailAddress>();
	public int attempts = 0;
	public Date firstAttempt = null;
	public Date prevAttempt = null;
	public List<String> failures = new ArrayList<String>();
}
