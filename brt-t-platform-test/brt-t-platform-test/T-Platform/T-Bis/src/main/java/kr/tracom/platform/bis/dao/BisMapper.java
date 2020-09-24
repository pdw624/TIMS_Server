package kr.tracom.platform.bis.dao;

public class BisMapper {
	public static final String BIS_NAME_SPACE = "Bis.";

	public static String UPDATE_SESSION = BIS_NAME_SPACE + "updateSession";

	public static final String MT_IMP = BIS_NAME_SPACE + "masterImp";
	public static final String MT_BIT = BIS_NAME_SPACE + "masterBit";
	public static final String MT_ROUTE = BIS_NAME_SPACE + "masterRoute";
	public static final String MT_ROUTE_PLAN = BIS_NAME_SPACE + "masterRoutePlan";
	public static final String MT_NODE = BIS_NAME_SPACE + "masterNode";
	public static final String MT_LINK = BIS_NAME_SPACE + "masterLink";
	public static final String MT_STATION = BIS_NAME_SPACE + "masterStation";
	public static final String MT_ROUTE_NODE = BIS_NAME_SPACE + "masterRouteNode";
	public static final String MT_ROUTE_STATION = BIS_NAME_SPACE + "masterRouteStation";
	public static final String MT_ROUTE_LINK = BIS_NAME_SPACE + "masterRouteLink";
	public static final String MT_LINK_VERTEX = BIS_NAME_SPACE + "masterLinkVertex";
	public static final String MT_ROUTE_NODE_STATION = BIS_NAME_SPACE + "masterRouteNodeStation";

	public static final String MT_ROUTE_BIT = BIS_NAME_SPACE + "selectRouteBit";

	public static final String MT_CREATE_ROUTE_BUS = BIS_NAME_SPACE + "createRouteBus";
	public static final String MT_CREATE_LINK_TIME = BIS_NAME_SPACE + "createLinkTime";
	public static final String MT_CREATE_PRDT_TIME = BIS_NAME_SPACE + "createPredictionTime";

	public static final String PR_FRONT_REAR_BUS = BIS_NAME_SPACE + "selectFrontRearBus";
	public static final String PR_ROUTE_BUS_LOCATION = BIS_NAME_SPACE + "selectRouteBusLocation";

	public static String BUS_SELECT_EVENT = BIS_NAME_SPACE + "selectBusEvent";
	public static String BUS_INSERT_EVENT = BIS_NAME_SPACE + "insertBusEvent";
	public static String BUS_DELETE_EVENT = BIS_NAME_SPACE + "deleteBusEvent";

	public static String BUS_SELECT_ALARM = BIS_NAME_SPACE + "selectBusAlarm";

	public static String MT_SELECT_LINK = BIS_NAME_SPACE + "selectLink";
	public static String IT_LINK_SPEED = BIS_NAME_SPACE + "updateLinkSpeed";

	public static String PR_SELECT_LOCATION = BIS_NAME_SPACE + "selectStationEvent";
	public static String PR_UPDATE_BUS_STATION_EVENT = BIS_NAME_SPACE + "updateBusStationEvent";
	public static String PR_UPDATE_BUS_NODE_EVENT = BIS_NAME_SPACE + "updateBusNodeEvent";
	public static String PR_UPDATE_BUS_ALARM_EVENT = BIS_NAME_SPACE + "updateBusAlarmEvent";
	public static String PR_UPDATE_BUS_ALARM_OFF = BIS_NAME_SPACE + "updateBusAlarmOff";

	public static String PR_INSERT_BUS_SERVICE = BIS_NAME_SPACE + "insertBusService";
	public static String PR_INSERT_BIT_SERVICE = BIS_NAME_SPACE + "insertBitService";
}
