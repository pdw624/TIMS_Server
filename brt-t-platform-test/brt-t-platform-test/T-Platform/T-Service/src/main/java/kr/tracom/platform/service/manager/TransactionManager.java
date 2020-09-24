package kr.tracom.platform.service.manager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import kr.tracom.platform.net.protocol.TimsHeaderOption;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.payload.*;
import kr.tracom.platform.net.util.ByteHelper;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.model.SyncObject;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpSession;
import kr.tracom.platform.tcp.model.TcpTransaction;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager {
	private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

	private static final String DT_FORMAT_KEY = "yyyyMMddHHmmssSS";

	private static final String TR_SENDING = CodeManager.TransactionState.SENDING.getValue();
	private static final String TR_SENT = CodeManager.TransactionState.SENT.getValue();
	private static final String TR_RETRY = CodeManager.TransactionState.RETRY.getValue();
	private static final String TR_RESPONSE = CodeManager.TransactionState.RESPONSE.getValue();
	private static final String TR_OK_FAIL = CodeManager.TransactionState.OK_FAIL.getValue();
	private static final String TR_FAIL = CodeManager.TransactionState.FAIL.getValue();

	public static final SyncObject transactionSyncObject = new SyncObject();

	// TODO : 세션정보로 처리할지 전역 맵으로 처리할지 고민중.(향후 세션에서 처리해봐야 겠다.)
	public static ConcurrentHashMap<String, List<TcpChannelMessage>> sendPacketMap = new ConcurrentHashMap<>();

	private static void addQueue(String sessionId, TcpChannelMessage tcpChannelMessage) {
		if(sendPacketMap.containsKey(sessionId)) {
			List<TcpChannelMessage> list = sendPacketMap.get(sessionId);
			list.add(tcpChannelMessage);
		} else {
			List<TcpChannelMessage> list = Collections.synchronizedList(new ArrayList<TcpChannelMessage>());
			list.add(tcpChannelMessage);

			sendPacketMap.put(sessionId, list);
		}
	}

	public static void write(TcpChannelMessage tcpChannelMessage) {
		TcpSession tcpSession = tcpChannelMessage.getSession();
		if(tcpSession != null) {
			// 응답을 요구하는 패킷만 트랜잭션 처리한다.
			if(tcpChannelMessage.isResponse()) {
				// 패킷 아이디는 전송 직전에 세션정보에서 처리한다.
				TimsMessage timsMessage = tcpChannelMessage.getMessage();
				timsMessage.getHeader().setPacketId(tcpSession.nextSequence());
				transactionSave(tcpChannelMessage, TR_SENDING);
			}
		}
		tcpChannelMessage.getChannel().writeAndFlush(tcpChannelMessage.getMessage()).addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					if (tcpChannelMessage.getSession() != null && tcpChannelMessage.isResponse()) {
						// TCP 세션이 있는 경우에만 재전송 처리를 위해 큐에 넣는다.
						String sessionId = tcpChannelMessage.getSession().getSessionId();
						addQueue(sessionId, tcpChannelMessage);

						//transactionSave(tcpChannelMessage, TR_SENT);
						PacketLogManager.writeLog(PacketLogManager.SEND_PACKET,
								tcpChannelMessage.getChannel(), tcpChannelMessage.getMessage().getLog(), sessionId);
					}
				} else {
					//전송 실패하면 연결을 해제한다.
					tcpChannelMessage.getChannel().close();
				}
			}
		});
	}

	public static void writeAndClose(TcpChannelMessage tcpChannelMessage) {
		TcpSession tcpSession = tcpChannelMessage.getSession();
		if(tcpSession != null) {
			// 응답을 요구하는 패킷만 트랜잭션 처리한다.
			if(tcpChannelMessage.isResponse()) {
				// 패킷 아이디는 전송 직전에 세션정보에서 처리한다.
				TimsMessage timsMessage = tcpChannelMessage.getMessage();
				timsMessage.getHeader().setPacketId(tcpSession.nextSequence());
				transactionSave(tcpChannelMessage, TR_SENDING);
			}
		}
		tcpChannelMessage.getChannel().writeAndFlush(tcpChannelMessage.getMessage()).addListener(ChannelFutureListener.CLOSE);
	}

	private static void writeRetry(final TcpChannelMessage tcpChannelMessage) {
		// 재전송일 경우 재전송 횟수와 시간을 업데이트 한다.
		tcpChannelMessage.nextRetry();

		transactionSave(tcpChannelMessage, TR_RETRY);

		tcpChannelMessage.getChannel().writeAndFlush(tcpChannelMessage.getMessage());
	}

	public static void writeById(String clientId, TimsMessage timsMessage) {
		ChannelGroup channels = TcpSessionManager.channelGroup;

		for (Channel ch : channels) {
			TcpSession session = ch.attr(TcpSessionManager.channelKey).get();
			if(session.getSessionId().equals(clientId)) {
				write(new TcpChannelMessage(ch, session, timsMessage));
			}
		}
	}

	public static void writeByType(String clientType, TimsMessage timsMessage) {
		ChannelGroup channels = TcpSessionManager.channelGroup;

		for (Channel ch : channels) {
			TcpSession session = ch.attr(TcpSessionManager.channelKey).get();
			if(session.getRemoteType().equals(clientType)) {
				write(new TcpChannelMessage(ch, session, timsMessage));
			}
		}
	}

	public static void responseOk(String sessionId, byte packetId, boolean bResult, short attrId, AtData attrData) {
		if(sendPacketMap.containsKey(sessionId)) {
			List<TcpChannelMessage> list = sendPacketMap.get(sessionId);

			synchronized (list) {
				for (Iterator<TcpChannelMessage> iterator = list.iterator(); iterator.hasNext(); ) {
					TcpChannelMessage tcpChannelMessage = iterator.next();
					TimsMessage timsMessage = tcpChannelMessage.getMessage();

					if (timsMessage.getHeader().getPacketId() == packetId) {
						String sendStatus = bResult ? TR_RESPONSE : TR_OK_FAIL;
						transactionSave(tcpChannelMessage, sendStatus);
						iterator.remove();

						if (tcpChannelMessage.isBlocking()) {
							synchronized (transactionSyncObject) {
								transactionSyncObject.setSyncId(attrId);
								transactionSyncObject.setSyncData(attrData);
								transactionSyncObject.notifyAll();
							}
						}
					}
				}
			}
		}
	}

	public static boolean waitOn(int seconds) {
		boolean bSuccess;
		while(true) {
			try {
				synchronized (transactionSyncObject) {
					transactionSyncObject.wait(1000 * seconds);
				}
				bSuccess = true;
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				bSuccess = false;
			}
			break;
		}
		return bSuccess;
	}

	public static void transactionSave(TcpChannelMessage tcpChannelMessage, String sendState) {
		TcpSession tcpSession = tcpChannelMessage.getSession();
		TimsMessage timsMessage = tcpChannelMessage.getMessage();

		String registerDateTime = tcpChannelMessage.getRegisterDateTime().toString(DT_FORMAT_KEY);
		String sendDateTime = tcpChannelMessage.getSendDateTime().toString(DT_FORMAT_KEY);

		TcpTransaction transaction = new TcpTransaction();
		transaction.setRegisterDateTime(registerDateTime);
		transaction.setPlatformId(PlatformConfig.PLF_ID);
		transaction.setSessionId(tcpSession.getSessionId());
		transaction.setPacketId(ByteHelper.unsigned(timsMessage.getHeader().getPacketId()));
		//transaction.setPacketId(tcpSession.getPacketSequence());

		if(TR_SENDING.equals(sendState)) {
			transaction.setRemoteType(tcpSession.getRemoteType());
			transaction.setPayloadType(timsMessage.getPayload().PayloadName);
			//transaction.setByteData(timsMessage.toByte());
			transaction.setStringData(timsMessage.getLog().trim());
			transaction.setSystemDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

			if(timsMessage.getHeader().getOpCode() == PlCode.OP_GET_REQ) {
				PlGetRequest payload = (PlGetRequest) timsMessage.getPayload();

				transaction.setAttributeCount(payload.getAttrCount());
				transaction.setAttributeList(commaStringAttrId(payload.getAttrList()));
			} else if(timsMessage.getHeader().getOpCode() == PlCode.OP_SET_REQ) {
				PlSetRequest payload = (PlSetRequest) timsMessage.getPayload();

				transaction.setAttributeCount(payload.getAttrCount());
				transaction.setAttributeList(commaStringAtMessage(payload.getAttrList()));
			} else if(timsMessage.getHeader().getOpCode() == PlCode.OP_EVENT_REQ) {
				PlEventRequest payload = (PlEventRequest) timsMessage.getPayload();

				transaction.setAttributeCount(payload.getAttrCount());
				transaction.setAttributeList(commaStringAtMessage(payload.getAttrList()));
			} else if(timsMessage.getHeader().getOpCode() == PlCode.OP_ACTION_REQ) {
				PlActionRequest payload = (PlActionRequest) timsMessage.getPayload();

				transaction.setAttributeCount((byte)1);
				transaction.setAttributeList(String.valueOf(payload.getAtMessage().getAttrId()));
			}
		}
		transaction.setRetryCount(tcpChannelMessage.getRetry());
		transaction.setSendState(sendState);
		transaction.setSendDateTime(sendDateTime);

		PlatformDao platformDao = new PlatformDao();

		if(TR_SENDING.equals(sendState)) {
			try {
				platformDao.insert(PlatformMapper.TRANSACTION_INSERT, transaction.toMap());
			} catch(Exception e) {
				logger.error(ErrorManager.getStackTrace(e));

				ErrorManager.trace(PlatformConfig.PLF_ID,
						TransactionManager.class.getName(), Thread.currentThread().getStackTrace(),
						transaction.toString(), ErrorManager.getStackTrace(e));
			}
		} else {
			try {
				platformDao.update(PlatformMapper.TRANSACTION_UPDATE, transaction.toMap());
			} catch(Exception e) {
				logger.error(ErrorManager.getStackTrace(e));

				ErrorManager.trace(PlatformConfig.PLF_ID,
						TransactionManager.class.getName(), Thread.currentThread().getStackTrace(),
						transaction.toString(), ErrorManager.getStackTrace(e));
			}
		}
	}

	private static String commaStringAtMessage(List<AtMessage> atList) {
		StringBuilder stringBuilder = new StringBuilder();
		for(AtMessage atMessage : atList) {
			stringBuilder.append(atMessage.getAttrId());
			stringBuilder.append(",");
		}
		String attrList = stringBuilder.toString();
		if(attrList.length() > 0) {
			return attrList.substring(0, attrList.length() - 1);
		} else {
			return "-";
		}
	}

	private static String commaStringAttrId(List<Short> atList) {
		StringBuilder stringBuilder = new StringBuilder();
		for(Short attrId : atList) {
			stringBuilder.append(attrId);
			stringBuilder.append(",");
		}
		String attrList = stringBuilder.toString();
		if(attrList.length() > 0) {
			return attrList.substring(0, attrList.length() - 1);
		} else {
			return "-";
		}
	}

	public static void timeCheck() {
		DateTime nowDate = new DateTime();

		// TODO : 클라이언트 수가 많을 경우 문제가 될 수 있음
		//long start = System.currentTimeMillis();
		try {
			for (Iterator<Map.Entry<String, List<TcpChannelMessage>>> it = sendPacketMap.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry<String, List<TcpChannelMessage>> entry = it.next();

				synchronized (entry.getValue()) {
					for (Iterator<TcpChannelMessage> iterator = entry.getValue().iterator(); iterator.hasNext(); ) {
						TcpChannelMessage tcpChannelMessage = iterator.next();
						TimsMessage timsMessage = tcpChannelMessage.getMessage();
						TimsHeaderOption headerOption = tcpChannelMessage.getMessage().getHeader().getHeaderOption();

						String sessionId = tcpChannelMessage.getSession().getSessionId();
						int retryCount = headerOption.getRetryCount();
						int timeOut = headerOption.getTimeOut();

						// 재전송 타임아웃보다 크면 재전송한다.
						int gapSeconds = Seconds.secondsBetween(tcpChannelMessage.getSendDateTime(), nowDate).getSeconds();

						//System.out.println("gapSeconds : " + gapSeconds + ", timeOut :" + timeOut);

						if (gapSeconds > timeOut) {
							writeRetry(tcpChannelMessage);
							logger.debug("[timeCheck] no response " + timeOut + " seconds : " + sessionId + ", " + timsMessage.getLog());
							//System.out.println("[timeCheck] no response " + timeOut + " seconds : " + sessionId + ", " + timsMessage.getLog());
						}

						// 재전송 횟수가 설정값 이상이면 보내기 실패
						if (tcpChannelMessage.getRetry() >= retryCount) {
							logger.debug("[countCheck] no response over " + retryCount + " times : " + sessionId + ", " + timsMessage.getLog());
							//System.out.println("[countCheck] no response over " + retryCount + " times : " + sessionId + ", " + timsMessage.getLog());

							transactionSave(tcpChannelMessage, TR_FAIL);

							iterator.remove();
						}
					}
				}
			}
		} catch(Exception e) {
			logger.error(ErrorManager.getStackTrace(e));
		}

		//long end = System.currentTimeMillis();
		//System.out.println("실행 시간 : " + (end - start) / 1000.0);
	}
}
