package com.fanap.fanrp.pat.patemailserver.imapserver;

import static com.fanap.fanrp.pat.patemailserver.core.message.EmailRaw.CRLF;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fanap.fanrp.pat.patemailserver.security.UserManagement;
import com.fanap.fanrp.pat.patemailserver.utils.EmailConvertor;
import org.apache.commons.codec.digest.DigestUtils;

import com.fanap.fanrp.pat.patemailserver.core.db.DbDao;
import com.fanap.fanrp.pat.patemailserver.core.db.POPEmail;
import com.fanap.fanrp.pat.patemailserver.security.User;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailData;
/**
 * Listens for and handles IMAP client connections. Forks each client/server
 * connection into its own thread. 
 */
public class IMAPConnectionListener {
	private static final Logger logger = Logger.getLogger(IMAPConnectionListener.class.getName());
	private final DbDao dao;
	private boolean started = false;
	private int port = 110;
	private String hostName;
	private File transactionLogFile;
	//private final Map<String, User> loggedInUsers = Collections.synchronizedMap(new HashMap<String, User>());

	public IMAPConnectionListener(DbDao dao) {
		this.dao = dao;
	}

	public void setPort(int port) {
		if (started) {
			throw new IllegalStateException("IMAP server properties cannot be changed once the server starts.");
		}
		if (port < 1) {
			throw new IllegalArgumentException("Port must be a postive integer.");
		}
		this.port = port;
	}

	public void setHostName(String hostName) {
		if (started) {
			throw new IllegalStateException("IMAP server properties cannot be changed once the server starts.");
		}
		if (hostName == null) {
			throw new IllegalArgumentException("Host name cannot be null.");
		}
		this.hostName = hostName;
	}

	public void setTransactionLogFile(File transactionLogFile) {
		if (started) {
			throw new IllegalStateException("Transaction log file cannot be changed once the server starts.");
		}
		this.transactionLogFile = transactionLogFile;
	}

	public void start() throws IOException {
		if (hostName == null) {
			throw new IllegalStateException("Host name must be set.");
		}

		started = true;

		ServerSocket serverSocket = new ServerSocket(port);
		logger.info("Ready to receive IMAP requests on port " + port + "...");

		while (true) {
			Socket socket = serverSocket.accept();
			logger.info("IMAP connection established with " + socket.getInetAddress().getHostAddress());
			IMAPConversation thread = new IMAPConversation(socket);
			thread.start();
		}
	}

	private class IMAPConversation extends Thread {
		private final Socket socket;
		private final IMAPServerSocket serverSocket;
		private List<POPEmail> popEmails;
		private User currentUser;
		private boolean authenticated = false;
		//int cmdId = 0;

		public IMAPConversation(Socket socket) throws IOException {
			this.socket = socket;
			this.serverSocket = new IMAPServerSocket(socket.getInputStream(), socket.getOutputStream());
		}

