package kr.tracom.platform.net.protocol;

import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.attribute.AtResult;
import kr.tracom.platform.net.protocol.payload.*;

public class TimsMessageBuilder {
	private TimsMessage timsMessage;

	public TimsMessageBuilder(TimsConfig timsConfig) {
		this(timsConfig, new TimsAddress(), new TimsAddress(), 0, 0);
	}

	public TimsMessageBuilder(TimsConfig timsConfig, TimsAddress srcAddress, TimsAddress dstAddress) {
		this.timsMessage = new TimsMessage(timsConfig);

		TimsHeader header = this.timsMessage.getHeader();

		if(header instanceof TimsHeaderTypeA) {
			TimsHeaderTypeA headerA = (TimsHeaderTypeA)header;
			headerA.setSrcAddress(srcAddress);
			headerA.setDstAddress(dstAddress);
		} else if(header instanceof TimsHeaderTypeB) {
			TimsHeaderTypeB headerB = (TimsHeaderTypeB)header;
		}
	}

	public TimsMessageBuilder(TimsConfig timsConfig, TimsAddress srcAddress, TimsAddress dstAddress, int currentIndex, int totalIndex) {
		this.timsMessage = new TimsMessage(timsConfig);

		TimsHeader header = this.timsMessage.getHeader();

		if(header instanceof TimsHeaderTypeA) {
			TimsHeaderTypeA headerA = (TimsHeaderTypeA)header;
			headerA.setSrcAddress(srcAddress);
			headerA.setDstAddress(dstAddress);
			
			headerA.setCurrentIndex((short)currentIndex);
			headerA.setTotalIndex((short)totalIndex);
		} else if(header instanceof TimsHeaderTypeB) {
			TimsHeaderTypeB headerB = (TimsHeaderTypeB)header;
			headerB.setCurrentIndex((byte)currentIndex);
			headerB.setTotalIndex((byte)totalIndex);			
		}
	}
	
	public TimsMessage getRequest(short attributeId) {	
    	PlGetRequest payload = new PlGetRequest();
    	payload.setAttrCount((byte)1);
    	payload.addAttributeId(attributeId);
    	
    	this.timsMessage.setOpCode(payload.OpCode);
    	this.timsMessage.setPayload(payload);
    	this.timsMessage.setPayloadSize((short)payload.getSize());  
    	
    	return this.timsMessage;
    }
	
	public TimsMessage getResponse(PlGetResponse payload) {
    	this.timsMessage.setOpCode(payload.OpCode);
    	this.timsMessage.setPayload(payload);
    	this.timsMessage.setPayloadSize((short)payload.getSize());
    	
    	return this.timsMessage;
    }
	
	public TimsMessage setRequest(AtData atData) {
		AtMessage atMessage = new AtMessage();
		atMessage.setAttrId(atData.getAttrId());
		atMessage.setAttrSize((short)atData.getSize());
		atMessage.setAttrData(atData);
		
		PlSetRequest payload = new PlSetRequest();
		payload.setAttrCount((byte)1);
		payload.addAttrMessage(atMessage);
		
    	this.timsMessage.setOpCode(payload.OpCode);
    	this.timsMessage.setPayload(payload);
    	this.timsMessage.setPayloadSize((short)payload.getSize());
    	
    	return this.timsMessage;
	}
	
	public TimsMessage setResponse(short attributeId, byte result) {	
		AtResult atResult = new AtResult();
		atResult.setAtId(attributeId);
		atResult.setResult(result);
		
		PlSetResponse payload = new PlSetResponse();
		payload.setAttrCount((byte)1);
		payload.addAttrResult(atResult);
		
		this.timsMessage.setOpCode(payload.OpCode);
    	this.timsMessage.setPayload(payload);
    	this.timsMessage.setPayloadSize((short)payload.getSize());
    	
    	return this.timsMessage;
    }

