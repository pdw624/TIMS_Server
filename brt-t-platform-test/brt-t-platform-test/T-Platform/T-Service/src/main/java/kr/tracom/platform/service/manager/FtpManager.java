package kr.tracom.platform.service.manager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.tracom.platform.attribute.brt.AtFtpListNotifyRequest;
import kr.tracom.platform.attribute.common.AtFtpNotifyRequest;
import kr.tracom.platform.common.util.StringUtil;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.domain.HtFileTransfer;
import kr.tracom.platform.service.domain.MtServer;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;

public class FtpManager {
    private static final Logger logger = LoggerFactory.getLogger(FtpManager.class);

    public static MtServer getFtpConfig(int groupId) {
        PlatformDao platformDao = new PlatformDao();
        MtServer ftpServer = (MtServer) platformDao.select(PlatformMapper.SERVER_INFO,
                platformDao.buildMap("PLF_ID", PlatformConfig.PLF_ID, "GRP_ID", String.valueOf(groupId)));

        return ftpServer;
    }

    public static AtFtpNotifyRequest createMessage(int groupId, byte fileCode, String ftpPath, String ftpFile) {
        MtServer ftpInfo = FtpManager.getFtpConfig(groupId);

        AtFtpNotifyRequest ftpRequest = new AtFtpNotifyRequest();
        ftpRequest.setFtpIp(ftpInfo.getServerIp());
        ftpRequest.setFtpPort((short)ftpInfo.getServerPort());
        ftpRequest.setUserId(ftpInfo.getUserId());
        ftpRequest.setPassword(ftpInfo.getUserPw());
        ftpRequest.setTransferMode((byte)0x01);
        ftpRequest.setEncryption((byte)0);
        ftpRequest.setOperation((byte)0);
        ftpRequest.setFileAttribute(fileCode);
        ftpRequest.setSourcePath(ftpPath);
        ftpRequest.setSourceFile(ftpFile);
        ftpRequest.setDestinationPath("");
        ftpRequest.setDestinationFile(ftpFile);
        
        return ftpRequest;
    }
    
    // brt sftp 서버 정보
    public static AtFtpListNotifyRequest createMessage() throws UnknownHostException {
    	MtServer ftpInfo = FtpManager.getFtpConfig(4);
    	
    	AtFtpListNotifyRequest ftpRequest = new AtFtpListNotifyRequest();
    	ftpRequest.setFtpIp(InetAddress.getByName(ftpInfo.getServerIp()).getHostAddress());
        ftpRequest.setFtpPort((short)ftpInfo.getServerPort());
        ftpRequest.setUserId(ftpInfo.getUserId());
        ftpRequest.setPassword(ftpInfo.getUserPw());
        ftpRequest.setProtocol((byte)0x01);
        ftpRequest.setTransferMode((byte)0x01);
        ftpRequest.setEncryption((byte)0x00);
        ftpRequest.setOperation((byte)0x10);
        ftpRequest.setFileAttribute((byte)0x00);
        
        return ftpRequest;
    }
    
    // brt sftp log 파일 Upload 정보
    public static AtFtpListNotifyRequest createUploadMessage() throws UnknownHostException {
    	MtServer ftpInfo = FtpManager.getFtpConfig(4);
    	
    	AtFtpListNotifyRequest ftpRequest = new AtFtpListNotifyRequest();
    	ftpRequest.setFtpIp(InetAddress.getByName(ftpInfo.getServerIp()).getHostAddress());
        ftpRequest.setFtpPort((short)ftpInfo.getServerPort());
        ftpRequest.setUserId(ftpInfo.getUserId());
        ftpRequest.setPassword(ftpInfo.getUserPw());
        ftpRequest.setProtocol((byte)0x01);
        ftpRequest.setTransferMode((byte)0x01);
        ftpRequest.setEncryption((byte)0x00);
        ftpRequest.setOperation((byte)0x01);
        ftpRequest.setFileAttribute((byte)0x00);
        
        return ftpRequest;
    }

