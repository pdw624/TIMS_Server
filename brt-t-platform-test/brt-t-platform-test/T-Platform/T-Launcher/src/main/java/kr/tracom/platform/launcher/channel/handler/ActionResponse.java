package kr.tracom.platform.launcher.channel.handler;

import io.netty.channel.Channel;
import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.attribute.common.AtCommandScriptResponse;
import kr.tracom.platform.attribute.common.AtControlResponse;
import kr.tracom.platform.attribute.common.AtFtpNotifyResponse;
import kr.tracom.platform.attribute.common.AtPowerControlResponse;
import kr.tracom.platform.imp.dao.ImpMapper;
import kr.tracom.platform.launcher.channel.PayloadHandler;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.payload.PlActionResponse;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.manager.CodeManager;
import kr.tracom.platform.service.manager.FtpManager;
import kr.tracom.platform.service.manager.RoutingManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.model.TcpSession;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionResponse extends PayloadHandler {

    public ActionResponse(TimsConfig timsConfig) {
        super(timsConfig);
    }

    @Override
    public List<Short> handle(Channel ch, TimsMessage timsMessage, TcpSession session) {
        boolean bResult = true;
        PlActionResponse payload = (PlActionResponse) timsMessage.getPayload();
        AtMessage atMessage = payload.getAtMessage();
        short attrId = atMessage.getAttrId();
        AtData attrData = atMessage.getAttrData();
        List<Short> attributeList = new ArrayList<>();
        
        if(attrId == AtCode.FTP_NOTIFY_RES) {
            AtFtpNotifyResponse atFtpNotifyResponse = (AtFtpNotifyResponse)attrData;

            FtpManager.downloading(session.getSessionId(), atFtpNotifyResponse.getSourceFile());
        } else if(attrId == AtCode.FTP_RESULT_REQ) {
            AtFtpNotifyResponse response = (AtFtpNotifyResponse)atMessage.getAttrData();

            FtpManager.completed(session.getSessionId(), response.getSourceFile());

        } else if(attrId == AtCode.POWER_CONTROL_RES) {
            AtPowerControlResponse atPowerControlResponse = (AtPowerControlResponse)attrData;

            PlatformDao dao = new PlatformDao();
            Map<String, Object> map = new HashMap<>();
            map.put("DEVICE_ID", session.getSessionId());
            map.put("CODE", atPowerControlResponse.getControlType());
            map.put("DEVICE_TYPE", CodeManager.RoutingGroupId.BUS.getValue());
            map.put("VALUE", (byte)0x12);
            map.put("RESULT", atPowerControlResponse.getResultCode());
            map.put("SYS_DT", DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

            dao.update(PlatformMapper.DEVICE_CONTROL_INSERT, map);

            if(atPowerControlResponse.getResultCode() != AtPowerControlResponse.SUCCESS) {
                bResult = false;
            }
        } else if(atMessage.getAttrId() == AtCode.CONTROL_RES) {
            AtControlResponse atControlResponse = (AtControlResponse)attrData;
            Map<String, Object> map = atControlResponse.toMap();
            map.put("DEVICE_TYPE", RoutingManager.getGroupId(atControlResponse.getDeviceId()));
            map.put("SYS_DT", DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

            PlatformDao dao = new PlatformDao();

            dao.update(PlatformMapper.DEVICE_CONTROL_INSERT, map);

            if(atControlResponse.getResultCode() != 0x00) {
                bResult = false;
            }
        } else if(atMessage.getAttrId() ==AtCode.CMD_RES) {
        	AtCommandScriptResponse atCmdRes = (AtCommandScriptResponse)attrData;
        	Map<String, Object> map = new HashMap<>();
        	map.put("SESSION_ID", session.getSessionId());
        	map.put("RESULT", atCmdRes.getCResult());
        	map.put("LENGTH", atCmdRes.getUsLength());
        	map.put("RES", atCmdRes.getSzResp());
        	System.out.println(">> CMD_RESPONSE! <<");
        	System.out.println(map);
        	PlatformDao dao = new PlatformDao();
        	dao.insert(ImpMapper.INSERT_CMD_RES, map);
        } else {
        	attributeList.add(payload.getAtMessage().getAttrId());
        }
        //여기에 AtCode cmd_res해줄거임 aaaaa

        // 요청에 대한 응답일 경우 패킷 대기 목록에서 제거한다.
        TransactionManager.responseOk(session.getSessionId(), timsMessage.getHeader().getPacketId(), bResult,
                attrId, attrData);

        return attributeList;
    }
}
