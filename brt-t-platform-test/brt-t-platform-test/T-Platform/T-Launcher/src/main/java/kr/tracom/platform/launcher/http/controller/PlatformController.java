package kr.tracom.platform.launcher.http.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import kr.tracom.platform.common.util.StringUtil;
import kr.tracom.platform.db.TDatabaseModule;
import kr.tracom.platform.imp.dao.ImpMapper;
import kr.tracom.platform.launcher.TLauncher;
import kr.tracom.platform.launcher.http.builder.JsonBuilder;
import kr.tracom.platform.launcher.manager.LogManager;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.domain.ItH2DbStatus;
import kr.tracom.platform.service.manager.FtpManager;
import kr.tracom.platform.service.manager.SessionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelSession;

@Path("/admin")
public class PlatformController {
    private final String SESSION_KEY = "SESSION_ID";
    private int SESSION_TIMEOUT = 10 * 60 * 1000;

  

    
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        System.out.println("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
        return "ping";
    }

    @GET
    @Path("/startup")
    @Produces(MediaType.APPLICATION_JSON)
    public String startup() {
        if(!TLauncher.getInstance().getRunning()) {
            TDatabaseModule.getInstance().init(null);
            TLauncher.getInstance().startup();
        }

        return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
    }

    @GET
    @Path("/shutdown")
    @Produces(MediaType.APPLICATION_JSON)
    public String shutdown() {
        if(TLauncher.getInstance().getRunning()) {
            TLauncher.getInstance().shutdown();
            TDatabaseModule.getInstance().shutdown();
        }
        return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
    }

    @GET
    @Path("/restart")
    @Produces(MediaType.APPLICATION_JSON)
    public String restart() {

        synchronized (TLauncher.restartEvent) {
            TLauncher.restartEvent.setSyncId(1);
            TLauncher.restartEvent.notify();
        }

        return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
    }

