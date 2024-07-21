package com.fanap.fanrp.pat.patemailserver.core.admin;

import com.fanap.fanrp.pat.patemailserver.TransactionLog;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailRaw;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface used by the Sleet admin console.
 * @author Mike Angstadt [mike.angstadt@gmail.com]
 */
public class AdminServerSocket {
	private static final Logger logger = Logger.getLogger(AdminServerSocket.class.getName());

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
	public AdminServerSocket(InputStream fromClient, OutputStream toClient) {
		this.fromClient = new BufferedReader(new InputStreamReader(fromClient));
		this.toClient = new PrintWriter(toClient);
	}

	/**
	 * Sends a success ("+OK") response without any message text.
	 */
	public void sendSuccess() {
		sendSuccess(new ArrayList<String>(0));
	}

	/**
	 * Sends a success ("+OK") response.
	 * @param msg the message text
	 */
	public void sendSuccess(String msg) {
		String split[] = msg.split("\\r\\n|\\n");
		sendSuccess(Arrays.asList(split));
	}

	/**
	 * Sends a multi-lined success ("+OK") response.
	 * @param lines the multi-lined message
	 */
	public void sendSuccess(List<String> lines) {
		StringBuilder sb = new StringBuilder();
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

					sb.append(EmailRaw.CRLF + msg);
				}
				sb.append(EmailRaw.CRLF).append(".");
			}
		}

		sb.append(EmailRaw.CRLF);

		String response = sb.toString();
		toClient.print(response);
		toClient.flush();

		transactionLog.server(response);
	}

	/**
	 * Sends an error ("-ERR") response.
	 * @param msg the error message
	 * @throws IOException
	 */
	public void sendError(String msg) throws IOException {
		String response = "-ERR " + msg + EmailRaw.CRLF;
		toClient.print(response);
		toClient.flush();
		transactionLog.server(response);
	}

	/**
	 * Gets the next request from the client.
	 * @return
	 * @throws IOException
	 */
	public AdminRequest nextRequest() throws IOException {
		String line = fromClient.readLine();
		if (line == null) {
			return null;
		}
		transactionLog.client(line);
		line = line.trim();

		String command;
		String message = null;
		int space = line.indexOf(' ');
		if (space == -1) {
			command = line;
		} else {
			command = line.substring(0, space);
			message = line.substring(space + 1);
		}
		command = command.toUpperCase();

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		if (message != null) {
			//see: http://stackoverflow.com/questions/3160564/split-tokenize-scan-a-string-being-aware-of-quotation-marks
			Pattern pattern = Pattern.compile("(\\w+)=(\"([^\"]*)\"|'([^']*)'|[a-z']+)", Pattern.CASE_INSENSITIVE);
			Matcher m = pattern.matcher(message);
			while (m.find()) {
				String name = m.group(1).toLowerCase();
				String value;
				if (m.group(3) != null) {
					value = m.group(3);
				} else if (m.group(4) != null) {
					value = m.group(4);
				} else {
					value = m.group(2);
				}
				parameters.put(name, value);
			}
		}

		return new AdminRequest(command, parameters);
	}

	/**
	 * Closes the connection.
	 */
	public void close() {
		try {
			fromClient.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Problem closing input stream from POP3 client.", e);
		}

		toClient.close();
	}

	public TransactionLog getTransactionLog() {
		return transactionLog;
	}
}
