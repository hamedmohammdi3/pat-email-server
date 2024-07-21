package com.fanap.fanrp.pat.patemailserver.imapserver;

import com.fanap.fanrp.pat.patemailserver.TransactionLog;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.fanap.fanrp.pat.patemailserver.core.message.EmailRaw.CRLF;

public class IMAPServerSocket {
	private static final Logger logger = Logger.getLogger(IMAPServerSocket.class.getName());

	/**
	 * Input stream from the client.
	 */
	private final BufferedReader fromClient;

	/**
	 * Output stream to the client.
	 */
	private final PrintWriter toClient;

	/**
	 * Records conversation for logging purposes.
	 */
	private final TransactionLog transactionLog = new TransactionLog();

	/**
	 * @param fromClient the input stream from the client.
	 * @param toClient the output stream to the client.
	 */
	public IMAPServerSocket(InputStream fromClient, OutputStream toClient) {
		this.fromClient = new BufferedReader(new InputStreamReader(fromClient));
		this.toClient = new PrintWriter(toClient);
	}

	/**
	 * Sends a success ("+OK") response without any message text.
	 */
	/*public void sendSuccess() {
		sendSuccess(new ArrayList<String>(0));
	}*/

	/**
	 * Sends a success ("+OK") response.
	 * @param msg the message text
	 */
	/*public void sendSuccess(String msg) {
		//String split[] = msg.split("\\r\\n|\\n");
		sendSuccess(msg);
	}*/

	/**
	 * Sends a multi-lined success ("+OK") response.
	 */
	public void sendSuccess(String response) {
		/*StringBuilder sb = new StringBuilder();
		sb.append("+OK");

		if (!lines.isEmpty()) {
			sb.append(" " + lines.get(0));
			if (lines.size() > 1) {
				for (int i = 1; i < lines.size(); i++) {
					String msg = lines.get(i);

					//add an extra "." to the beginning of all lines that start with "." (called "dot-stuffing")
					if (msg.startsWith(".")) {
						msg = "." + msg;
					}

					sb.append(CRLF + msg);
				}
				sb.append(CRLF).append(".");
			}
		}

		sb.append(CRLF);

		String response = sb.toString();*/
		toClient.print(response + "\r\n");
		toClient.flush();

		transactionLog.server(response);
	}

	/**
	 * Sends an error ("-ERR") response.
	 * @param msg the error message
	 * @throws IOException
	 */
	public void sendError(String msg) throws IOException {
		String response = "-ERR " + msg + CRLF;
		toClient.print(response);
		toClient.flush();
		transactionLog.server(response);
	}

	/**
	 * Gets the next request from the client.
	 * @return
	 * @throws IOException
	 */
	public IMAPRequest nextRequest() throws IOException {
		String line = fromClient.readLine();
		if (line == null) {
			return null;
		}
		transactionLog.client(line);
		line = line.trim();

		String commandId = null;
		String command;
		String message = null;
		int space = line.indexOf(' ');
		if (space == -1) {
			command = line;
		} else {
			commandId = line.substring(0, space);
			command = line.substring(space + 1);
			space = command.indexOf(' ');
			if (space > -1){
				message = command.substring(space + 1);
				command = command.substring(0, space);
			}
		}
		command = command.toUpperCase();

		return new IMAPRequest(commandId, line, command, message);
	}

	/**
	 * Closes the connection.
	 */
	public void close() {
		try {
			fromClient.close();
			toClient.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Problem closing input stream from IMAP client.", e);
		}

	}

	public TransactionLog getTransactionLog() {
		return transactionLog;
	}
}
