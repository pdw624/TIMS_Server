package kr.tracom.platform.master.bis;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.common.util.ZipUtil;
import kr.tracom.platform.db.util.DbHelper;
import kr.tracom.platform.master.base.BaseFile;
import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.master.bis.v01.*;
import kr.tracom.platform.master.dao.MasterMapper;
import kr.tracom.platform.net.util.ByteHelper;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.dao.ServiceDao;
import kr.tracom.platform.service.manager.VersionManager;
import kr.tracom.platform.service.model.VersionItem;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BisLoader extends BaseFile {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BisLoader(String serviceId, String version, SqlSessionFactory sqlSessionFactory) {
        super.serviceId = serviceId;
        super.serviceDao = new ServiceDao(sqlSessionFactory);
        super.versionNumber = version;
        super.versionName = "MASTER";
        super.prefix = "CMN";
        super.dbType = "h2";

        DbHelper.addMapper(sqlSessionFactory,
                AppConfig.getClasspath(String.format("%s/bis/master-%s-%s.xml", Constants.DB_PATH, dbType, version)));

        absoluteDir = AppConfig.getApplicationPath();
    }

    @Override
    public String execute(Object args) {
        boolean isSuccess;
        try {
            VersionItem item = VersionManager.getItem(serviceId, versionName);
            String zipFile = AppConfig.getApplicationPath() + item.getFilePath();
            applyDateTime = item.getApplyDateTime();

            //workingDir = FileUtil.combine(Constants.TEMP_PATH, applyDateTime);
            workingDir = String.format("%s/%s/%s", Constants.TEMP_PATH, serviceId, applyDateTime);

            absoluteDir += workingDir;

            File f = new File(absoluteDir);
            if(!f.isDirectory()) {
                f.mkdirs();
            }

            ZipUtil.decompress(zipFile, absoluteDir);

            clearDB();

            codeGroupDb(applyDateTime);
            codeDetailDb(applyDateTime);

            busDb(applyDateTime);
            bitDb(applyDateTime);
            routeDb(applyDateTime);
            stationDb(applyDateTime);
            linkDb(applyDateTime);
            nodeDb(applyDateTime);
            routeLinkDb(applyDateTime);
            routeStationDb(applyDateTime);
            routeNodeDb(applyDateTime);
            linkVertexDb(applyDateTime);
            allInfoDb();

            return zipFile;
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage());

            return "";
        }
    }

    private void clearDB() {
        /*
        String[] tables = {"BIS_MT_ROUTE_STATION", "BIS_MT_ROUTE", "BIS_MT_LINK_VERTEX", "BIS_MT_NODE",
                "BIS_MT_BIT", "BIS_MT_LINK", "BIS_MT_ALL_INFO", "BIS_MT_ROUTE_LINK", "BIS_MT_ROUTE_NODE",
                "BIS_MT_BUS", "BIS_MT_STATION", "BIS_MT_ROUTE_PLAN" };
        String[] viewes = {"BIS_VW_ROUTE_BUS", "BIS_VW_PRDT_TIME", "BIS_VW_LINK_TIME"};
        */

        List<Object> list = serviceDao.selectList(PlatformMapper.DDL_SELECT_TABLES,
                serviceDao.buildMap("TB_CATALOG", "TDB", "TB_SCHEMA", "PUBLIC", "TB_PREFIX", "BIS_MT"));

        for(Object obj : list) {
            serviceDao.delete(PlatformMapper.DDL_TRUNCATE_TABLES, serviceDao.buildMap("TB_NAME", (String)obj));
        }

        /*
        items = masterDao.selectList(PlatformMapper.DDL_SELECT_TABLES,
                masterDao.buildMap("TB_CATALOG", "TDB", "TB_SCHEMA", "PUBLIC", "TB_PREFIX", "BIS_VW"));
        */

        String[] viewes = {"BIS_VW_ROUTE_BUS", "BIS_VW_PRDT_TIME", "BIS_VW_LINK_TIME"};
        for(Object obj : viewes) {
            serviceDao.delete(PlatformMapper.DDL_DROP_VIEW, serviceDao.buildMap("TB_NAME", (String)obj));
        }
    }

    private void codeGroupDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_CODE_GROUP_ITEM;
        String filePath = getFileName(BisFileCode.CODE_GROUP, applyDateTime);

        CodeGroupFile fileObj = new CodeGroupFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(CodeGroupItem item1 : fileObj.getItems()) {
            paramList1.add(item1.toMap());
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void codeDetailDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_CODE_DETAIL_ITEM;
        String filePath = getFileName(BisFileCode.CODE_DETAIL, applyDateTime);

        CodeDetailFile fileObj = new CodeDetailFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(CodeDetailItem item1 : fileObj.getItems()) {
            paramList1.add(item1.toMap());
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void busDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_BUS_ITEM;
        String filePath = getFileName(BisFileCode.BUS, applyDateTime);

        BusFile fileObj = new BusFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(BusItem item1 : fileObj.getItems()) {
            paramList1.add(item1.toMap());
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void bitDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_BIT_ITEM;
        String filePath = getFileName(BisFileCode.BIT, applyDateTime);

        BitFile fileObj = new BitFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(BitItem item1 : fileObj.getItems()) {
            paramList1.add(item1.toMap());
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void routeDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_ROUTE_ITEM;
        String mapper2 = MasterMapper.LOADER_ROUTE_PLAN;
        String filePath = getFileName(BisFileCode.ROUTE, applyDateTime);

        RouteFile fileObj = new RouteFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();
        List<Map<String, Object>> paramList2 = new ArrayList<>();

        for(RouteItem1 item1 : fileObj.getList()) {
            paramList1.add(item1.toMap());

            for(RouteItem2 item2 : item1.getList()) {
                Map<String, Object> map2 = item2.toMap();
                map2.put("ROUTE_ID", item1.getRouteId());

                paramList2.add(map2);
            }
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
        serviceDao.insertBatch(mapper2, paramList2, batchSize);
    }

    private void stationDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_STATION_ITEM;
        String filePath = getFileName(BisFileCode.STATION, applyDateTime);

        StationFile fileObj = new StationFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(StationItem item1 : fileObj.getList()) {
            paramList1.add(item1.toMap());
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void linkDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_LINK_ITEM;
        String filePath = getFileName(BisFileCode.LINK, applyDateTime);

        LinkFile fileObj = new LinkFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(LinkItem item1 : fileObj.getList()) {
            paramList1.add(item1.toMap());
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void nodeDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_NODE_ITEM;
        String filePath = getFileName(BisFileCode.NODE, applyDateTime);

        NodeFile fileObj = new NodeFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(NodeItem item1 : fileObj.getList()) {
            paramList1.add(item1.toMap());
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void routeLinkDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_ROUTE_LINK_ITEM;
        String filePath = getFileName(BisFileCode.ROUTE_LINK, applyDateTime);

        RouteLinkFile fileObj = new RouteLinkFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(RouteLinkItem1 item1 : fileObj.getList()) {
            for(RouteLinkItem2 item2 : item1.getList()) {
                Map<String, Object> map1 = item2.toMap();
                map1.put("ROUTE_ID", item1.getRouteId());

                paramList1.add(map1);
            }
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void routeStationDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_ROUTE_STATION_ITEM;
        String filePath = getFileName(BisFileCode.ROUTE_STATION, applyDateTime);

        RouteStationFile fileObj = new RouteStationFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(RouteStationItem1 item1 : fileObj.getList()) {
            for(RouteStationItem2 item2 : item1.getList()) {
                Map<String, Object> map1 = item2.toMap();
                map1.put("ROUTE_ID", item1.getRouteId());

                paramList1.add(map1);
            }
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void routeNodeDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_ROUTE_NODE_ITEM;
        String filePath = getFileName(BisFileCode.ROUTE_NODE, applyDateTime);

        RouteNodeFile fileObj = new RouteNodeFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(RouteNodeItem1 item1 : fileObj.getList()) {
            for(RouteNodeItem2 item2 : item1.getList()) {
                Map<String, Object> map1 = item2.toMap();
                map1.put("ROUTE_ID", item1.getRouteId());

                paramList1.add(map1);
            }
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void linkVertexDb(String applyDateTime) {
        String mapper1 = MasterMapper.LOADER_LINK_VERTEX_ITEM;
        String filePath = getFileName(BisFileCode.LINK_VERTEX, applyDateTime);

        LinkVertexFile fileObj = new LinkVertexFile();
        fileToObject(filePath, fileObj);

        List<Map<String, Object>> paramList1 = new ArrayList<>();

        for(LinkVertexItem1 item1 : fileObj.getItems()) {
            for(LinkVertexItem2 item2 : item1.getItems()) {
                Map<String, Object> map1 = item2.toMap();
                map1.put("LINK_ID", item1.getLinkId());

                paramList1.add(map1);
            }
        }
        serviceDao.insertBatch(mapper1, paramList1, batchSize);
    }

    private void allInfoDb() {
        String mapper1 = MasterMapper.LOADER_ALL_INFO_ITEM;
        serviceDao.insert(mapper1, null);
    }

    private String getFileName(String dataType, String applyDate) {
        return String.format("%s/%s_%s_%s.%s", absoluteDir, prefix, dataType, applyDate, fileExt);
    }

    private void fileToObject(String filePath, BaseModel model) {
        byte[] byteData = readFile(filePath);
        if (byteData == null) {
            return;
        }
        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(byteData, byteData.length);

        model.decode(byteHelper);
    }
}
