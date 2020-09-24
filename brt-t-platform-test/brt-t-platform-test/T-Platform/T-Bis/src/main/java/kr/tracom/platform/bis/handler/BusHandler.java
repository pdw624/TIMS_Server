package kr.tracom.platform.bis.handler;

import kr.tracom.platform.attribute.BisAtCode;
import kr.tracom.platform.attribute.bis.AtBusEvent;
import kr.tracom.platform.attribute.bis.AtFrontRearBus;
import kr.tracom.platform.attribute.bis.AtFrontRearBusItem;
import kr.tracom.platform.bis.dao.BisMapper;
import kr.tracom.platform.bis.domain.*;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.payload.PlCode;
import kr.tracom.platform.net.protocol.payload.PlEventRequest;
import kr.tracom.platform.net.protocol.payload.PlGetResponse;
import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.manager.CodeManager;
import kr.tracom.platform.service.manager.ErrorManager;
import kr.tracom.platform.service.manager.FtpManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/*
정류장 도착	    1	정류장 ID
정류장 출발	    2	정류장 ID
기점 도착   	    3	기점 ID
기점 출발	        4	기점 ID
종점 도착       	5	종점 ID
종점 출발	        6	종점 ID
교차로 통과	    7	교차로 ID
가상지점 통과	    8	가상지점 ID
특정 이벤트
단말기 시동	    11	N/A
단말기 종료	    12	N/A
정주기	        13	N/A
운행위반 이벤트
무정차 주행	    21	정류장 ID
과속 주행	        22	위반 속도(Km/h)
회차 위반	        23	N/A
돌발 이벤트
차량 고장	        31	N/A
차량 사고	        32	N/A
차내 폭력 사고	33	N/A
강도	            34	N/A
테러	            35	N/A
*/

