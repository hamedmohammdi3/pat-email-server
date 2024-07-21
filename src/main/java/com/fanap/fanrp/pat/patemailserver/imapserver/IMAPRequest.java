package com.fanap.fanrp.pat.patemailserver.imapserver;


public class IMAPRequest {


	private final String commandId;
	private final String pureCommand;

	/**
	 * The command (e.g. "USER").
	 */
	private final String command;

	/**
	 * The command parameters.
	 */
	private final String parameters;

	/**
	 * @param commandId
	 * @param pureCommand
	 * @param command the command (e.g. "USER")
	 * @param parameters the command parameters or null if there are none
	 */
	public IMAPRequest(String commandId, String pureCommand, String command, String parameters) {
		this.commandId = commandId;
		this.pureCommand = pureCommand;
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

	public String getCommandId() {
		return commandId;
	}
}
