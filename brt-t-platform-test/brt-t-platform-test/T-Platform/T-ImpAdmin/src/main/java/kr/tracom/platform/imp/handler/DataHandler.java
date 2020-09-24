package kr.tracom.platform.imp.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.attribute.imp.AtProcess;
import kr.tracom.platform.attribute.imp.AtProcessItem;
import kr.tracom.platform.attribute.imp.AtTimeStamp;
import kr.tracom.platform.imp.dao.ImpMapper;
import kr.tracom.platform.net.protocol.TimsHeaderTypeA;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.payload.PlCode;
import kr.tracom.platform.net.protocol.payload.PlGetRequest;
import kr.tracom.platform.net.protocol.payload.PlGetResponse;
import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpSession;

public class DataHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServiceLauncher launcher;

    public DataHandler(ServiceLauncher launcher) {
        this.launcher = launcher;
    }

    public void read(TcpChannelMessage tcpChannelMessage) {
        TimsMessage timsMessage = tcpChannelMessage.getMessage();
        TcpSession tcpSession = tcpChannelMessage.getSession();
        TimsHeaderTypeA header = (TimsHeaderTypeA) timsMessage.getHeader();
        short reserved1 = header.getReserved1();
        short reserved2 = header.getReserved2();

        byte opCode = timsMessage.getHeader().getOpCode();

        if(opCode == PlCode.OP_GET_RES) {
            PlatformDao platformDao = new PlatformDao();
            String updateDate = DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT);
            PlGetResponse payload = (PlGetResponse)timsMessage.getPayload();

            if(payload.getAttrList().size() > 0) {
                platformDao.update(ImpMapper.DELETE_IT_VERSION, platformDao.buildMap("IMP_ID", tcpSession.getSessionId()));

                for (AtMessage atMessage : payload.getAttrList()) {
                    AtProcess atProcess = (AtProcess) atMessage.getAttrData();
                    for (AtProcessItem atProcessItem : atProcess.getItems()) {
                        Map<String, Object> paramMap = new HashMap<>();
                        paramMap.put("IMP_ID", tcpSession.getSessionId());
                        paramMap.put("PRCS_NAME", atProcessItem.getProcessName());
                        paramMap.put("PRCS_INDEX", atProcessItem.getProcessIndex());
                        paramMap.put("VER_HIGH", atProcessItem.getVersionHigh());
                        paramMap.put("VER_MID", atProcessItem.getVersionMiddle());
                        paramMap.put("VER_LOW", atProcessItem.getVersionLow());
                        paramMap.put("START_DATE", atProcessItem.getStartTime());
                        paramMap.put("LAST_DATE", atProcessItem.getLastRunTime());
                        paramMap.put("BUILD_DATE", atProcessItem.getBuildDate());
                        paramMap.put("UPDATE_DATE", updateDate);
                        
                        
                        
                        platformDao.update(ImpMapper.INSERT_IT_VERSION, paramMap);
                    }
                }
            }
        } else if(opCode == PlCode.OP_GET_REQ) {
        	PlGetRequest request = (PlGetRequest) timsMessage.getPayload();
            PlGetResponse response = new PlGetResponse();

            for(Short attrId : request.getAttrList()) {
                if(attrId == AtCode.TIMESTAMP) {
                    AtTimeStamp timeStamp = new AtTimeStamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PlatformConfig.PLF_DT_FORMAT)));

                    AtMessage atMessage = new AtMessage();
                    atMessage.setAttrId(attrId);
                    atMessage.setAttrSize((short) timeStamp.getSize());
                    atMessage.setAttrData(timeStamp);

                    response.addAttribute(atMessage);
                }
            }

            TimsMessageBuilder builder = new TimsMessageBuilder(launcher.getTimsConfig());
            TimsMessage responseMessage = builder.getResponse(response);
            TimsHeaderTypeA responseHeader = (TimsHeaderTypeA) responseMessage.getHeader();
            responseHeader.setReserved1(reserved1);
            responseHeader.setReserved2(reserved2);
            
            TransactionManager.write(new TcpChannelMessage(tcpChannelMessage.getChannel(), tcpChannelMessage.getSession(), responseMessage));
        }
        logger.debug(timsMessage.getLog());
    }
}
