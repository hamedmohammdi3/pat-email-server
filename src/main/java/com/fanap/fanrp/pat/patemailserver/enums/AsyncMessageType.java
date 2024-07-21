package com.fanap.fanrp.pat.patemailserver.enums;

public class AsyncMessageType {
	public static final byte PING = 0;
	public static final byte SERVER_REGISTER = 1;
	public static final byte DEVICE_REGISTER = 2;
	public static final byte MESSAGE = 3;
	public static final byte MESSAGE_ACK_NEEDED = 4;
	public static final byte MESSAGE_SENDER_ACK_NEEDED = 5;
	public static final byte ACK = 6;
	public static final byte SEND_MESSAGE_FAILED = 7;
	public static final byte PEER_REMOVED = (byte) -3;
	public static final byte REGISTER_QUEUE = (byte) -2;
	public static final byte NOT_REGISTERED = (byte) -1;

	public static boolean isAckNeeded(byte msgType) {
		return msgType == MESSAGE_ACK_NEEDED || msgType == MESSAGE_SENDER_ACK_NEEDED || msgType == PEER_REMOVED;
	}
}