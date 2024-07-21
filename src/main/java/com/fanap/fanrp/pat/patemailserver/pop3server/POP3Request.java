package com.fanap.fanrp.pat.patemailserver.pop3server;


public class POP3Request {
	/**
	 * The command (e.g. "USER").
	 */
	private final String command;

	/**
	 * The command parameters.
	 */
	private final String parameters;

	/**
	 * @param command the command (e.g. "USER")
	 * @param parameters the command parameters or null if there are none
	 */
	public POP3Request(String command, String parameters) {
		this.command = command;
		this.parameters = parameters;
	}

	/**
	 * Gets the command.
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Gets the command parameters.
	 * @return the command parameters or null if there are none
	 */
	public String getParameters() {
		return parameters;
	}
}