    @GET
    @Path("/launcherStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public String launcherStatus() {
        boolean isRunning = TLauncher.getInstance().getRunning();
        if(isRunning) {
            return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
        } else {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, "");
        }
    }

    @GET
    @Path("/dbStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public String dbStatus() {
        if(!TLauncher.getInstance().getRunning()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_SHUTDOWN);
        }

        PlatformDao dao = new PlatformDao();
        ItH2DbStatus obj = (ItH2DbStatus)dao.select(PlatformMapper.DB_STATUS, null);
        String used = StringUtil.formatSeperatedByComma(obj.getMemoryUsed());
        String total = StringUtil.formatSeperatedByComma(obj.getMemoryFree() + obj.getMemoryUsed());

        return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE, "used", used, "total", total);
    }

    @GET
    @Path("/getLogLevel")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLogLevel() {
        if(!TLauncher.getInstance().getRunning()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_SHUTDOWN);
        }

        return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE, "loglevel", PlatformConfig.PLF_LOG_LEVEL);
    }

    @GET
    @Path("/setLogLevel/{logLevel}")
    @Produces(MediaType.APPLICATION_JSON)
    public String setLogLevel(@PathParam("logLevel") String logLevel) {
        if(!TLauncher.getInstance().getRunning()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_SHUTDOWN);
        }
        LogManager.setLogLevel(logLevel);
        PlatformConfig.PLF_LOG_LEVEL = logLevel;

        return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE, "loglevel", PlatformConfig.PLF_LOG_LEVEL);
    }

    @GET
    @Path("/session")
    @Produces(MediaType.APPLICATION_JSON)
    public String sessionList() {
        if(!TLauncher.getInstance().getRunning()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_SHUTDOWN);
        }
        System.out.println("in session");
        PlatformDao dao = new PlatformDao();
        //List<Object> list = dao.selectList(PlatformMapper.SESSION_LIST, null);//원본
        List<Object> list = dao.selectList(ImpMapper.SELECT_IMP_SESSION, null);//by jhlee
        return JsonBuilder.getJson(list.size(), list);
    }

    @GET
    @Path("/service")
    @Produces(MediaType.APPLICATION_JSON)
    public String serviceList() {
        if(!TLauncher.getInstance().getRunning()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_SHUTDOWN);
        }

        PlatformDao dao = new PlatformDao();
        List<Object> list = dao.selectList(PlatformMapper.SERVICE_CONFIG, null);

        return JsonBuilder.getJson(list.size(), list);
    }

    @GET
    @Path("/schedule")
    @Produces(MediaType.APPLICATION_JSON)
    public String scheduleList() {
        if(!TLauncher.getInstance().getRunning()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_SHUTDOWN);
        }

        PlatformDao dao = new PlatformDao();
        List<Object> list = dao.selectList(PlatformMapper.SCHEDULE_LIST, null);

        return JsonBuilder.getJson(list.size(), list);
    }

    @GET
    @Path("/transaction")
    @Produces(MediaType.APPLICATION_JSON)
    public String transactionList() {
        if(!TLauncher.getInstance().getRunning()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_SHUTDOWN);
        }

        PlatformDao dao = new PlatformDao();
        List<Object> list = dao.selectList(PlatformMapper.TRANSACTION_SELECT,
                dao.buildMap("PLF_ID", PlatformConfig.PLF_ID));

        return JsonBuilder.getJson(list.size(), list);
    }

    @GET
    @Path("/transaction/delete/{sendState}")
    @Produces(MediaType.APPLICATION_JSON)
    public String transactionDelete(@PathParam("sendState") String sendState) {
        if(sendState.isEmpty()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }
        String param = sendState.equalsIgnoreCase("ALL") ? null : "RESPONSE";

        PlatformDao dao = new PlatformDao();
        int affect = dao.delete(PlatformMapper.TRANSACTION_DELETE,
                dao.buildMap("PLF_ID", PlatformConfig.PLF_ID, "SEND_STATE", param));

        if(affect > 0) {
            return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
        } else {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_UNKNOWN);
        }
    }

    @GET
    @Path("/transfer")
    @Produces(MediaType.APPLICATION_JSON)
    public String transfer() {
        List<Object> list = FtpManager.selectAll();

        return JsonBuilder.getJson(list.size(), list);
    }

    /*191015 JH */
    @GET
    @Path("/powerRefresh")
    @Produces(MediaType.APPLICATION_JSON)
    public String powerRefresh() {
    	PlatformDao dao = new PlatformDao();
    	List<Object> list = dao.selectList(PlatformMapper.SELECT_IMP_ID, null);
    	
    	for(Object o : list) {
    		TcpChannelSession tcs = TcpSessionManager.getTcpChannelSessionById(o.toString());
    		SessionManager.forceClose(tcs.getChannel(), tcs.getSession().getSessionId(), "ADMIN CONTROL");	
    	}
    	return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
    }
    
    @GET
    @Path("/{cmd}/{impId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String action(@PathParam("cmd") String cmd, @PathParam("impId") String impId) {
        if(impId.isEmpty() || cmd.isEmpty()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }

        if(cmd.equalsIgnoreCase("disconnect")) {
            TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(impId);
            if (tcpChannelSession == null) {
                return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_NOT_CONNECTED);
            } else {
                //Debug.log(PlatformController.class.getName(), tcpChannelSession.toString());

                SessionManager.forceClose(tcpChannelSession.getChannel(), tcpChannelSession.getSession().getSessionId(), "ADMIN CONTROL");

                return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
            }
        }
        else {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }
    }

    @GET
    @Path("/transfer/delete/{impId}/{fileName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String fileTransfer(@PathParam("impId") String impId, @PathParam("fileName") String fileName) {
        if(impId.isEmpty() || fileName.isEmpty()) {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_INVALID_PARAM);
        }

        PlatformDao dao = new PlatformDao();
        int affect = dao.delete(PlatformMapper.FILE_TRANSFER_CANCEL,
                dao.buildMap("PLF_ID", PlatformConfig.PLF_ID, "SESSION_ID", impId, "FILE_NAME", fileName));

        if(affect > 0) {
            return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
        } else {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_UNKNOWN);
        }
    }
}
