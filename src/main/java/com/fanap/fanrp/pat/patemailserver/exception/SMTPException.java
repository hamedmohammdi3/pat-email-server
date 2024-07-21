package com.fanap.fanrp.pat.patemailserver.exception;

/**
 * Thrown when the client or server receives an unexpected SMTP message.
 * @author Mike Angstadt [mike.angstadt@gmail.com]
 */
@SuppressWarnings("serial")
public class SMTPException extends RuntimeException {
	public SMTPException(String msg){
		super(msg);
	}
}
