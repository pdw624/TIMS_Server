package kr.tracom.platform.bis.handler;

import kr.tracom.platform.attribute.bis.AtRouteBusLocation;
import kr.tracom.platform.attribute.bis.AtRouteBusLocationItem;
import kr.tracom.platform.bis.dao.BisMapper;
import kr.tracom.platform.bis.domain.MtRouteBit;
import kr.tracom.platform.bis.domain.PrBitService;
import kr.tracom.platform.bis.domain.VwPredictionTime;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;
import org.joda.time.DateTime;

import java.util.List;

public class StationHandler {

    private DataHandler parentHandler;

    public StationHandler(DataHandler parentHandler) {
        this.parentHandler = parentHandler;
    }

    public void handle(TcpChannelMessage tcpChannelMessage) {

    }

    public void eventBitService(PlatformDao platformDao, String routeId, String busId) {
        List<Object> listRouteBit = platformDao.selectList(BisMapper.MT_ROUTE_BIT,
                platformDao.buildMap("ROUTE_ID", routeId));

        for(Object routeBitObj : listRouteBit) {
            MtRouteBit mtRouteBit = (MtRouteBit)routeBitObj;

            List<Object> listRouteBus = platformDao.selectList(BisMapper.PR_ROUTE_BUS_LOCATION,
                    platformDao.buildMap("ROUTE_ID", routeId, "BIT_SEQ", String.valueOf(mtRouteBit.getBitSeq())));

            AtRouteBusLocation busLocation = new AtRouteBusLocation();
            busLocation.setBitId(mtRouteBit.getBitId());
            busLocation.setRouteId(mtRouteBit.getRouteId());
            busLocation.setCount((byte)listRouteBus.size());

            for(Object routeBusObj : listRouteBus) {
                PrBitService prBitService = (PrBitService)routeBusObj;

                /*
                if(mtRouteBit.getBitSeq() - prRouteBusLocation.getStationSeq() == 0) {
                    progressType = "3";
                } else if(mtRouteBit.getBitSeq() - prRouteBusLocation.getStationSeq() < 3) {
                    progressType = "2";
                } else {
                    progressType = "1";
                }
                */

                AtRouteBusLocationItem locationItem = new AtRouteBusLocationItem();
                locationItem.setStationId(prBitService.getStationId());
                locationItem.setRouteType(prBitService.getRouteType());
                locationItem.setBusType(prBitService.getBusType());
                locationItem.setArrivalType(prBitService.getArrivalType());
                locationItem.setRunType(prBitService.getRunType());
                locationItem.setBusSeq(prBitService.getBusStnSeq());

                busLocation.getList().add(locationItem);

                prBitService.setSystemDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT + "SS"));
                prBitService.setBitId(mtRouteBit.getBitId());

                if(busId.equals(prBitService.getBusId())) {
                    platformDao.insert(BisMapper.PR_INSERT_BIT_SERVICE, prBitService.toMap());
                }
            }

            TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(mtRouteBit.getBitId());

            if(tcpChannelSession != null) {
                TimsMessageBuilder builder = new TimsMessageBuilder(parentHandler.getLauncher().getTimsConfig());
                TimsMessage timsMessage = builder.eventRequest(busLocation);

                TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
                        tcpChannelSession.getChannel(),
                        tcpChannelSession.getSession(),
                        timsMessage);

                tcpChannelMessage.setResponse(true);

                TransactionManager.write(tcpChannelMessage);
            }
        }
    }

    public void sendLocation2(VwPredictionTime currentBus, VwPredictionTime predictionBus,
                            TimsConfig timsConfig, TcpChannelSession tcpChannelSession) {

        AtRouteBusLocation busLocation = new AtRouteBusLocation();
        busLocation.setBitId(predictionBus.getBitId());
        busLocation.setRouteId(currentBus.getRouteId());
        busLocation.setCount((byte)1);

        for(int i=0; i<busLocation.getCount(); i++) {
            AtRouteBusLocationItem locationItem = new AtRouteBusLocationItem();
            locationItem.setStationId(predictionBus.getStationId());
            locationItem.setBusType(currentBus.getBusType());
            locationItem.setBusSeq(predictionBus.getStationSeq());

            busLocation.getList().add(locationItem);
        }

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.eventRequest(busLocation);

        TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
                tcpChannelSession.getChannel(),
                tcpChannelSession.getSession(),
                timsMessage);

        tcpChannelMessage.setResponse(true);

        TransactionManager.write(tcpChannelMessage);
    }
}
