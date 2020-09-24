package kr.tracom.platform.bis.handler;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.attribute.common.AtControlRequest;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.payload.PlActionRequest;
import kr.tracom.platform.net.protocol.payload.PlCode;
import kr.tracom.platform.net.protocol.payload.PlSetRequest;
import kr.tracom.platform.tcp.model.TcpChannelMessage;

public class AdminHandler {

    private DataHandler parentHandler;

    public AdminHandler(DataHandler parentHandler) {
        this.parentHandler = parentHandler;
    }

    public void handle(TcpChannelMessage tcpChannelMessage) {
        //System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        //System.out.println(tcpChannelMessage.getMessage().getLog());

        TimsMessage timsMessage = tcpChannelMessage.getMessage();
        byte opCode = timsMessage.getHeader().getOpCode();

        if(opCode == PlCode.OP_SET_REQ) {
            PlSetRequest request = (PlSetRequest)timsMessage.getPayload();

            for(AtMessage atMessage : request.getAttrList()) {

                if(atMessage.getAttrId() == AtCode.CONTROL_REQ) {
                    AtControlRequest atData = (AtControlRequest)atMessage.getAttrData();

                    this.parentHandler.write(atData.getDeviceId(), timsMessage);
                }
            }
        }
        else if(opCode == PlCode.OP_ACTION_REQ) {
            PlActionRequest request = (PlActionRequest)timsMessage.getPayload();
            AtMessage atMessage = request.getAtMessage();

            if(atMessage.getAttrId() == AtCode.CONTROL_REQ) {
                AtControlRequest atData = (AtControlRequest)atMessage.getAttrData();

                this.parentHandler.write(atData.getDeviceId(), timsMessage);
            }
        }
    }
}