		@Override
		public void run() {
			try {
				/*BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
*/				String message = "";
				serverSocket.sendSuccess("* OK IMAP4rev1 Service Ready");

				IMAPRequest imapRequest;
				while ((imapRequest = serverSocket.nextRequest()) != null) {
					String cmdId = imapRequest.getCommandId();
					String cmd = imapRequest.getCommand();
					String params = imapRequest.getParameters();
					System.out.println("IMAP MSG: " + cmdId + " | " + cmd + " | " + params);

					switch (cmd){
						case IMAPCommands.CAPABILITY:
							serverSocket.sendSuccess("* CAPABILITY IMAP4rev1");
//							serverSocket.sendSuccess("* CAPABILITY IMAP4");
							serverSocket.sendSuccess(cmdId + " OK CAPABILITY completed");
							break;
						case IMAPCommands.LOGOUT:
							serverSocket.sendSuccess("+ BYE IMAP4 Server logging out");
							serverSocket.sendSuccess(cmdId + " OK LOGOUT completed");
							try {
								socket.close();
							} catch (IOException e) {
								logger.log(Level.WARNING, "Problem closing socket.");
							}
							break;
						case IMAPCommands.LOGIN:
							String[] auth = params.split(" ");
							serverSocket.sendSuccess(cmdId + " OK LOGIN completed");
							break;
						case IMAPCommands.LIST:
							String[] list = params.split(" ");
							if (Arrays.stream(list).filter(i->i.equals("\"*\"")).count() == 0){
								serverSocket.sendSuccess("* LIST (HasNoChildren) \"/\" " + list[1]);
							}else {
//								serverSocket.sendSuccess("* LIST (HasNoChildren) \"/\" \"INBOX\"");
								serverSocket.sendSuccess("* LIST () \"/\" \"Trash\"");
								serverSocket.sendSuccess("* LIST () \"/\" \"folder-name-1/folder-name-1.1\"");
								serverSocket.sendSuccess("* LIST () \"/\" \"folder-name-12\"");
								serverSocket.sendSuccess("* LIST () \"/\" \"folder-name-10\"");
								serverSocket.sendSuccess("* LIST () \"/\" \"folder-name-۰۵\"");
								serverSocket.sendSuccess("* LIST () \"/\" \"folder-name-88\"");
							}
							serverSocket.sendSuccess(cmdId + " OK LIST Completed");
							//serverSocket.sendSuccess(cmdId + " * 23 FETCH (FLAGS (\\INBOX) SIZE 44827)");
							break;
						case IMAPCommands.LSUB: {
							String[] mailBox = params.split(" ");
							if (Arrays.stream(mailBox).filter(i->i.equals("\"*\"")).count() == 0) {
								serverSocket.sendSuccess("* LSUB () \".\" #" + mailBox[0]);
							}else{
								serverSocket.sendSuccess("* LSUB () \"/\" \"Trash\"");
								serverSocket.sendSuccess("* LSUB () \"/\" \"folder-name-1/folder-name-1.1\"");
								serverSocket.sendSuccess("* LSUB () \"/\" \"folder-name-12\"");
								serverSocket.sendSuccess("* LSUB () \"/\" \"folder-name-10\"");
								serverSocket.sendSuccess("* LSUB () \"/\" \"folder-name-۰۵\"");
								serverSocket.sendSuccess("* LSUB () \"/\" \"folder-name-88\"");
							}
							serverSocket.sendSuccess(cmdId + " OK LSUB completed");
							break;
						}
						case IMAPCommands.SELECT:
							String[] mailBox = params.split(" ");
							serverSocket.sendSuccess("* 4 EXISTS");
							serverSocket.sendSuccess("* FLAGS (\\Answered \\Flagged \\Deleted \\Seen \\Draft)");
							serverSocket.sendSuccess("* 2 RECENT");
							serverSocket.sendSuccess("* OK [UNSEEN 2] Message 2 is the first unseen message");
							serverSocket.sendSuccess("* OK [UIDVALIDITY 3857529045] UIDs valid");
							serverSocket.sendSuccess(cmdId + " OK [READ-WRITE] SELECT completed");
							break;
						case IMAPCommands.CREATE: {
							String folderName = params;
							serverSocket.sendSuccess(cmdId + " OK CREATE completed");
							break;
						}
						case IMAPCommands.SUBSCRIBE:
							serverSocket.sendSuccess(cmdId + " OK SUBSCRIBE completed");
							break;
						case IMAPCommands.UNSUBSCRIBE:
							serverSocket.sendSuccess(cmdId + " OK UNSUBSCRIBE completed");
							break;
						case IMAPCommands.UID:
							String[] args = params.split(" ");

						if ("(FLAGS)".equals(args[2])) {
								serverSocket.sendSuccess("* 1 FETCH (UID 1  FLAGS (\\Seen))");
								serverSocket.sendSuccess("* 2 FETCH (UID 2  FLAGS (\\Seen))");
//							serverSocket.sendSuccess("* 44 EXPUNGE");
							}else{
								serverSocket.sendSuccess("* 1 FETCH (BODY[HEADER.FIELDS (SUBJECT, TO)] {1}\n" +
										"Subject: Test message "+System.currentTimeMillis() +" Subject \n" +
										"To: <someuser@example.atmailcloud.com>\n" +
										"Reply-To: \"Some User\" <someuser@example.atmailcloud.com>\n" +
										"From: \"Some User\" <someuser@example.atmailcloud.com>\n" +
										"\n)");
							serverSocket.sendSuccess("* 2 FETCH (BODY[HEADER.FIELDS (SUBJECT, TO)] {2}\n" +
									"Subject: Test message "+System.currentTimeMillis() +" Subject \n" +
									"To: <someuser@example.atmailcloud.com>\n" +
									"Reply-To: \"Some User\" <someuser@example.atmailcloud.com>\n" +
									"From: \"Some User\" <someuser@example.atmailcloud.com>\n" +
									"\n)");

							}
							serverSocket.sendSuccess(cmdId + " OK FETCH completed");
							break;
						case IMAPCommands.NOOP:
							serverSocket.sendSuccess(cmdId + " OK NOOP completed");
							break;
					}
				}


			} catch ( Exception e) {
				//throw new RuntimeException(e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, "Problem closing socket.");
				}

			}
		}

		/**
		 * Gets info on an email, sending an error response if there's a problem
		 * with the input supplied by the client.
		 * @param popIdStr the POP email ID (client input)
		 * @return the email or null if there was an error with the input
		 * @throws IOException
		 */
		private POPEmail getPOPEmail(String popIdStr) throws IOException {
			int popId = 0;
			try {
				popId = Integer.parseInt(popIdStr);
				if (popId <= 0) {
					throw new NumberFormatException();
				} else if (popId > popEmails.size()) {
					serverSocket.sendError("No such message.");
					return null;
				}
			} catch (NumberFormatException e) {
				serverSocket.sendError("Invalid email ID " + popIdStr + ".");
				return null;
			}

			POPEmail email = popEmails.get(popId - 1);
			return email;
		}
	}
}
