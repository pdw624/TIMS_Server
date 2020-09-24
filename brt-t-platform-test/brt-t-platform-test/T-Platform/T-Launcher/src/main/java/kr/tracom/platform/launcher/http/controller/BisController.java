package kr.tracom.platform.launcher.http.controller;

import kr.tracom.platform.attribute.common.AtControlRequest;
import kr.tracom.platform.attribute.common.AtFtpNotifyRequest;
import kr.tracom.platform.attribute.common.AtPowerControlRequest;
import kr.tracom.platform.bis.TBisLauncher;
import kr.tracom.platform.common.log.Debug;
import kr.tracom.platform.common.util.FileUtil;
import kr.tracom.platform.launcher.TLauncher;
import kr.tracom.platform.launcher.http.builder.JsonBuilder;
import kr.tracom.platform.launcher.module.TServiceModule;
import kr.tracom.platform.master.TFileDB;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.net.util.ByteHelper;
import kr.tracom.platform.service.domain.MtRouting;
import kr.tracom.platform.service.manager.CodeManager;
import kr.tracom.platform.service.manager.ErrorManager;
import kr.tracom.platform.service.manager.FtpManager;
import kr.tracom.platform.service.manager.RoutingManager;
import kr.tracom.platform.service.model.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/bis")
public class BisController extends ChannelController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private TimsConfig timsConfig = TLauncher.getInstance().getTimsConfig();

    @POST
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@QueryParam("sessionId") String sessionId, @QueryParam("cmd") short cmd) {
        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.getRequest(cmd);

        Debug.log(BisController.class.getName(), sessionId, cmd);

        return relayTimsMessage(sessionId, timsMessage, false);
    }

    public String jsonp(@QueryParam("callback") String callback) {
        return JsonBuilder.getJsonP(callback, JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
    }

    @POST
    @Path("/getRequest/{deviceType}/{sessionId}/{attrId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRequest(@PathParam("deviceType") int deviceType,
                             @PathParam("sessionId") String sessionId, @PathParam("attrId") short attrId) {
        logger.info("[getRequest] " + String.format("/%d/%s/%d", deviceType, sessionId, attrId));

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.getRequest(attrId);

        //Debug.log("kr.tracom.platform.launcher.http.controller", deviceType, sessionId, attrId);

        String json = "";
        String targetGroup;
        if(deviceType == 0) {
            targetGroup = String.format("%d,%d", CodeManager.RoutingGroupId.BUS.getValue(),
                    CodeManager.RoutingGroupId.STN.getValue());
        } else {
            targetGroup = String.valueOf(deviceType);
        }

        if(CodeManager.ALL_ID.equals(sessionId)) {
            List<Object> listSystem = RoutingManager.getSystemName(targetGroup);
            for(Object obj : listSystem) {
                MtRouting mtRouting = (MtRouting)obj;
                json = relayTimsMessage(mtRouting.getSystemName(), timsMessage, false);
            }
        } else {
            json = relayTimsMessage(sessionId, timsMessage, false);
        }

        return json;
    }

    @POST
    @Path("/control")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String control(@BeanParam JsonControlNotify controlNotify) {
        logger.info("[control] " + JsonBuilder.pertty(controlNotify));

        if(controlNotify == null) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }
        String sessionId = controlNotify.getDeviceId();
        if(!checkSessionId(sessionId)) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }

        String result;
        try {
            if (controlNotify.getDeviceType() == CodeManager.RoutingGroupId.BUS.getValue()) {
                result = obeReset(sessionId);
            } else if (controlNotify.getDeviceType() == CodeManager.RoutingGroupId.STN.getValue()) {
                result = bitReset(sessionId, controlNotify.getCode(), controlNotify.getValue());
            } else {
                result = JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_TYPE);
            }
        }
        catch (Exception e) {
            result = JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_FILE_FORMAT);
            logger.error(ErrorManager.getStackTrace(e));
        }

        return result;
    }

    @POST
    @Path("/ftp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String ftpNotify(@BeanParam JsonFtpNotify ftpNotify) {
        logger.info("[ftp] " + JsonBuilder.pertty(ftpNotify));

        if(ftpNotify == null) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }

        String sessionId = ftpNotify.getDeviceId();
        if(!checkSessionId(sessionId)) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }

        if(!CodeManager.FtpFile.isExist((byte)ftpNotify.getFileCode())) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }

        String result;
        try {
            if (ftpNotify.getFileCode() == (int) CodeManager.FtpFile.MASTER.getValue()) {
                result = masterChange(sessionId, ftpNotify.getDeviceType(), ftpNotify.getApplyDate());
            } else {
                String ftpFilePath = ftpNotify.getFtpPath() + "/" + ftpNotify.getFtpFile();
                result = ftpFileNotify(ftpFilePath, (byte)ftpNotify.getFileCode(), sessionId, ftpNotify.getDeviceType());
            }
        } catch (Exception e) {
            result = JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_FILE_FORMAT);
            logger.error(ErrorManager.getStackTrace(e));
        }

        return result;
    }

    @POST
    @Path("/bit/scenario")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String bitScenario(@BeanParam JsonScenarioRoot scenarioRoot) {
        logger.info("[bit/scenario] " + JsonBuilder.pertty(scenarioRoot));

        if(scenarioRoot == null) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }
        String sessionId = scenarioRoot.getDeviceId();
        if(!checkSessionId(sessionId)) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }

        String result;
        try {
            JsonData jsonData = new JsonData();
            jsonData.setType(CodeManager.FtpFile.BIT_SCENARIO.getValue());
            jsonData.setApplyDateTime(scenarioRoot.getApplyDate());
            jsonData.setData(scenarioRoot);

            String ftpFile = TFileDB.getInstance().buildBisBit(TFileDB.SVC_BIS_ID, jsonData);

            result = ftpFileNotify(ftpFile, jsonData.getType(), sessionId, CodeManager.RoutingGroupId.STN.getValue());
        } catch (Exception e) {
            result = JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_FILE_FORMAT);
            logger.error(ErrorManager.getStackTrace(e));
        }

        return result;
    }

    @POST
    @Path("/bit/monitor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String bitMonitor(@BeanParam JsonMonitorRoot monitorRoot) {
        logger.info("[bit/monitor] " + JsonBuilder.pertty(monitorRoot));

        if(monitorRoot == null) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }
        String sessionId = monitorRoot.getDeviceId();
        if(!checkSessionId(sessionId)) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }
        String result;
        try {
            JsonData jsonData = new JsonData();
            jsonData.setType(CodeManager.FtpFile.BIT_MONITOR.getValue());
            jsonData.setApplyDateTime(monitorRoot.getApplyDate());
            jsonData.setData(monitorRoot);

            String ftpFile = TFileDB.getInstance().buildBisBit(TFileDB.SVC_BIS_ID, jsonData);

            result = ftpFileNotify(ftpFile, jsonData.getType(), sessionId, CodeManager.RoutingGroupId.STN.getValue());
        } catch (Exception e) {
            result = JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_FILE_FORMAT);
            logger.error(ErrorManager.getStackTrace(e));
        }

        return result;
    }

    @POST
    @Path("/bit/illumination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String bitIllumination(@BeanParam JsonIlluminationRoot illuminationRoot) {
        logger.info("[bit/illumination] " + JsonBuilder.pertty(illuminationRoot));

        if(illuminationRoot == null) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }
        String sessionId = illuminationRoot.getDeviceId();
        if(!checkSessionId(sessionId)) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }

        String result;
        try {
            JsonData jsonData = new JsonData();
            jsonData.setType(CodeManager.FtpFile.BIT_ILLUMINATION.getValue());
            jsonData.setApplyDateTime(illuminationRoot.getApplyDate());
            jsonData.setData(illuminationRoot);

            String ftpFile = TFileDB.getInstance().buildBisBit(TFileDB.SVC_BIS_ID, jsonData);

            result = ftpFileNotify(ftpFile, jsonData.getType(), sessionId, CodeManager.RoutingGroupId.STN.getValue());
        } catch (Exception e) {
            result = JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_FILE_FORMAT);
            logger.error(ErrorManager.getStackTrace(e));
        }

        return result;
    }

    private boolean checkSessionId(String sessionId) {
        if(sessionId == null || sessionId.length() != 10) {
            return false;
        } else {
            return true;
        }
    }

    private String masterChange(String sessionId, int deviceType, String applyDateTime) {
        String ftpFile = TFileDB.getInstance().buildBisMaster(TFileDB.SVC_BIS_ID, applyDateTime);

        if(!ftpFile.isEmpty()) {
            ftpFileNotify(ftpFile, CodeManager.FtpFile.MASTER.getValue(), sessionId, deviceType);

            TFileDB.getInstance().loadBisMaster(TFileDB.SVC_BIS_ID);

            TBisLauncher bisLauncher = (TBisLauncher) TServiceModule.getInstance().getLauncher(TFileDB.SVC_BIS_ID);
            bisLauncher.initBisDB();

            return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
        } else {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_UNKNOWN);
        }
    }

    public String obeReset(String clientId) {
        AtPowerControlRequest atData = new AtPowerControlRequest();
        atData.setTargetId(AtPowerControlRequest.IMP_MAIN_POWER);
        atData.setControlType((byte)0x12);
        atData.setDelayTime((byte)0x00);
        atData.setResetLapse((byte)1);

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.actionRequest(atData);

        return relayTimsMessage(clientId, timsMessage, true);
    }

    public String bitReset(String clientId, String code, int value) {
        AtControlRequest atData = new AtControlRequest();
        atData.setDeviceId(clientId);
        atData.setCode(code);
        atData.setValue((short)value);

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.actionRequest(atData);

        return relayTimsMessage(clientId, timsMessage, true);
    }

    public String ftpFileNotify(String createdFile, byte fileCode, String sessionId, int routingGroup) {
        String json = "";
        if(!createdFile.isEmpty()) {
            String ftpPath = FileUtil.getFtpPath(createdFile);
            String ftpFile = FileUtil.getFtpFile(createdFile);
            String targetGroup;

            if(routingGroup == 0) {
                targetGroup = String.format("%d,%d", CodeManager.RoutingGroupId.BUS.getValue(),
                        CodeManager.RoutingGroupId.STN.getValue());
            } else {
                targetGroup = String.valueOf(routingGroup);
            }

            if(CodeManager.ALL_ID.equals(sessionId)) {
                List<Object> listSystem = RoutingManager.getSystemName(targetGroup);

                for(Object obj : listSystem) {
                    MtRouting mtRouting = (MtRouting)obj;

                    json = ftpSaveAndSend(mtRouting.getGroupId(), mtRouting.getSystemName(), fileCode, ftpPath, ftpFile);
                }
            } else {
                int groupId = RoutingManager.getGroupId(sessionId);

                json = ftpSaveAndSend(groupId, sessionId, fileCode, ftpPath, ftpFile);
            }
        } else {
            json = JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_DATA);
        }

        return json;
    }

    private String ftpSaveAndSend(int groupId, String sessionId, byte fileCode, String ftpPath, String ftpFile) {
        AtFtpNotifyRequest atData = FtpManager.createMessage(groupId, fileCode, ftpPath, ftpFile);

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.actionRequest(atData);

        FtpManager.ready(sessionId, ftpFile, String.valueOf(ByteHelper.unsigned(fileCode)), ftpPath);

        return relayTimsMessage(sessionId, timsMessage, false);
    }
}
