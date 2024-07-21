package com.fanap.fanrp.pat.patemailserver.smtpserver;

import com.fanap.fanrp.pat.patemailserver.TransactionLog;
import com.fanap.fanrp.pat.patemailserver.core.db.Email;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailAddress;
import com.fanap.fanrp.pat.patemailserver.exception.SMTPException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SMTPOutboundConnection {
	private final Socket socket;
	private final SMTPClientSocket client;
	private final String originatingHost;
	private List<String> extensions = new ArrayList<String>();

	public static void main(String args[]) throws Exception {
		SMTPOutboundConnection conn = new SMTPOutboundConnection("mangstadt.dyndns.org", MxRecordResolver.resolveSmtpServers("gmail.com").get(0));
		System.out.println(conn.vrfy("mike.angstadt@gmail.com"));
		
		conn.close();
		System.out.println(conn.getTransactionLog());
	}

	/**
	 * @param originatingHost the name of the originating host
	 * @param remoteHost the address of the SMTP server to connect to
	 * @throws Exception
	 */
	public SMTPOutboundConnection(String originatingHost, String remoteHost) throws SMTPException, IOException {
		this(originatingHost, remoteHost, 25);
	}

	/**
	 * @param originatingHost the name of the originating host
	 * @param remoteHost the address of the SMTP server to connect to
	 * @param port the port to connect to
	 * @throws SMTPException if there was a problem initiating the SMTP
	 * connection
	 * @throws IOException if there was a socket-related problem
	 */
	public SMTPOutboundConnection(String originatingHost, String remoteHost, int port) throws SMTPException, IOException {
		this.originatingHost = originatingHost;

		socket = new Socket(remoteHost, port);
		client = new SMTPClientSocket(socket.getInputStream(), socket.getOutputStream());

		try {
			connect();
		} catch (SMTPException e) {
			try {
				close();
			} catch (Exception e2) {
				//ignore
			}
			throw e;
		} catch (IOException e) {
			try {
				close();
			} catch (Exception e2) {
				//ignore
			}
			throw e;
		}
	}

	/**
	 * Performs the initial SMTP connection commands.
	 * 
	 * @throws SMTPException if an unexpected SMTP message was received
	 * @throws IOException if there was a socket-related problem
	 */
	private void connect() throws SMTPException, IOException {
		SMTPResponse response = client.connect();
		if (response.getStatusCode() == 220) {
			response = client.ehlo(originatingHost);
			if (response.getStatusCode() == 250) {
				extensions.addAll(response.getMessages());
			} else if (response.getStatusCode() == 502) {
				//RPC-5321, p.18: if the server doesn't support "EHLO", send "HELO"
				response = client.helo(originatingHost);
				if (response.getStatusCode() != 250) {
					throw new SMTPException("Unexpected server message: " + response);
				}
			}
		} else if (response.getStatusCode() == 554) {
			//RFC-5321, p.18
			throw new SMTPException(response.toString());
		} else {
			throw new SMTPException("Unexpected server message: " + response);
		}
	}
	
	public SendResult sendEmail(List<EmailAddress> recipients, Email email) throws SMTPException, IOException {
		SendResult sendResult = new SendResult();

		//from field can be blank (see RPC 5321, p.27-8)
		String from = (email.sender == null) ? "" : email.sender.getAddress();
		SMTPResponse response = client.mail(from);

		if (response.getStatusCode() != 250 && response.getStatusCode() != 251 && response.getStatusCode() != 252) {
			throw new SMTPException("Unexpected server message: " + response);
		}

		for (EmailAddress recipient : recipients) {
			response = client.rcpt(recipient.getAddress());
			if (response.getStatusCode() >= 550 && response.getStatusCode() <= 559) {
				//SMTP server didn't like that email address
				sendResult.failedAddresses.add(recipient);
				sendResult.failedAddressesMessages.add(response.toString());
			} else if (response.getStatusCode() != 250){
				throw new SMTPException("Unexpected server message: " + response);
			} else {
				sendResult.successfulAddresses.add(recipient);
			}
		}

		if (sendResult.failedAddresses.size() == recipients.size()) {
			//server didn't like any of the email addresses, so don't sent the email body
			response = client.rset();
		} else {
			response = client.data();
			if (response.getStatusCode() != 354) {
				throw new SMTPException("Unexpected server message: " + response);
			}
			
			response = client.data(email.data.toData());
			if (response.getStatusCode() != 250) {
				throw new SMTPException("Unexpected server message: " + response);
			}
		}
		
		return sendResult;
	}

	/**
	 * Sends an email.
	 * 
	 * @param recipients the email recipients. These are all expected to have a
	 * host name which corresponds to the SMTP host that we're connected to.
	 * Therefore, these emails may only be a subset of the email's entire
	 * recipient list. In the case that the email has recipients with different
	 * hosts, one {@link SMTPOutboundConnection} instance should be created for
	 * each host. For example, if an email is addressed to a "yahoo.com" and a
	 * "gmail.com" address, then one {@link SMTPOutboundConnection} object
	 * should be created for each of these domains.
	 * @param email the email to send
	 * @return the failed recipients
	 * @throws SMTPException if an unexpected SMTP message is encountered
	 * @throws IOException if a socket-related problem occurred
	 */
	public synchronized SendResult sendEmail(List<EmailAddress> recipients, com.fanap.fanrp.pat.patemailserver.core.message.Email email) throws SMTPException, IOException {
		SendResult sendResult = new SendResult();

		//from field can be blank (see RPC 5321, p.27-8)
		String from = (email.getEmailRaw().getMailFrom() == null) ? "" : email.getEmailRaw().getMailFrom().getAddress();
		SMTPResponse response = client.mail(from);

		if (response.getStatusCode() != 250 && response.getStatusCode() != 251 && response.getStatusCode() != 252) {
			throw new SMTPException("Unexpected server message: " + response);
		}

		for (EmailAddress recipient : recipients) {
			response = client.rcpt(recipient.getAddress());
			if (response.getStatusCode() >= 550 && response.getStatusCode() <= 559) {
				//SMTP server didn't like that email address
				sendResult.failedAddresses.add(recipient);
				sendResult.failedAddressesMessages.add(response.toString());
			} else if (response.getStatusCode() != 250){
				throw new SMTPException("Unexpected server message: " + response);
			}
		}

		if (sendResult.failedAddresses.size() == recipients.size()) {
			//server didn't like any of the email addresses, so don't sent the email body
			response = client.rset();
		} else {
			response = client.data();
			if (response.getStatusCode() != 354) {
				throw new SMTPException("Unexpected server message: " + response);
			}
			response = client.data(email.getData().toData());
			if (response.getStatusCode() != 250) {
				throw new SMTPException("Unexpected server message: " + response);
			}
		}
		
		return sendResult;
	}

	/**
	 * Verifies whether the host recognizes an email address or not.
	 * 
	 * @param address the address to verify
	 * @return the response message
	 * @throws SMTPException if an unexpected SMTP message was received
	 * @throws IOException if a socket-related error occurred
	 */
	public synchronized String vrfy(String address) throws SMTPException, IOException {
		SMTPResponse response = client.vrfy(address);
		if (response.getStatusCode() >= 250 || response.getStatusCode() <= 259) {
			return response.getMessage();
		} else if (response.getStatusCode() == 553) {
			//ambiguous
			StringBuilder sb = new StringBuilder();
			for (String message : response.getMessages()) {
				sb.append(message);
				sb.append('\n');
			}
			return sb.toString();
		} else {
			throw new SMTPException("Unexpected server message: " + response);
		}
	}

	/**
	 * Gets the emails that make up a mailing list.
	 * 
	 * @param mailingList the mailing list address
	 * @return the server response
	 * @throws SMTPException if an unexpected SMTP message was received
	 * @throws IOException if a socket-related error occurred
	 */
	public synchronized List<String> expn(String mailingList) throws SMTPException, IOException {
		SMTPResponse response = client.expn(mailingList);
		if (response.getStatusCode() == 250) {
			List<String> emails = new ArrayList<String>();
			for (String message : response.getMessages()) {
				emails.add(message);
			}
			return emails;
		} else {
			throw new SMTPException("Unexpected server message: " + response);
		}
	}
	
	public TransactionLog getTransactionLog(){
		return client.getTransactionLog();
	}

	/**
	 * Closes the SMTP connection.
	 * 
	 * @throws SMTPException if an unexpected SMTP message was received
	 * @throws IOException if a socket-related error occurred
	 */
	public synchronized void close() throws SMTPException, IOException {
		try {
			SMTPResponse response = client.quit();
			if (response.getStatusCode() != 221) {
				throw new SMTPException("Unexpected server message when trying to send QUIT: \"" + response + "\"");
			}
		} finally {
			IOUtils.closeQuietly(socket);
		}
	}
}