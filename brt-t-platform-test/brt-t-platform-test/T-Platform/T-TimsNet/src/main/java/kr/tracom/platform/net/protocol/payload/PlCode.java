package kr.tracom.platform.net.protocol.payload;

public class PlCode {
	public static final byte OP_GET_REQ = 0x10;
	public static final byte OP_GET_RES = 0x11;
	public static final byte OP_SET_REQ = 0x20;
	public static final byte OP_SET_RES = 0x21;
	public static final byte OP_ACTION_REQ = 0x30;
	public static final byte OP_ACTION_RES = 0x31;
	public static final byte OP_EVENT_REQ = 0x40;
	public static final byte OP_EVENT_RES = 0x41;
	public static final byte OP_MANAGE_REQ = 0x50;
	public static final byte OP_MANAGE_RES = 0x51;
	public static final byte OP_INIT_REQ = 0x60;
	public static final byte OP_INIT_RES = 0x61;
	public static final byte OP_PROTOCOL_RES = (byte)0x90;
}