	public TimsMessage eventRequest(AtData atData) {
		AtMessage atMessage = new AtMessage();
		atMessage.setAttrId(atData.getAttrId());
		atMessage.setAttrSize((short)atData.getSize());
		atMessage.setAttrData(atData);

		PlEventRequest payload = new PlEventRequest();
		payload.setAttrCount((byte)1);
		payload.addAttrMessage(atMessage);

		this.timsMessage.setOpCode(payload.OpCode);
		this.timsMessage.setPayload(payload);
		this.timsMessage.setPayloadSize((short)payload.getSize());

		return this.timsMessage;
	}

    public TimsMessage eventResponse(short attributeId, byte result) {
		AtResult atResult = new AtResult();
		atResult.setAtId(attributeId);
		atResult.setResult(result);

		PlEventResponse payload = new PlEventResponse();
		payload.setAttrCount((byte)1);
		payload.addAttributeResult(atResult);

		this.timsMessage.setOpCode(payload.OpCode);
		this.timsMessage.setPayload(payload);
		this.timsMessage.setPayloadSize((short)payload.getSize());

		return this.timsMessage;
	}

	public TimsMessage actionRequest(AtData atData) {
		AtMessage atMessage = new AtMessage();
		atMessage.setAttrId(atData.getAttrId());
		atMessage.setAttrSize((short)atData.getSize());
		atMessage.setAttrData(atData);

		PlActionRequest payload = new PlActionRequest();
		payload.setAtMessage(atMessage);

		this.timsMessage.setOpCode(payload.OpCode);
		this.timsMessage.setPayload(payload);
		this.timsMessage.setPayloadSize((short)payload.getSize());

		return this.timsMessage;
	}

	public TimsMessage actionResponse(AtData atData) {
		AtMessage atMessage = new AtMessage();
		atMessage.setAttrId(atData.getAttrId());
		atMessage.setAttrSize((short)atData.getSize());
		atMessage.setAttrData(atData);

		PlActionResponse payload = new PlActionResponse();
		payload.setAtMessage(atMessage);

		this.timsMessage.setOpCode(payload.OpCode);
		this.timsMessage.setPayload(payload);
		this.timsMessage.setPayloadSize((short)payload.getSize());

		return this.timsMessage;
	}

	public TimsMessage initRequest(short attributeId) {
		AtMessage atMessage = new AtMessage();
		atMessage.setAttrId(attributeId);
		atMessage.setAttrSize((short)0);
		atMessage.setAttrData(null);

		PlInitRequest payload = new PlInitRequest();
		payload.addAttrMessage(atMessage);

		this.timsMessage.setOpCode(payload.OpCode);
		this.timsMessage.setPayload(payload);
		this.timsMessage.setPayloadSize((short)payload.getSize());

		return this.timsMessage;
	}

	public TimsMessage initResponse(short attributeId, AtData atData) {
		AtMessage atMessage = new AtMessage();
		atMessage.setAttrId(attributeId);
		atMessage.setAttrSize((short)atData.getSize());
		atMessage.setAttrData(atData);

		PlInitResponse payload = new PlInitResponse();
		payload.addAttrMessage(atMessage);

		this.timsMessage.setOpCode(payload.OpCode);
		this.timsMessage.setPayload(payload);
		this.timsMessage.setPayloadSize((short)payload.getSize());

		return this.timsMessage;
	}

	public TimsMessage protocolResponse(byte resultCode, byte opCode, TimsAddress responderId, byte errorCode, byte packetId) {
		PlProtocolResponse payload = new PlProtocolResponse();
		payload.setResultCode(resultCode);
		payload.setOpCode(opCode);
		payload.setResponderId(responderId);
		payload.setErrorCode(errorCode);
		payload.setPacketId(packetId);

		this.timsMessage.setOpCode(payload.OpCode);
		this.timsMessage.setPayload(payload);
		this.timsMessage.setPayloadSize((short)payload.getSize());

		return this.timsMessage;
	}
}
