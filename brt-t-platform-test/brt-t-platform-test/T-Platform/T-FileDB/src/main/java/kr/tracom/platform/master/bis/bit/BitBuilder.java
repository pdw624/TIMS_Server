package kr.tracom.platform.master.bis.bit;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.master.base.BaseFile;
import kr.tracom.platform.master.bis.BisFileCode;
import kr.tracom.platform.net.util.ByteHelper;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.ServiceDao;
import kr.tracom.platform.service.domain.MtServer;
import kr.tracom.platform.service.manager.CodeManager;
import kr.tracom.platform.service.manager.FtpManager;
import kr.tracom.platform.service.model.json.*;
import kr.tracom.platform.tcp.helper.FtpHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class BitBuilder extends BaseFile {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BitBuilder(String serviceId, String applyDateTime, SqlSessionFactory sqlSessionFactory) {
        super.serviceId = serviceId;
        super.serviceDao = new ServiceDao(sqlSessionFactory);
        super.versionNumber = "-";
        super.prefix = "BIT";
        super.dbType = "maria";
        super.applyDateTime = applyDateTime;

        workingDir = String.format("%s/%s/%s", Constants.TEMP_PATH, serviceId, DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));
        absoluteDir = AppConfig.getApplicationPath() + workingDir;

        /*
        DbHelper.addMapper(sqlSessionFactory,
                AppConfig.getClasspath(String.format("%s/bis-bit-%s.xml", Constants.DB_PATH, dbType)));
        */

        File f = new File(absoluteDir);
        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }

    @Override
    public String execute(Object args) {
        JsonData jsonData = (JsonData) args;
        String typeCode = null;
        String parentPath = null;
        String localPath = null;
        String remotePath, remoteFullPath;

        try {
            if(jsonData.getType() == CodeManager.FtpFile.BIT_CONFIG.getValue()) {
                // 설정
                //buildBitMonitor(applyDateTime);
                typeCode = "CF";
                parentPath = "CONFIG";
            } else if(jsonData.getType() == CodeManager.FtpFile.BIT_SCENARIO.getValue()) {
                // 시나리오
                JsonScenarioRoot scenarioRoot = (JsonScenarioRoot)jsonData.getData();
                localPath = buildBitScenario(applyDateTime, scenarioRoot);
                typeCode = "SN";
                parentPath = "SCENARIO/" + scenarioRoot.getDeviceId();
            } else if(jsonData.getType() == CodeManager.FtpFile.BIT_MONITOR.getValue()) {
                // 모니터
                JsonMonitorRoot jsonMonitorRoot = (JsonMonitorRoot)jsonData.getData();
                localPath = buildBitMonitor(applyDateTime, jsonMonitorRoot);
                typeCode = "MO";
                parentPath = "CONFIG";
            } else if(jsonData.getType() == CodeManager.FtpFile.BIT_ILLUMINATION.getValue()) {
                // 조도
                JsonIlluminationRoot jsonIlluminationRoot = (JsonIlluminationRoot)jsonData.getData();
                localPath = buildBitIllumination(applyDateTime, jsonIlluminationRoot);
                typeCode = "IL";
                parentPath = "CONFIG";
            }

            if(localPath == null) {
                return "";
            }

            //localPath = FileUtil.combine(absoluteDir, String.format("/%s_%s_%s.zip", prefix, typeCode, applyDateTime));
            //remotePath = String.format("/%s/%s/%s_%s_%s.zip", prefix, parentPath, prefix, typeCode, applyDateTime);
            remotePath = String.format("/%s/%s/", prefix, parentPath);
            remoteFullPath = String.format("/%s/%s/%s_%s_%s.DAT", prefix, parentPath, prefix, typeCode, applyDateTime);

            //ZipUtil.compressDirectory(absoluteDir, fileExt, localPath);
            //logger.info("compress zip " + localPath);

            MtServer ftpCenter = FtpManager.getFtpConfig(CodeManager.RoutingGroupId.CTR.getValue());

            FtpHelper ftpHelper = new FtpHelper();
            if(ftpHelper.open(ftpCenter.getServerIp(), ftpCenter.getServerPort())) {
                if(ftpHelper.login(ftpCenter.getUserId(), ftpCenter.getUserPw())) {
                    ftpHelper.setActiveMode(false);
                    ftpHelper.makeDirectory(remotePath);
                    ftpHelper.upload(localPath, remoteFullPath);
                }
            }
            ftpHelper.close();

            logger.info("ftp upload " + remoteFullPath);

            //FileUtil.deleteByExtension(absoluteDir + File.separator, fileExt);

            logger.info("file delete " + absoluteDir);

            return remoteFullPath;
        } catch (Exception ex) {
            logger.error(ex.getMessage());

            return "";
        }
    }

    public String buildBitScenario(String applyDateTime, JsonScenarioRoot scenarioRoot) {
        ScenarioFile scenarioFile = new ScenarioFile();
        scenarioFile.setApplyDateTime(applyDateTime);
        scenarioFile.setCount(scenarioRoot.getCount());

        for(JsonScenarioForm jForm : scenarioRoot.getItems()) {
            ScenarioForm form = new ScenarioForm();
            form.setFormType(jForm.getFormType());
            form.setFormCount(jForm.getFormCount());

            for(JsonScenarioItem jItem : jForm.getItems()) {
                ScenarioItem item = new ScenarioItem();
                item.setOrder(jItem.getOrder());
                item.setFileName(jItem.getFileName());
                item.setRunTime(jItem.getDisplaySeconds());

                form.addItem(item);
            }

            scenarioFile.addItem(form);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(scenarioFile.getSize());

        scenarioFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.BIT_SCENARIO, applyDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String buildBitMonitor(String applyDateTime, JsonMonitorRoot jsonMonitorRoot) {
        MonitorFile monitorFile = new MonitorFile();
        monitorFile.setApplyDateTime(applyDateTime);
        monitorFile.setCount(jsonMonitorRoot.getCount());

        for(JsonMonitorItem jItem : jsonMonitorRoot.getItems()) {
            MonitorItem item = new MonitorItem();
            item.setStartDate(jItem.getStartDate());
            item.setEndDate(jItem.getEndDate());
            item.setOnTime(jItem.getStartTime());
            item.setOffTime(jItem.getEndTime());

            monitorFile.addItem(item);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(monitorFile.getSize());

        monitorFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.BIT_MONITOR, applyDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());

            //String localPath = absoluteDir + String.format("/%s_%s_%s.zip", prefix, BisFileCode.CODE_GROUP, applyDateTime);
            //ZipUtil.compressFile(fileName, localPath);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String buildBitIllumination(String applyDateTime, JsonIlluminationRoot jsonIlluminationRoot) {
        IlluminationFile illuminationFile = new IlluminationFile();
        illuminationFile.setApplyDateTime(applyDateTime);
        illuminationFile.setCount(jsonIlluminationRoot.getCount());

        for(JsonIlluminationItem jItem : jsonIlluminationRoot.getItems()) {
            IlluminationItem item = new IlluminationItem();
            item.setStartDate(jItem.getStartDate());
            item.setEndDate(jItem.getEndDate());
            item.setOnTime(jItem.getStartTime());
            item.setOffTime(jItem.getEndTime());
            item.setValue(jItem.getValue());

            illuminationFile.addItem(item);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(illuminationFile.getSize());

        illuminationFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.BIT_ILLUMINATION, applyDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