    public static List<Object> selectList(String sessionId) {
        PlatformDao platformDao = new PlatformDao();
        List<Object> items = platformDao.selectList(PlatformMapper.FILE_TRANSFER_SELECT,
                platformDao.buildMap("PLF_ID", PlatformConfig.PLF_ID, "SESSION_ID", sessionId, "SEND_STATE",
                        CodeManager.FileTransferState.FILE_READY.getValue()));

        return items;
    }

    public static List<Object> selectAll() {
        PlatformDao platformDao = new PlatformDao();
        List<Object> items = platformDao.selectList(PlatformMapper.FILE_TRANSFER_LIST,
                platformDao.buildMap("PLF_ID", PlatformConfig.PLF_ID));

        return items;
    }

    public static void ready(String sessionId, String fileName, String fileCode, String filePath) {
        PlatformDao platformDao = new PlatformDao();
        try {
            platformDao.update(PlatformMapper.FILE_TRANSFER_INSERT,
                    platformDao.buildMap(
                            "PLF_ID", PlatformConfig.PLF_ID,
                            "SESSION_ID", sessionId,
                            "FILE_NAME", fileName,
                            "FILE_CD", fileCode,
                            "SEND_TYPE", CodeManager.FileTransferType.FTP.getValue(),
                            "FILE_PATH", filePath,
                            "FILE_SIZE", "0",
                            "FILE_POINTER", "0",
                            "SEND_STATE", CodeManager.FileTransferState.FILE_READY.getValue(),
                            "SEND_ST_DT", "",
                            "SEND_ED_DT", "",
                            "SYS_DT", DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT))
            );
        } catch (Exception e) {
            logger.error(ErrorManager.getStackTrace(e));
        }
    }

    public static void downloading(String sessionId, String fileName) {
        PlatformDao platformDao = new PlatformDao();

        try {
            platformDao.update(PlatformMapper.FILE_TRANSFER_SENDING,
                    platformDao.buildMap(
                            "SEND_STATE", CodeManager.FileTransferState.FILE_SENDING.getValue(),
                            "SEND_ST_DT", DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT),
                            "PLF_ID", PlatformConfig.PLF_ID,
                            "SESSION_ID", sessionId,
                            "FILE_NAME", fileName)
            );
        } catch (Exception e) {
            logger.error(ErrorManager.getStackTrace(e));
        }
    }

    public static void completed(String sessionId, String fileName) {
        PlatformDao platformDao = new PlatformDao();
        try {
            platformDao.update(PlatformMapper.FILE_TRANSFER_SENT,
                    platformDao.buildMap(
                            "SEND_STATE", CodeManager.FileTransferState.FILE_SENT.getValue(),
                            "SEND_ED_DT", DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT),
                            "PLF_ID", PlatformConfig.PLF_ID,
                            "SESSION_ID", sessionId,
                            "FILE_NAME", fileName)
            );
        } catch (Exception e) {
            logger.error(ErrorManager.getStackTrace(e));
        }
    }

    public static boolean checkFileTransfer(int groupId, TimsConfig timsConfig, String sessionId) {
        List<Object> items = FtpManager.selectList(sessionId);

        HtFileTransfer item;
        for(Object obj : items) {
            item = (HtFileTransfer) obj;

            TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(sessionId);

            if(tcpChannelSession != null) {

                FtpManager.ftpNotify(groupId, timsConfig, item, tcpChannelSession);
            }
        }
        return items.size() > 0 ? true : false;
    }

    private static void ftpNotify(int groupId, TimsConfig timsConfig, HtFileTransfer item, TcpChannelSession tcpChannelSession) {
        AtFtpNotifyRequest ftpRequest = createMessage(groupId, StringUtil.stringToByte(item.getFileCode()),
                item.getFilePath(), item.getFileName());

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.actionRequest(ftpRequest);

        TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
                tcpChannelSession.getChannel(),
                tcpChannelSession.getSession(),
                timsMessage);

        tcpChannelMessage.setResponse(true);
        TransactionManager.write(tcpChannelMessage);
    }
}
