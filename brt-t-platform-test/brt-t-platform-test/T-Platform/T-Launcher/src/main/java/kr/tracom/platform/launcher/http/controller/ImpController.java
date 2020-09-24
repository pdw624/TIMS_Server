package kr.tracom.platform.launcher.http.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.attribute.common.AtCommandScriptRequest;
import kr.tracom.platform.attribute.common.AtFtpNotifyRequest;
import kr.tracom.platform.attribute.common.AtPowerControlRequest;
import kr.tracom.platform.attribute.imp.AtProcess;
import kr.tracom.platform.attribute.imp.AtProcessItem;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.common.util.FileUtil;
import kr.tracom.platform.imp.dao.ImpMapper;
import kr.tracom.platform.launcher.TLauncher;
import kr.tracom.platform.launcher.http.builder.JsonBuilder;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.net.util.ByteHelper;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.domain.MtServer;
import kr.tracom.platform.service.manager.CodeManager;
import kr.tracom.platform.service.manager.FtpManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.helper.FtpHelper;

@Path("/imp")
public class ImpController extends ChannelController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TimsConfig timsConfig = TLauncher.getInstance().getTimsConfig();

    private final String FTP_DIR = "/IMP";
    private final int GRP_ID = CodeManager.RoutingGroupId.BUS.getValue();


    @GET
    @Path("/test0718")
    @Produces(MediaType.APPLICATION_JSON)
    public String t() {
    	
    	System.out.println("");
    	return JsonBuilder.getJson(JsonBuilder.SUCS_CD);
    }
    
    @GET
    @Path("/getRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRequest(@QueryParam("attrId") short attrId, @QueryParam("ids") String ids) {
    	
		if(ids == null || ids.isEmpty()) {
		     return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
		}
		
		String[] items = ids.split(",");
		 
		for(String clientId : items) {
			 //getRequest요청을 보내면됨
		
			TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
			TimsMessage timsMessage = builder.getRequest(attrId);
		   
			relayTimsMessage(clientId, timsMessage, true);
		}//for

		return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
    }
    
    @GET
    @Path("/cmdScript")
    @Produces(MediaType.APPLICATION_JSON)
    public String commandScript(@QueryParam("sessionId") String sessionId, @QueryParam("cmd") String command) {
    	    	
    	AtCommandScriptRequest atData = new AtCommandScriptRequest();
        
        atData.setLength((short) command.length());
        atData.setCommand(command);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("SESSION_ID", sessionId);
        map.put("REQ", command);
        PlatformDao dao = new PlatformDao();
        dao.insert(ImpMapper.INSERT_CMD_REQ, map);
        
        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.actionRequest(atData);
        
        String result = relayTimsMessage(sessionId, timsMessage, true).toString();
        return JsonBuilder.getJson(result);
    }//커맨드 받아오는
    
    @GET
    @Path("/cmdLog")
    @Produces(MediaType.APPLICATION_JSON)
    public String cmdLog(@QueryParam("sessionId") String sessionId) {

    	Map<String, Object> map = new HashMap<>();
        map.put("SESSION_ID", sessionId);
        
        PlatformDao dao = new PlatformDao();
        List<Object> list = dao.selectList(ImpMapper.SELECT_CMD_LOG, map);
        
        return JsonBuilder.getJson(list.size(), list);
    }
    
    
    @GET
    @Path("/getImpSession")
    @Produces(MediaType.APPLICATION_JSON)
    public String getImpSession() {
        PlatformDao dao = new PlatformDao();
        List<Object> list = dao.selectList(ImpMapper.SELECT_IMP_SESSION, null);
        return JsonBuilder.getJson(list.size(), list);
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String upload(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("ids") String ids) throws UnsupportedEncodingException {

        String fileName = new String(fileDetail.getFileName().getBytes("8859_1"),"utf-8");
        String parentPath = FileUtil.combine(AppConfig.getApplicationPath(), Constants.TEMP_PATH + "/UPLOAD");

        if(!FileUtil.isExist(parentPath)) {
            FileUtil.makeDirectory(parentPath);
        }

        String localPath = FileUtil.combine(parentPath, fileName);
        String[] items = ids.split(",");

        writeToFile(uploadedInputStream, localPath);

        if(FileUtil.isExist(localPath)) {
            String remotePath = FTP_DIR;
            String remoteFile = FileUtil.getFileName(localPath);
            String remoteFullPath = String.format("%s/%s", FTP_DIR, remoteFile);

            FtpHelper ftpHelper = new FtpHelper();
            try {
                MtServer ftpCenter = FtpManager.getFtpConfig(CodeManager.RoutingGroupId.CTR.getValue());
                if (ftpHelper.open(ftpCenter.getServerIp(), ftpCenter.getServerPort())) {
                    if (ftpHelper.login(ftpCenter.getUserId(), ftpCenter.getUserPw())) {
                        ftpHelper.setActiveMode(false);
                        ftpHelper.makeDirectory(remotePath);
                        ftpHelper.upload(localPath, remoteFullPath);
                    }
                }
                FileUtil.deleteFile(localPath);

                for(String sessionId : items) {
                    ftpSaveAndSend(GRP_ID, sessionId, (byte)0, remotePath, remoteFile);
                }

                return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
            } catch (Exception e) {
                return JsonBuilder.getJson(JsonBuilder.FAIL_CD, e.getMessage());
            } finally {
                ftpHelper.close();
            }
        }

        return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_UNKNOWN);
    }

    @GET
    @Path("/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public String impReset(@QueryParam("ids") String ids) {
        if(ids == null || ids.isEmpty()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }

        String[] items = ids.split(",");
        
        for(String clientId : items) {
            AtPowerControlRequest atData = new AtPowerControlRequest();
            atData.setTargetId(AtPowerControlRequest.IMP_MAIN_POWER);
            atData.setControlType((byte)0x12);
            atData.setDelayTime((byte)0x00);
            atData.setResetLapse((byte)1);

            TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
            TimsMessage timsMessage = builder.actionRequest(atData);

            relayTimsMessage(clientId, timsMessage, true);
        }

        return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
    }

    @GET
    @Path("/getRequest/{sessionId}/{attrId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRequest(@PathParam("sessionId") String sessionId, @PathParam("attrId") short attrId) {
        logger.info("[imp getRequest] " + String.format("/%s/%d", sessionId, attrId));

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.getRequest(attrId);

        boolean bSuccess = getAttributeData(sessionId, timsMessage);
        if(bSuccess) {
            int atId = TransactionManager.transactionSyncObject.getSyncId();
            if(atId == AtCode.IMP_PROCESS_LIST) {
                AtProcess atProcess = (AtProcess) TransactionManager.transactionSyncObject.getSyncData();
                List<Object> list = new ArrayList<>();
                for (AtProcessItem item : atProcess.getItems()) {
                    list.add(item);
                    System.out.println("item : " + item);
                }

                if (atProcess != null) {
                    return JsonBuilder.getJson((int) atProcess.getCount(), list);
                }
            }
        }
        
        return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_UNKNOWN);
    }

    @GET
    @Path("/getSessionHistory")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSessionHistory(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
        logger.info("[imp getSessionHistory]");

        if(startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
            startDate = DateTime.now().toString("yyyyMMdd") + "000000";
            endDate = DateTime.now().toString("yyyyMMdd") + "235959";
        }

        PlatformDao dao = new PlatformDao();
        List<Object> list = dao.selectList(ImpMapper.SELECT_HT_SESSION_1,
                dao.buildMap("ST_DT", startDate, "ED_DT", endDate));

        return JsonBuilder.getJson(list.size(), list);
    }

    @GET
    @Path("/getVersion/{sessionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getVersion(@PathParam("sessionId") String sessionId) {
        logger.info("[imp getVersion]");

        sessionId = sessionId.length() == 10 ? sessionId : null;

        PlatformDao dao = new PlatformDao();
        List<Object> list = dao.selectList(ImpMapper.SELECT_IT_VERSION, dao.buildMap("IMP_ID", sessionId));

        return JsonBuilder.getJson(list.size(), list);
    }

    private void writeToFile(InputStream upStream, String localPath) {
        try {
            OutputStream out;
            int read;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(localPath));
            while ((read = upStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String ftpSaveAndSend(int groupId, String sessionId, byte fileCode, String ftpPath, String ftpFile) {
        AtFtpNotifyRequest atData = FtpManager.createMessage(groupId, fileCode, ftpPath, ftpFile);

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.actionRequest(atData);

        FtpManager.ready(sessionId, ftpFile, String.valueOf(ByteHelper.unsigned(fileCode)), ftpPath);

        return relayTimsMessage(sessionId, timsMessage, false);
    }
}