public class BusHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataHandler parentHandler;

    public BusHandler(DataHandler parentHandler) {
        this.parentHandler = parentHandler;
    }

    public void handle(TcpChannelMessage tcpChannelMessage) {
        TimsMessage timsMessage = tcpChannelMessage.getMessage();
        byte opCode = timsMessage.getHeader().getOpCode();

        if(opCode == PlCode.OP_GET_RES) {
            PlGetResponse payload = (PlGetResponse)timsMessage.getPayload();
            for(int i = 0; i<payload.getAttrCount(); i++) {

            }
        } else if(opCode == PlCode.OP_EVENT_REQ) {
            PlEventRequest payload = (PlEventRequest)timsMessage.getPayload();
            for(int i = 0; i<payload.getAttrCount(); i++) {
                AtMessage atMessage = payload.getAttrList().get(i);

                if(atMessage.getAttrId() == BisAtCode.BUS_EVENT) {
                    AtBusEvent atData = (AtBusEvent) atMessage.getAttrData();

                    htBusEvent(parentHandler.getLauncher(), atData);
                }
            }

            // 이벤트 처리 완료 후에 파일전송할 항목이 있는지 체크한다.
            FtpManager.checkFileTransfer(CodeManager.RoutingGroupId.BUS.getValue(),
                    parentHandler.getLauncher().getTimsConfig(),
                    tcpChannelMessage.getSession().getSessionId());
        }
    }

    public void htBusEvent(ServiceLauncher launcher, AtBusEvent busEvent) {
        PlatformDao platformDao = new PlatformDao();
        try {
            platformDao.insert(BisMapper.BUS_INSERT_EVENT, busEvent.toMap());

            String eventCode = busEvent.getEventCode();

            if ("31".equals(eventCode) || "32".equals(eventCode) || "33".equals(eventCode) ||
                    "34".equals(eventCode) || "35".equals(eventCode)) {
                updateBusAlarm(platformDao, busEvent);
            } else {
                if("1".equals(busEvent.getRunType()) || "2".equals(busEvent.getRunType()) || "3".equals(busEvent.getRunType())) {
                    updateBusLocation(platformDao, busEvent);
                }

                if ("1".equals(eventCode)) { // 정류장 도착
                    linkSpeed(platformDao, busEvent, 1);

                    rtBusService(platformDao, busEvent);
                    rtStationService(platformDao, busEvent);
                } else if ("2".equals(eventCode)) { // 정류장 출발
                    linkSpeed(platformDao, busEvent, 2);

                    rtBusService(platformDao, busEvent);
                    rtStationService(platformDao, busEvent);
                } else if ("3".equals(eventCode)) { // 기점 도착
                    linkSpeed(platformDao, busEvent, 1);

                    rtBusService(platformDao, busEvent);
                    rtStationService(platformDao, busEvent);
                } else if ("4".equals(eventCode)) { // 기점 출발

                    rtBusService(platformDao, busEvent);
                    rtStationService(platformDao, busEvent);
                } else if ("5".equals(eventCode)) { // 종점 도착
                    linkSpeed(platformDao, busEvent, 1);

                    rtBusService(platformDao, busEvent);
                    rtStationService(platformDao, busEvent);
                } else if ("6".equals(eventCode)) { // 종점 출발

                } else if ("7".equals(eventCode)) { // 교차로 통과
                    linkSpeed(platformDao, busEvent, 1);

                } else if ("8".equals(eventCode)) { // 가상지점 통과

                }

                else if ("11".equals(eventCode)) { // 단말기 시동

                } else if ("12".equals(eventCode)) { // 단말기 종료

                } else if ("13".equals(eventCode)) { // 정주기
                    rtBusService(platformDao, busEvent);
                    rtStationService(platformDao, busEvent);
                }

                else if ("21".equals(eventCode)) { // 무정차 주행
                    linkSpeed(platformDao, busEvent, 1);
                } else if ("22".equals(eventCode)) { // 과속 주행

                } else if ("23".equals(eventCode)) { // 회차 위반

                }
            }
        } catch (Exception e) {
            //String log = String.format("%s : %s", DateTime.now().toString(PlatformConfig.PLF_TIME_FORMAT), busEvent.getLog());
            //FileLogManager.writeLog(AppConfig.getApplicationPath() + Constants.LOG_PATH, launcher.ServiceId, log);

            ErrorManager.trace(launcher.ServiceId,
                    BusHandler.class.getName(), Thread.currentThread().getStackTrace(),
                    busEvent.getLog(), ErrorManager.getStackTrace(e));

            logger.error(ErrorManager.getStackTrace(e));
        }
    }

    private void rtBusService(PlatformDao platformDao, AtBusEvent busEvent) {
        String routeId = busEvent.getRouteId();
        String busId = busEvent.getBusId();
        String impId = busEvent.getImpId();

        PrBusService prFrontRearBus = (PrBusService)platformDao.select(BisMapper.PR_FRONT_REAR_BUS,
                platformDao.buildMap("ROUTE_ID", routeId, "BUS_ID", busId));

        if(prFrontRearBus != null) {
            AtFrontRearBus atRoot = new AtFrontRearBus();
            atRoot.setImpId(impId);

            if(prFrontRearBus.getPprvBusId() != null) {
                AtFrontRearBusItem atItem = new AtFrontRearBusItem();
                atItem.setLocationType("1");
                atItem.setBusName(prFrontRearBus.getPprvBusName());
                atItem.setNodeName(prFrontRearBus.getPprvStnName());
                atItem.setRelativeSeq(prFrontRearBus.getPprvRelSeq());
                atItem.setGapDistance(prFrontRearBus.getPprvDist());
                atItem.setGapTime((byte)prFrontRearBus.getPprvTime());

                atRoot.addItem(atItem);
            }
            if(prFrontRearBus.getPrevBusId() != null) {
                AtFrontRearBusItem atItem = new AtFrontRearBusItem();
                atItem.setLocationType("2");
                atItem.setBusName(prFrontRearBus.getPrevBusName());
                atItem.setNodeName(prFrontRearBus.getPrevStnName());
                atItem.setRelativeSeq(prFrontRearBus.getPrevRelSeq());
                atItem.setGapDistance(prFrontRearBus.getPrevDist());
                atItem.setGapTime((byte)prFrontRearBus.getPrevTime());

                atRoot.addItem(atItem);
            }
            if(prFrontRearBus.getNextBusId() != null) {
                AtFrontRearBusItem atItem = new AtFrontRearBusItem();
                atItem.setLocationType("3");
                atItem.setBusName(prFrontRearBus.getNextBusName());
                atItem.setNodeName(prFrontRearBus.getNextStnName());
                atItem.setRelativeSeq(prFrontRearBus.getNextRelSeq());
                atItem.setGapDistance(prFrontRearBus.getNextDist());
                atItem.setGapTime((byte)prFrontRearBus.getNextTime());

                atRoot.addItem(atItem);
            }
            if(prFrontRearBus.getNnxtBusId() != null) {
                AtFrontRearBusItem atItem = new AtFrontRearBusItem();
                atItem.setLocationType("4");
                atItem.setBusName(prFrontRearBus.getNnxtBusName());
                atItem.setNodeName(prFrontRearBus.getNnxtStnName());
                atItem.setRelativeSeq(prFrontRearBus.getNnxtRelSeq());
                atItem.setGapDistance(prFrontRearBus.getNnxtDist());
                atItem.setGapTime((byte)prFrontRearBus.getNnxtTime());

                atRoot.addItem(atItem);
            }

            if(atRoot.getCount() > 0) {
                TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(impId);

                if (tcpChannelSession != null) {
                    TimsMessageBuilder builder = new TimsMessageBuilder(parentHandler.getLauncher().getTimsConfig());
                    TimsMessage timsMessage = builder.eventRequest(atRoot);

                    TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
                            tcpChannelSession.getChannel(),
                            tcpChannelSession.getSession(),
                            timsMessage);

                    tcpChannelMessage.setResponse(true);

                    TransactionManager.write(tcpChannelMessage);
                }
            }

            if(prFrontRearBus.getPrevBusId() != null || prFrontRearBus.getNextBusId() != null) {
                prFrontRearBus.setSystemDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT + "SS"));
                platformDao.insert(BisMapper.PR_INSERT_BUS_SERVICE, prFrontRearBus.toMap());
            }
        }
    }

    // 실시간 정류장 제공정보 전송
    private void rtStationService(PlatformDao platformDao, AtBusEvent busEvent) {
        String routeId = busEvent.getRouteId();
        String busId = busEvent.getBusId();

        parentHandler.getStationHandler().eventBitService(platformDao, routeId, busId);
    }

    private void linkSpeed(PlatformDao platformDao, AtBusEvent busEvent, int range) {
        HtBusEvent prevEvent = findLocation(platformDao, busEvent, range);

        if(prevEvent == null) {
            /*
            logger.debug("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            logger.debug("AtBusEvent : " + busEvent + "," + locationSequence);
            */
        } else {
            // 지나온 링크 조회
            MtLink mtLink = findLink(platformDao, prevEvent.getEventData(), busEvent.getEventData());

            if(mtLink != null) {
                DateTime prevDt = DateTime.parse(prevEvent.getGpsDateTime(), DateTimeFormat.forPattern("yyyyMMddHHmmssSS"));
                DateTime nowDt = DateTime.parse(busEvent.getGpsTimeStamp(), DateTimeFormat.forPattern("yyyyMMddHHmmssSS"));

                int travelTime = Seconds.secondsBetween(prevDt, nowDt).getSeconds();
                double travelSpeed = ((double)mtLink.getLinkLength() / (double)travelTime) * 3.6;
                int stopTime = busEvent.getStopTime();

                ItLinkSpeed itlinkSpeed = new ItLinkSpeed();
                itlinkSpeed.setLinkId(mtLink.getId());
                itlinkSpeed.setSpeed((int) travelSpeed);
                itlinkSpeed.setRunTime(travelTime);
                itlinkSpeed.setStopTime(stopTime);
                itlinkSpeed.setUpdateDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

                platformDao.insert(BisMapper.IT_LINK_SPEED, itlinkSpeed.toMap());
            }
        }
    }

    private void updateBusLocation(PlatformDao platformDao, AtBusEvent busEvent) {
        // 정류장 위치 정보 조회
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ROUTE_ID", busEvent.getRouteId());
        paramMap.put("NODE_SEQ", busEvent.getEventSequence());

        TmLocation tmLocation = (TmLocation)platformDao.select(BisMapper.PR_SELECT_LOCATION, paramMap);

        ItBusLocation itBusLocation = new ItBusLocation();
        itBusLocation.setBusId(busEvent.getBusId());
        itBusLocation.setImpId(busEvent.getImpId());
        itBusLocation.setRouteId(busEvent.getRouteId());
        itBusLocation.setNodeSequence(busEvent.getEventSequence());
        itBusLocation.setNodeId(busEvent.getEventData());
        itBusLocation.setLinkId(busEvent.getLinkId());
        itBusLocation.setRunType(busEvent.getRunType());
        itBusLocation.setLatitude(convertCoordinate(busEvent.getLatitude()));
        itBusLocation.setLongitude(convertCoordinate(busEvent.getLongitude()));
        itBusLocation.setGpsDateTime(busEvent.getGpsTimeStamp());
        itBusLocation.setSystemDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

        if(tmLocation != null) {
            //정류장 이벤트가 아니면
            itBusLocation.setStationSequence(tmLocation.getSequence());
            itBusLocation.setStationId(tmLocation.getId());

            platformDao.update(BisMapper.PR_UPDATE_BUS_STATION_EVENT, itBusLocation.toMap());
        } else {
            itBusLocation.setStationSequence(0);
            itBusLocation.setStationId("-");

            platformDao.update(BisMapper.PR_UPDATE_BUS_NODE_EVENT, itBusLocation.toMap());
        }
    }

    private void updateBusAlarm(PlatformDao platformDao, AtBusEvent busEvent) {
        ItBusLocation itBusLocation = new ItBusLocation();
        itBusLocation.setBusId(busEvent.getBusId());
        itBusLocation.setImpId(busEvent.getImpId());
        itBusLocation.setRouteId(busEvent.getRouteId());
        itBusLocation.setLatitude(convertCoordinate(busEvent.getLatitude()));
        itBusLocation.setLongitude(convertCoordinate(busEvent.getLongitude()));
        itBusLocation.setGpsDateTime(busEvent.getGpsTimeStamp());
        itBusLocation.setAlarmType(busEvent.getEventCode());
        itBusLocation.setAlarmDateTime(busEvent.getGpsTimeStamp());
        itBusLocation.setSystemDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

        platformDao.update(BisMapper.PR_UPDATE_BUS_ALARM_EVENT, itBusLocation.toMap());
    }

    private HtBusEvent findLocation(PlatformDao platformDao, AtBusEvent busEvent, int range) {
        DateTime endDateTime = DateTime.parse(busEvent.getGpsTimeStamp(), DateTimeFormat.forPattern("yyyyMMddHHmmssSS"));
        DateTime startDateTime = endDateTime.minusMinutes(10);
        // 이전 위치 조회
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ST_DT", startDateTime.toString("yyyyMMddHHmmssSS"));
        paramMap.put("ED_DT", endDateTime.toString("yyyyMMddHHmmssSS"));
        paramMap.put("BUS_ID", busEvent.getBusId());
        paramMap.put("ROUTE_ID", busEvent.getRouteId());
        paramMap.put("LOCATION_SEQ", range);

        HtBusEvent prevEvent = (HtBusEvent)platformDao.select(BisMapper.BUS_SELECT_EVENT, paramMap);

        return prevEvent;
    }

    private MtLink findLink(PlatformDao platformDao, String fromNodeId, String toNodeId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ST_NODE_ID", fromNodeId);
        paramMap.put("ED_NODE_ID", toNodeId);

        MtLink mtLink = (MtLink) platformDao.select(BisMapper.MT_SELECT_LINK, paramMap);

        return mtLink;
    }

    private double convertCoordinate(int coord) {
        return (double)coord / 1000000.0;
    }
}
