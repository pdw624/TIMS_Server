package kr.tracom.platform.launcher.channel.handler;

import io.netty.channel.Channel;
import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.attribute.common.AtStatus;
import kr.tracom.platform.attribute.common.AtStatusItem;
import kr.tracom.platform.attribute.common.AtVersion;
import kr.tracom.platform.attribute.common.AtVersionItem;
import kr.tracom.platform.launcher.channel.PayloadHandler;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.payload.PlGetResponse;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.manager.RoutingManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.model.TcpSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetResponse extends PayloadHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GetResponse(TimsConfig timsConfig) {
        super(timsConfig);
    }

    @Override
    public List<Short> handle(Channel ch, TimsMessage timsMessage, TcpSession session) {
        List<Short> attributeList = new ArrayList<>();

        boolean bResult = true;
        PlatformDao dao = new PlatformDao();

        PlGetResponse payload = (PlGetResponse) timsMessage.getPayload();
        short attrId = 0;
        AtData attrData = null;

        for (int i = 0; i < payload.getAttrCount(); i++) {
            AtMessage atMessage = payload.getAttrList().get(i);
            if(atMessage.getSize() == 0) {
                bResult = false;
            }

            attrId = atMessage.getAttrId();
            attrData = atMessage.getAttrData();

            if(attrId == AtCode.VERSION) {
                AtVersion atVersion = (AtVersion)attrData;

                for(AtVersionItem item : atVersion.getItems()) {
                    Map<String, Object> map = item.toMap();
                    map.put("DEVICE_ID", atVersion.getDeviceId());
                    map.put("SYS_DT", DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));
                    map.put("DEVICE_TYPE", RoutingManager.getGroupId(atVersion.getDeviceId()));
                    dao.update(PlatformMapper.DEVICE_VERSION_UPDATE, map);
                }
            }
            else if(attrId == AtCode.STATUS) {
                AtStatus atStatus = (AtStatus) attrData;

                for(AtStatusItem item : atStatus.getItems()) {
                    Map<String, Object> map = item.toMap();
                    map.put("DEVICE_ID", atStatus.getDeviceId());
                    map.put("SYS_DT", DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));
                    map.put("DEVICE_TYPE", RoutingManager.getGroupId(atStatus.getDeviceId()));

                    dao.insert(PlatformMapper.DEVICE_STATUS_INSERT, map);
                }
            }
            else if(attrId == AtCode.IMP_PROCESS_LIST) {
                //AtProcess atProcess = (AtProcess) attrData;
            }

            attributeList.add(attrId);
        }

        // 요청에 대한 응답일 경우 패킷 대기 목록에서 제거한다.
        TransactionManager.responseOk(session.getSessionId(), timsMessage.getHeader().getPacketId(), bResult,
                attrId, attrData);

        return attributeList;
    }
}
