package kr.tracom.platform.launcher.manager;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.db.factory.PlatformDbFactory;
import kr.tracom.platform.db.util.DbHelper;
import kr.tracom.platform.launcher.module.TChannelModule;
import kr.tracom.platform.launcher.module.TServiceModule;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.domain.*;
import kr.tracom.platform.service.model.ServiceArgs;
import kr.tracom.platform.service.model.ServiceSetArgs;
import kr.tracom.platform.service.module.TScheduleModule;
import kr.tracom.platform.tcp.util.TcpUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class StartUpManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String platformId;
    private TimsConfig timsConfig;
    private List<ServiceArgs> serviceList;

    public StartUpManager(String platformId) {
        this.platformId = platformId;
        this.serviceList = new ArrayList<>();
    }

    public TimsConfig getTimsConfig() {
        return timsConfig;
    }

    public List<ServiceArgs> getServiceList() {
        return serviceList;
    }

    public ServiceSetArgs getServiceSetArgs() {
        ServiceSetArgs serviceSetArgs = new ServiceSetArgs();
        serviceSetArgs.setPlatformId(platformId);
        serviceSetArgs.setTimsConfig(timsConfig);

        return serviceSetArgs;
    }

    private boolean forgery(Object obj, String checkKey) {
        //TODO : 변조된 값이면 어떻게 할까요??? 이번 버전은 체크안한다.
        //String lockKey = CryptoManager.encode(RuntimeManager.lockString(obj));
        //return !checkKey.equals(lockKey);
        return false;
    }

    public void step1() {
        String dbType = AppConfig.get("platform.db.profile");
        // DB 구성
        SqlSessionFactory sessionFactory = PlatformDbFactory.getSqlSessionFactory();

        DbHelper.addMapper(sessionFactory,
                AppConfig.getClasspath(String.format("%s/platform/sql-%s.xml", Constants.DB_PATH, dbType)));
    }

    public void step2() {
        // 런처 설정 로딩
        PlatformDao platformDao = new PlatformDao();
        MtLauncher mtLauncher = (MtLauncher) platformDao.select(
                PlatformMapper.LAUNCHER_CONFIG,
                platformDao.buildMap("PLF_ID", platformId));

        // 플랫폼 전역 변수 설정
        PlatformConfig.PLF_ID = mtLauncher.getPlatformId();
        PlatformConfig.PLF_IP = mtLauncher.getIp();
        PlatformConfig.PLF_CITY = mtLauncher.getCity();
        PlatformConfig.PLF_LANG = mtLauncher.getLangCode();
        PlatformConfig.PLF_TIME_ZONE = mtLauncher.getTimeZone();
        PlatformConfig.PLF_DT_FORMAT = mtLauncher.getDateTimeFormat();
        PlatformConfig.PLF_DATE_FORMAT = mtLauncher.getDateFormat();
        PlatformConfig.PLF_TIME_FORMAT = mtLauncher.getTimeFormat();
        PlatformConfig.PLF_LOG_LEVEL = mtLauncher.getLogLevel();
        PlatformConfig.PLF_SINCE_DT = mtLauncher.getSinceDate();

        // 로그레벨 설정
        LogManager.setLogLevel(PlatformConfig.PLF_LOG_LEVEL);
    }

    public void step3() {
        PlatformDao platformDao = new PlatformDao();
        MtChannel mtChannel = (MtChannel) platformDao.select(
                PlatformMapper.CHANNEL_CONFIG, platformDao.buildMap("PLF_ID", platformId));

        timsConfig = new TimsConfig();
        timsConfig.setPlatformId(mtChannel.getPlatformId());
        timsConfig.setChannelName(mtChannel.getChannelName());
        timsConfig.setChannelPort(mtChannel.getChannelPort());
        timsConfig.setReadTimeout(mtChannel.getReadTimeout());
        timsConfig.setWriteTimeout(mtChannel.getWriteTimeout());
        timsConfig.setHeaderType(mtChannel.getHeaderType());
        timsConfig.setEndianType(mtChannel.getEndianType());
        timsConfig.setStrEncoding(mtChannel.getStrEncoding());
        timsConfig.setMaxPacketSize(mtChannel.getMaxPacketSize());
        timsConfig.setMaxPacketCount(mtChannel.getMaxPacketCount());
        timsConfig.setIndicator(mtChannel.getIndicator());
        timsConfig.setVersion(mtChannel.getVersion());
        timsConfig.setApduOffset(mtChannel.getApduOffset());
        timsConfig.setRetryCount(mtChannel.getRetryCount());
        timsConfig.setResponseTimeOut(mtChannel.getResponseTimeout());
        timsConfig.setTransferRoute(mtChannel.getTransferRoute());
        timsConfig.setCrcEnable(mtChannel.getCrcEnable());
        timsConfig.setRemoteFlowControl(mtChannel.getRemoteControl());
        timsConfig.setLocalFlowControl(mtChannel.getLocalControl());

        if(!TcpUtil.availablePort(timsConfig.getChannelPort())) {
            System.out.println("Failed to bind port");
            System.exit(-1);
        }
    }

    public void step4() {
        PlatformDao platformDao = new PlatformDao();
        List<Object> scheduleList = platformDao.selectList(PlatformMapper.SCHEDULE_SELECT,
                platformDao.buildMap("APP_ID", platformId));

        TScheduleModule.getInstance().loadPlatformJob(scheduleList);
    }

    public void step5() {
        PlatformDao dao = new PlatformDao();
        List<Object> list = dao.selectList(PlatformMapper.SERVICE_CONFIG, null);

        ServiceArgs app;
        MtServiceApp mtServiceApp;
        for(Object obj : list) {
            mtServiceApp = (MtServiceApp)obj;

            app = new ServiceArgs();
            app.setId(mtServiceApp.getId());
            app.setName(mtServiceApp.getName());
            app.setVersion(mtServiceApp.getVersion());
            app.setAttribute(mtServiceApp.getAttribute());
            app.setLauncher(mtServiceApp.getLauncher());
            app.setEnable(mtServiceApp.getEnable());
            app.setLicenseStartDate(mtServiceApp.getLicenseStartDate());
            app.setLicenseEndDate(mtServiceApp.getLicenseEndDate());

            serviceList.add(app);
        }
    }

    public void step6() {
        PlatformDao platformDao = new PlatformDao();
        List<Object> logList = platformDao.selectList(PlatformMapper.FILE_LOG_SELECT, null);
        MtLogFile logFile;
        String logPath;
        DateTime nowDateTime = DateTime.now().dayOfMonth().roundFloorCopy();

        for(Object obj : logList) {
            logFile = (MtLogFile)obj;
            logPath = AppConfig.getApplicationPath() + logFile.getLogPath();

            FileLogManager.execute(logFile.getPrcsType(), nowDateTime, logPath, logFile.getDeleteDays());
        }
    }

    public void step7() {
        TChannelModule.getInstance().init(getTimsConfig());
        TServiceModule.getInstance().init(getServiceList());
        
        if("direct".equalsIgnoreCase(AppConfig.get("service.startup"))) {
            TServiceModule.getInstance().loadDirect(getServiceSetArgs());
        } else {
            TServiceModule.getInstance().loadDynamic(getServiceSetArgs());
        }
    }

    public void step8() {
        PlatformDao platformDao = new PlatformDao();
        double value;
        ItPlatformStatus platformStatus = (ItPlatformStatus) platformDao.select(PlatformMapper.STATUS_SELECT, null);
        PlatformConfig.paramMap.put("PLF_STATUS", platformStatus);

        value = Double.parseDouble((String)platformDao.select(PlatformMapper.PARAMETER_GETVALUE,
                platformDao.buildMap("APP_ID", platformId, "PARAM_KEY", "MAX_USAGE_CPU")));
        PlatformConfig.paramMap.put("MAX_CPU", value);

        value = Double.parseDouble((String)platformDao.select(PlatformMapper.PARAMETER_GETVALUE,
                platformDao.buildMap("APP_ID", platformId, "PARAM_KEY", "MAX_USAGE_RAM")));
        PlatformConfig.paramMap.put("MAX_RAM", value);

        value = Double.parseDouble((String)platformDao.select(PlatformMapper.PARAMETER_GETVALUE,
                platformDao.buildMap("APP_ID", platformId, "PARAM_KEY", "MAX_USAGE_HDD")));
        PlatformConfig.paramMap.put("MAX_HDD", value);

        value = Double.parseDouble((String)platformDao.select(PlatformMapper.PARAMETER_GETVALUE,
                platformDao.buildMap("APP_ID", platformId, "PARAM_KEY", "MAX_USAGE_JVM")));
        PlatformConfig.paramMap.put("MAX_JVM", value);

        value = Double.parseDouble((String)platformDao.select(PlatformMapper.PARAMETER_GETVALUE,
                platformDao.buildMap("APP_ID", platformId, "PARAM_KEY", "MAX_USAGE_MDB")));
        PlatformConfig.paramMap.put("MAX_MDB", value);

        PlatformConfig.paramMap = Collections.unmodifiableMap(PlatformConfig.paramMap);
    }

    public void display(String mode) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        sb.append("###################################################################\r\n");
        sb.append("Tracom Platform Application Ver1.0.1 [" + DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT) + "]\r\n");

        if("startup".equalsIgnoreCase(mode)) {
            if (TServiceModule.getInstance().getSize() > 0) {
                Iterator<ServiceArgs> serviceIterator = TServiceModule.getInstance().getIterator();
                while (serviceIterator.hasNext()) {
                    ServiceArgs svc = serviceIterator.next();

                    sb.append("  - Service ID      : " + svc.getId() + "\r\n");
                    sb.append("  - Service Name    : " + svc.getName() + "\r\n");
                    sb.append("  - Service Version : " + svc.getVersion() + "\r\n");
                    sb.append("  - Service Class   : " + svc.getLauncher() + "\r\n");
                    sb.append("  - Attribute Path  : " + svc.getAttribute() + "\r\n");
                    sb.append("  - Attribute List  : " + svc.getAttributeList() + "\r\n");
                    sb.append("-------------------------------------------------------------------\r\n");
                }
            }
        } else {
            sb.append("  - Platform Application Shutdown\r\n");
        }
        sb.append("###################################################################\r\n");
        String log = sb.toString();

        logger.info(log);
        System.out.println(log);
    }
}
