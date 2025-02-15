package com.fanap.fanrp.pat.patemailserver.smtpserver;

import com.fanap.fanrp.pat.patemailserver.Runner;
import com.fanap.fanrp.pat.patemailserver.async.AsyncMessageSender;
import com.fanap.fanrp.pat.patemailserver.core.db.*;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailAddress;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailData;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailDateFormat;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailRaw;
import com.fanap.fanrp.pat.patemailserver.dto.PATLetterDTO;
import com.fanap.fanrp.pat.patemailserver.security.User;
import com.fanap.fanrp.pat.patemailserver.security.UserManagement;
import com.fanap.fanrp.pat.patemailserver.utils.DTOConvertor;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SMTPConnectionListener {
	private static final Logger logger = Logger.getLogger(SMTPConnectionListener.class.getName());

	/**
	 * The database DAO.
	 */
	private final DbDao dao;

	/**
	 * True if this is a Mail Transfer Agent (MTA, an SMTP server that accepts
	 * incoming mail from the Internet), false if this listener is a Mail
	 * Submission Agent (MSA, an SMTP server that accepts outbound mail to send
	 * to the Internet).
	 */
	private final boolean mta;

	/**
	 * The host name of this server.
	 */
	private String hostName;

	/**
	 * The port to listen for SMTP connections.
	 */
	private int port;

	/**
	 * True if the listener has started, false if not.
	 */
	private boolean started = false;

	/**
	 * All client/server communication is logged here for debugging purposes
	 * (null to not log anything).
	 */
	private File transactionLogFile;

	/**
	 * Sends outbound emails. Should only be set if this object is a MSA.
	 */
	private MailSender mailSender;

	/**
	 * Constructor for creating an MTA (mail transfer agent) server that accepts
	 * incoming emails from the Internet.
	 * @param dao the database DAO
	 */
	public SMTPConnectionListener(DbDao dao) {
		this.dao = dao;
		mta = true;
		port = 25;
	}

	/**
	 * Constructor for creating an MSA (mail submission agent) server that
	 * accepts outbound emails to send out to the Internet.
	 * @param dao the database DAO
	 * @param mailSender sends the emails
	 */
	public SMTPConnectionListener(DbDao dao, MailSender mailSender) {
		this.dao = dao;
		this.mailSender = mailSender;
		mta = false;
		port = 587;
	}

	/**
	 * Sets the host name of the server.
	 * @param hostName the host name
	 * @throws IllegalStateException if the listener has already been started
	 * @throws IllegalArgumentException if hostName is null
	 */
	public void setHostName(String hostName) {
		if (started) {
			throw new IllegalStateException("Server properties cannot be changed once the server starts.");
		}
		if (hostName == null) {
			throw new IllegalArgumentException("Host name cannot be null.");
		}
		this.hostName = hostName;
	}

	/**
	 * Sets the port of the server.
	 * @param port the port (defaults to 25)
	 * @throws IllegalStateException if the listener has already been started
	 * @throws IllegalArgumentException if port is not a positive integer
	 */
	public void setPort(int port) {
		if (started) {
			throw new IllegalStateException("Server properties cannot be changed once the server starts.");
		}
		if (port < 1) {
			throw new IllegalArgumentException("Port must be a postive integer.");
		}
		this.port = port;
	}

	/**
	 * Sets the file that all client/server communication will be logged to.
	 * @param transactionLogFile the file or null not to log anything (default)
	 * @throws IllegalStateException if the listener has already been started
	 */
	public void setTransactionLogFile(File transactionLogFile) {
		if (started) {
			throw new IllegalStateException("Transaction log file cannot be changed once the server starts.");
		}
		this.transactionLogFile = transactionLogFile;
	}

	/**
	 * Starts the SMTP server.
	 * @throws IOException
	 * @throws IllegalStateException if the server has not been configured
	 * properly and cannot start
	 */
	public void start() throws IOException {
		if (hostName == null) {
			throw new IllegalStateException("Host name must be set.");
		}

		started = true;

		ServerSocket serverSocket = new ServerSocket(port);

		logger.info("Ready to receive SMTP " + (mta ? "MTA" : "MSA") + " requests on port " + port + "...");

		while (true) {
			Socket socket = serverSocket.accept();
			logger.info("SMTP " + (mta ? "MTA" : "MSA") + " connection established with " + socket.getInetAddress().getHostAddress());
			SMTPClientThread thread = new SMTPClientThread(socket);
			thread.start();
		}
	}

	/**
	 * Handles a single client connection.
	 * @author Mike Angstadt [mike.angstadt@gmail.com]
	 */
	private class SMTPClientThread extends Thread {
		private final Socket socket;
		private final SMTPServerSocket serverSocket;

		public SMTPClientThread(Socket socket) throws IOException {
			this.socket = socket;
			serverSocket = new SMTPServerSocket(socket.getInputStream(), socket.getOutputStream());
		}

		@Override
		public void run() {
			try {
				Pattern fromPattern = Pattern.compile("FROM:<(.*?)>", Pattern.CASE_INSENSITIVE);
				Pattern toPattern = Pattern.compile("TO:<(.*?)>", Pattern.CASE_INSENSITIVE);

				boolean ehloSent = false;
				String remoteHostName = null;
				EmailRaw email = null;
				serverSocket.sendResponse(220, hostName + " " + Runner.appName + " v" + Runner.version + " Ready to receive mail.");
				SMTPRequest clientMsg;
				User authenticatedUser = null;
				while ((clientMsg = serverSocket.nextRequest()) != null) {
					System.out.println("SMTP MSG: " + clientMsg.toString());
					ClientCommand cmd;
					try {
						cmd = ClientCommand.valueOf(clientMsg.getCommand());
					} catch (IllegalArgumentException e) {
						cmd = null;
					}
					String params = clientMsg.getParameters();

					if (cmd == ClientCommand.HELP) {
						List<String> msgs = new ArrayList<String>();
						msgs.add("List of available commands:");
						if (email == null) {
							if (ehloSent) {
								if (!mta && authenticatedUser == null) {
									msgs.add("AUTH PLAIN base64(username/password) - Call this to authenticate with the server.");
								} else {
									msgs.add("MAIL FROM:<sender address> - The sender of the email.");
								}
							} else {
								msgs.add("EHLO <client host name> - Call this to start sending emails.");
							}
						} else {
							msgs.add("RCPT TO:<recipient address> - A recipient of the email.");
							if (!email.getRecipients().isEmpty()) {
								msgs.add("DATA - Start sending the email message (end with <CRLF>.<CRLF>).");
							}
							msgs.add("RSET - Cancel this email and start over.");
						}
						msgs.add("VRFY <email address> - Check to see if this address exists on this server.");
						msgs.add("EXPN <mailing list address> - Get the recipients of a mailing list.");
						msgs.add("NOOP - Does nothing (always returns 250 Ok response)");
						msgs.add("HELP - Display list of available commands (varies depending on context).");
						msgs.add("QUIT - End this SMTP session.");
						serverSocket.sendResponse(214, msgs);
					} else if (cmd == ClientCommand.HELO) {
						remoteHostName = params;
						serverSocket.sendResponse(250, "Hello " + params);
						ehloSent = true;
						email = null;
					} else if (cmd == ClientCommand.EHLO) {
						remoteHostName = params;
						ehloSent = true;
						List<String> messages = new ArrayList<String>();
						messages.add(hostName + " Hello" + (params == null ? "" : " " + params));
						messages.add("SIZE 1000000");
						if (!mta){
							messages.add("AUTH PLAIN");
						}
						messages.add("HELP");
						//TODO extended status codes should be supported by MSAs (RFC-6409, p.12)
						//TODO piplining should be supported by MSAs (RFC-6409, p.13)
						//messages.add("EXPN");
						//TODO RFC-5321 p.25 - say that EXPN is supported
						serverSocket.sendResponse(250, messages);
						email = null;
					} else if (cmd == ClientCommand.AUTH && !mta) {
						if (!ehloSent) {
							serverSocket.sendResponse(503, "EHLO required before authentication.");
							continue;
						}

						if (authenticatedUser != null) {
							//see RFC-4954, p.3
							serverSocket.sendResponse(503, "Already authenticated.");
							continue;
						}

						if (params == null || params.isEmpty()) {
							serverSocket.sendResponse(501, "Invalid syntax for AUTH command.");
							continue;
						}

						String split[] = params.split(" ");
						String authMech = split[0];
						if ("PLAIN".equalsIgnoreCase(authMech)) {
							//see RFC-4954, p.7-8

							//the auth string that the client sends is a base64-encoded string "username@password"
							//TODO this probably isn't correct, wanted to get something working
							String authStr;
							if (split.length > 1) {
								//the auth string is on the same line as the AUTH command
								//see RFC-4964, p.7
								authStr = split[1];
							} else {
								//the auth string is on the next line, after the AUTH command
								//see RFC-4964, p.8
								serverSocket.sendResponse(334, " ");
								authStr = serverSocket.nextLine();
							}
							authStr = new String(Base64.decodeBase64(authStr));

							int slash = authStr.indexOf('/');
							if (slash == -1 || slash == authStr.length() - 1) {
								serverSocket.sendResponse(535, "Authentication credentials invalid.");
							} else {
								String username = authStr.substring(0, slash);
								String password = authStr.substring(slash + 1);

								User user = dao.selectUser(username, password);
								if (user == null) {
									serverSocket.sendResponse(535, "Authentication credentials invalid.");
								} else {
									authenticatedUser = user;
									serverSocket.sendResponse(235, "Authentication successful.");
								}
							}
						} else {
							serverSocket.sendResponse(501, "The " + authMech + " authentication mechanism is not supported.");
						}
					} else if (cmd == ClientCommand.RSET) {
						if (params != null) {
							//this command does not have parameters (see RFC 5321 p.55)
							serverSocket.sendResponse(501, "RSET command has no parameters.");
							continue;
						}

						String msg;
						if (email == null) {
							msg = "Ok";
						} else {
							email = null;
							msg = "Ok, email transaction aborted.";
						}
						serverSocket.sendResponse(250, msg);
					} else if (cmd == ClientCommand.NOOP) {
						serverSocket.sendResponse(250, "Ok");
					} else if (cmd == ClientCommand.VRFY) {
						//RFC-5321, p.14: the address "postmaster" is valid, even though it does not have a host associated with it
						if ("postmaster".equalsIgnoreCase(params)) {
							//append host name
							params = "postmaster@" + hostName;
						}

						EmailAddress addr = new EmailAddress(params);
						if (addr.isValid()) {
							//if an email address was given, then check the host and then see if the user exists

							//check host
							String host = addr.getHost();
							if (!hostName.equalsIgnoreCase(host)) {
								serverSocket.sendResponse(551, "Invalid host name: " + addr);
								continue;
							}

							//check to see if username exists
							String username = addr.getMailbox();
							User user = dao.selectUser(username);
							if (user == null) {
								serverSocket.sendResponse(550, "User not found with given address: " + addr);
							} else {
								String msg;
								if (user.fullName == null) {
									msg = user.username + "@" + hostName;
								} else {
									msg = user.fullName + " <" + user.username + "@" + hostName + ">";
								}
								serverSocket.sendResponse(250, msg);
							}
						} else {
							//search usernames and full names of users

							List<User> users = dao.findUsers(params);
							if (users.isEmpty()) {
								serverSocket.sendResponse(550, "No users found: " + params);
							} else {
								List<String> msgs = new ArrayList<String>();
								msgs.add("User ambiguous.  Possibilities are:");
								for (User user : users) {
									String msg = "";
									if (user.fullName != null) {
										msg += user.fullName + " ";
									}
									msg += "<" + user.username + "@" + hostName + ">";
									msgs.add(msg);
								}

								//always use "ambiguous" status code because it's not clear whether the client is searching for a username or the person's real name
								serverSocket.sendResponse(553, msgs);
							}
						}
					} else if (cmd == ClientCommand.EXPN) {
						String name = params;
						if (name.isEmpty()) {
							serverSocket.sendResponse(501, "No mailing list specified.");
							continue;
						}

						MailingList mailingList = dao.selectMailingList(name);
						if (mailingList == null || mailingList.addresses.isEmpty()) {
							serverSocket.sendResponse(550, "Mailing list doesn't exist: " + name);
						} else {
							List<String> lines = new ArrayList<String>();
							for (MailingListAddress address : mailingList.addresses) {
								String msg;
								if (address.name == null) {
									msg = address.address;
								} else {
									msg = address.name + " <" + address.address + ">";
								}
								lines.add(msg);
							}
							serverSocket.sendResponse(250, lines);
						}
					} else if (cmd == ClientCommand.MAIL) {
						//TODO "from" address can be empty, RFC-6409 p.7
						if (!ehloSent) {
							serverSocket.sendResponse(503, "EHLO required before emails can be received.");
							continue;
						}

						if (!mta && authenticatedUser == null) {
							//user must authenticate first
							//see RFC-6409, p.10,12
							serverSocket.sendResponse(530, "You must authenticate first.");
							continue;
						}

						//get "from" address
						Matcher m = fromPattern.matcher(params);
						if (m.find()) {
							if (email != null && email.getMailFrom() != null) {
								serverSocket.sendResponse(503, "\"From\" address already specified.  Use RCPT to define recipients and DATA to define the message body.");
								continue;
							}

							String addrStr = m.group(1);
							EmailAddress addr = new EmailAddress(addrStr);
							if (!addr.isValid()) {
								serverSocket.sendResponse(501, "Invalid syntax of email address: " + addrStr);
								continue;
							}

							if (!mta) {
								if (!hostName.equals(addr.getHost()) || !authenticatedUser.username.equals(addr.getMailbox())){
									//user can only send emails that are from herself
									//see RFC-6409, p.10
									serverSocket.sendResponse(550, "The \"from\" address must be from *your* email account.");
									continue;
								}
							}

							email = new EmailRaw();
							email.setMailFrom(addr);

							serverSocket.sendResponse(250, "Ok");
						} else {
							serverSocket.sendResponse(501, "MAIL command must look like: \"MAIL FROM:<mailbox@host>\"");
						}
					} else if (cmd == ClientCommand.RCPT) {
						if (email == null) {
							serverSocket.sendResponse(503, "MAIL command must be used before RCPT can be used");
							continue;
						}

						if (!mta && authenticatedUser == null) {
							//user mus	t authenticate first
							//see RFC-6409, p.10,12
							serverSocket.sendResponse(530, "You must authenticate first.");
							continue;
						}

						Matcher m = toPattern.matcher(params);
						if (m.find()) {
							String addrStr = m.group(1);

							//RFC-5321, p.14: the address "postmaster" is valid, even though it does not have a host associated with it
							if ("postmaster".equalsIgnoreCase(addrStr)) {
								//append host name
								addrStr = "postmaster@" + hostName;
							}

							EmailAddress addr = new EmailAddress(addrStr);

							//check syntax
							if (!addr.isValid()) {
								serverSocket.sendResponse(501, "Invalid syntax of email address: " + addrStr);
								continue;
							}

							if (mta) {
								//check host name
								String host = addr.getHost();
								/*if (!hostName.equalsIgnoreCase(host)) {
									serverSocket.sendResponse(551, "Invalid host name: " + host);
									continue;
								}*/

								//check mailbox
								//TDOD: Remove Comment
								String mailbox = addr.getMailbox();
								/*if (!dao.doesMailboxExist(mailbox)) {
									serverSocket.sendResponse(550, "Mailbox not found: " + mailbox);
									continue;
								}*/
							}

							email.addRecipient(addr);
							serverSocket.sendResponse(250, "Ok");
						} else {
							//invalid syntax
							serverSocket.sendResponse(501, "RCPT command must look like: \"RCPT TO:<mailbox@" + hostName + ">\"");
							continue;
						}
					} else if (cmd == ClientCommand.DATA) {
						if (email == null) {
							serverSocket.sendResponse(503, "MAIL command must be used before DATA can be sent.");
							continue;
						}

						if (params != null) {
							//this command does not have parameters (see RFC 5321 p.55)
							serverSocket.sendResponse(501, "DATA command has no parameters.");
							continue;
						}

						if (email.getRecipients().isEmpty()) {
							serverSocket.sendResponse(503, "At least one RCPT (recipient) is required before DATA can be sent.");
							continue;
						}

						//get mail message body
						serverSocket.sendResponse(354, "Ready.");

						StringBuilder data = new StringBuilder();
						String dataLine;
						while ((dataLine = serverSocket.nextDataLine()) != null) {
							data.append(dataLine).append(EmailRaw.CRLF);
						}
						email.setData(new EmailData(data.toString()));
						DTOConvertor dtoConvertor = new DTOConvertor(email);
						PATLetterDTO letterDTO = dtoConvertor.getLetterDTO();

						EmailAddress senderAddress = email.getData().getHeaders().getFrom().getAddresses().get(0);
						String token = UserManagement.getToken(senderAddress.getAddress());
						AsyncMessageSender.bpCallRequest(letterDTO, "PAT_BP_EmailManagment","_4",
								"tfa.ow", token);


						if (mta) {
							//receiving email from the Internet

							DateFormat df = new EmailDateFormat();

							//trace info--from clause
							//existing "Received" headers in the email must not be modified or removed
							// see RFC 5321 p.57
							String remoteIp = socket.getInetAddress().getHostAddress();
							StringBuilder sb = new StringBuilder();
							sb.append("from ");
							if (remoteHostName == null) {
								sb.append(remoteIp);
							} else {
								sb.append(remoteHostName + " ([" + remoteIp + "])");
							}
							sb.append(" by " + hostName + "; " + df.format(new Date()));
							email.getData().getHeaders().addHeader("Received", sb.toString());
							email.getData().getHeaders().addHeader("Return-Path", "<" + email.getMailFrom().getAddress() + ">");

							//add mail message to database
							Exception error = null;
							Email dbEmail = null;
							synchronized (dao) {
								try {
									dbEmail = new Email();
									dbEmail.sender = email.getMailFrom();
									dbEmail.recipients = email.getRecipients();
									dbEmail.data = email.getData();
									dao.insertInboxEmail(dbEmail);
									dao.commit();
								} catch (Exception e) {
									dao.rollback();

									error = e;
									logger.log(Level.SEVERE, "Error saving email to database.", e);
								}
							}

							if (error == null) {
								serverSocket.sendResponse(250, "Ok: queued as " + dbEmail.id);
							} else {
								serverSocket.sendResponse(451, "An unexpected server error occurred while saving the email, sorry: " + error.getMessage());
							}
						} else { //if (!mta)
							//sending email to the Internet
							try {
								mailSender.sendEmail(email);
								serverSocket.sendResponse(250, "Ok: queued for sending.");
							} catch (SQLException e) {
								serverSocket.sendResponse(451, "An unexpected server error occurred while sending the email, sorry: " + e.getMessage());
							}
						}
						email = null;
					} else if (cmd == ClientCommand.QUIT) {
						if (params != null) {
							//this command does not have parameters (see RFC 5321 p.55)
							serverSocket.sendResponse(501, "QUIT command has no parameters.");
							continue;
						}

						String msg;
						if (email == null) {
							msg = "Bye";
						} else {
							msg = "Email transaction aborted.  Bye";
						}
						serverSocket.sendResponse(221, msg);
						break;
					} else {
						serverSocket.sendResponse(500, "Unknown command: " + clientMsg.getCommand());
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "SMTP error.", e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, "Problem closing socket.", e);
				}

				//write transaction log to file
				if (transactionLogFile != null) {
					synchronized (transactionLogFile) {
						try {
							serverSocket.getTransactionLog().writeToFile(transactionLogFile);
						} catch (IOException e) {
							logger.log(Level.WARNING, "Problem writing to transaction log file.", e);
						}
					}
				}

				logger.info("SMTP connection with " + socket.getInetAddress().getHostAddress() + " terminated.");
			}
		}
	}
}