package com.fanap.fanrp.pat.patemailserver.exception;

import com.fanap.fanrp.pat.patemailserver.pop3server.POP3Response;

@SuppressWarnings("serial")
public class IMAPErrorException extends Exception {
	private final POP3Response response;

	public IMAPErrorException(POP3Response response) {
		super(response.getMessage());
		this.response = response;
	}

	public POP3Response getResponse() {
		return response;
	}
}
