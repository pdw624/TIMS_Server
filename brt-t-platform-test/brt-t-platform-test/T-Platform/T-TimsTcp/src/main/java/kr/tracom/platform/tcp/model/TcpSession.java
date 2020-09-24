package kr.tracom.platform.tcp.model;

import kr.tracom.platform.db.base.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TcpSession implements BaseDTO {
	private String platformId;
	private String sessionId;
	private String sessionName;
	private String sessionIp;
	private String remoteType;
	private long elapsedTime;
	private long inPacketUsage;
	private long outPacketUsage;
	private String loginDateTime;
	private String logoutDateTime;
	private String lastDateTime;
	private short packetId;
	
	public TcpSession() {
		elapsedTime = 0;
		inPacketUsage = 0;
		outPacketUsage = 0;
		packetId = -1;
	}

	public synchronized byte nextSequence() {
		packetId++;

		if (packetId > 0xFF) {
			packetId = 0;
		}

		return (byte)packetId;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("PLF_ID", platformId);
		map.put("SESSION_ID", sessionId);
		map.put("SESSION_NAME", sessionName);
		map.put("SESSION_IP", sessionIp);
		map.put("REMOTE_TYPE", remoteType);
		map.put("ELAPSED_TIME", elapsedTime);
		map.put("IN_PACKET", inPacketUsage);
		map.put("OUT_PACKET", outPacketUsage);
		map.put("LOGIN_DT", loginDateTime);
		map.put("LOGOUT_DT", logoutDateTime);
		map.put("LAST_DT", lastDateTime);
		
		return map;
	}
}
