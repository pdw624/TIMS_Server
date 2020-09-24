package kr.tracom.platform.launcher.module;

import kr.tracom.platform.common.module.ModuleInterface;
import kr.tracom.platform.launcher.channel.TChannelServer;
import kr.tracom.platform.net.config.TimsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TChannelModule implements ModuleInterface {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /* singleton */
    private static class SingletonHolder {
        public static final TChannelModule INSTANCE = new TChannelModule();
    }

    public static TChannelModule getInstance() {
        return SingletonHolder.INSTANCE;
    }
	/* singleton */

    private TChannelServer commServer = null;
    private TimsConfig timsConfig;

    @Override
    public void init(Object args) {
        this.timsConfig = (TimsConfig)args;
    }

    @Override
    public void startup() {
        commServer = new TChannelServer(this.timsConfig);
        commServer.run();

        String logMsg = "\r\n\r\n";
        logMsg += "===================================================================\r\n";
        logMsg += "Channel module startup !!!\r\n";
        logMsg += "===================================================================\r\n";
        logger.info(logMsg);
    }

    @Override
    public void shutdown() {
        commServer.shutdown();

        String logMsg = "\r\n\r\n";
        logMsg += "===================================================================\r\n";
        logMsg += "Channel module shutdown !!!\r\n";
        logMsg += "===================================================================\r\n";
        logger.info(logMsg);
    }
}
