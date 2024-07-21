package com.fanap.fanrp.pat.patemailserver.core.db;

import java.util.ArrayList;
import java.util.List;

public class MailingList {
	public int id;
	
	public String name;
	
	public List<MailingListAddress> addresses = new ArrayList<MailingListAddress>();
}
