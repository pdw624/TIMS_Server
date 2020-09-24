package kr.tracom.platform.master.bis;

import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.common.util.FileUtil;
import kr.tracom.platform.common.util.ZipUtil;
import kr.tracom.platform.db.util.DbHelper;
import kr.tracom.platform.master.base.BaseFile;
import kr.tracom.platform.master.bis.v01.*;
import kr.tracom.platform.master.dao.MasterMapper;
import kr.tracom.platform.net.util.ByteHelper;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.ServiceDao;
import kr.tracom.platform.service.domain.MtServer;
import kr.tracom.platform.service.manager.CodeManager;
import kr.tracom.platform.service.manager.FtpManager;
import kr.tracom.platform.service.manager.VersionManager;
import kr.tracom.platform.service.model.VersionItem;
import kr.tracom.platform.tcp.helper.FtpHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BisBuilder extends BaseFile {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BisBuilder(String serviceId, String version, String applyDateTime, SqlSessionFactory sqlSessionFactory) {
        super.serviceId = serviceId;
        super.serviceDao = new ServiceDao(sqlSessionFactory);
        super.versionNumber = version;
        super.prefix = "CMN";
        super.dbType = "maria";
        super.applyDateTime = applyDateTime;

        workingDir = String.format("%s/%s/%s", Constants.MASTER_PATH, serviceId, DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));
        absoluteDir = AppConfig.getApplicationPath() + workingDir;

        try {
            DbHelper.addMapper(sqlSessionFactory,
                    AppConfig.getClasspath(String.format("%s/bis/master-%s-%s.xml", Constants.DB_PATH, dbType, version)));
        } catch(Exception e) {

        }

        File f = new File(absoluteDir);
        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }

    @Override
    public String execute(Object args) {
        String param = (String) args;
        try {
            String[] items = param.split(",");

            for(String item : items) {
                if(item.equals(BisFileCode.CODE_GROUP)) {
                    buildCodeGroup(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.CODE_GROUP);
                }
                else if(item.equals(BisFileCode.CODE_DETAIL)) {
                    buildCodeDetail(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.CODE_DETAIL);
                }
                else if(item.equals(BisFileCode.BUS)) {
                    buildBus(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.BUS);
                }
                else if(item.equals(BisFileCode.BIT)) {
                    buildBit(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.BIT);
                }
                else if(item.equals(BisFileCode.ROUTE)) {
                    buildRoute(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.ROUTE);
                }
                else if(item.equals(BisFileCode.STATION)) {
                    buildStation(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.STATION);
                }
                else if(item.equals(BisFileCode.LINK)) {
                    buildLink(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.LINK);
                }
                else if(item.equals(BisFileCode.NODE)) {
                    buildNode(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.NODE);
                }
                else if(item.equals(BisFileCode.ROUTE_LINK)) {
                    buildRouteLink(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.ROUTE_LINK);
                }
                else if(item.equals(BisFileCode.ROUTE_STATION)) {
                    buildRouteStation(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.ROUTE_STATION);
                }
                else if(item.equals(BisFileCode.ROUTE_NODE)) {
                    buildRouteNode(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.ROUTE_NODE);
                }
                else if(item.equals(BisFileCode.LINK_VERTEX)) {
                    buildLinkVertex(applyDateTime);

                    logger.info("buildMaster " + BisFileCode.LINK_VERTEX);
                }
            }

            String localPath = absoluteDir + String.format("/CMN_AL_%s.ZIP", applyDateTime);
            String remotePath = String.format("/CMN/MASTER/CMN_AL_%s.ZIP", applyDateTime);

            ZipUtil.compressDirectory(absoluteDir, fileExt, localPath);
            //CompressUtil.compressDirectory(absoluteDir, fileExt, localPath);

            logger.info("compress zip " + localPath);

            MtServer ftpCenter = FtpManager.getFtpConfig(CodeManager.RoutingGroupId.CTR.getValue());

            FtpHelper ftpHelper = new FtpHelper();
            if(ftpHelper.open(ftpCenter.getServerIp(), ftpCenter.getServerPort())) {
                if(ftpHelper.login(ftpCenter.getUserId(), ftpCenter.getUserPw())) {
                    ftpHelper.setActiveMode(false);
                    ftpHelper.upload(localPath, remotePath);
                }
            }
            ftpHelper.close();

            logger.info("ftp upload " + remotePath);

            //Thread.sleep(1000);

            FileUtil.deleteByExtension(absoluteDir, fileExt);

            logger.info("file delete " + absoluteDir);

            String relativeDir = workingDir + String.format("/CMN_AL_%s.ZIP", applyDateTime);
            long fileSize = FileUtil.getSize(localPath);

            updateVersion(relativeDir, fileSize, applyDateTime);

            logger.info("update version " + applyDateTime);

            return remotePath;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return "";
        }
    }

    private void updateVersion(String relativeDir, long fileSize, String applyDateTime) {
        VersionItem item = new VersionItem();
        item.setAppId(serviceId);
        item.setVersionName("MASTER");
        item.setVersionNumber(versionNumber);
        item.setFilePath(relativeDir);
        item.setFileSize(fileSize);
        item.setApplyDateTime(applyDateTime);
        item.setUpdateDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

        VersionManager.save(item);
    }

    private void buildCodeGroup(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_CODE_GROUP_ITEM;

        CodeGroupFile codeGroupFile = new CodeGroupFile();
        CodeGroupItem groupItem;
        List<Object> list = serviceDao.selectList(mapperId, null);

        codeGroupFile.setApplyDateTime(nowDateTime);
        for(Object item1Obj : list) {
            groupItem = (CodeGroupItem) item1Obj;

            codeGroupFile.addItems(groupItem);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(codeGroupFile.getSize());

        codeGroupFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.CODE_GROUP, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());

            //String localPath = absoluteDir + String.format("/%s_%s_%s.zip", prefix, BisFileCode.CODE_GROUP, applyDateTime);
            //ZipUtil.compressFile(fileName, localPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildCodeDetail(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_CODE_DETAIL_ITEM;

        CodeDetailFile codeDetailFile = new CodeDetailFile();
        CodeDetailItem groupItem;
        List<Object> list = serviceDao.selectList(mapperId, null);

        codeDetailFile.setApplyDateTime(nowDateTime);
        for(Object item1Obj : list) {
            groupItem = (CodeDetailItem) item1Obj;

            codeDetailFile.addItems(groupItem);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(codeDetailFile.getSize());

        codeDetailFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.CODE_DETAIL, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildBus(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_BUS_ITEM;

        BusFile busFile = new BusFile();
        BusItem busItem;
        List<Object> busItems = serviceDao.selectList(mapperId, null);

        busFile.setApplyDateTime(nowDateTime);
        for(Object itemObj : busItems) {
            busItem = (BusItem) itemObj;

            busFile.addList(busItem);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(busFile.getSize());

        busFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.BUS, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildBit(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_BIT_ITEM;

        BitFile bitFile = new BitFile();
        BitItem bitItem;
        List<Object> bitItems = serviceDao.selectList(mapperId, null);

        bitFile.setApplyDateTime(nowDateTime);
        for(Object itemObj : bitItems) {
            bitItem = (BitItem) itemObj;

            bitFile.addList(bitItem);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(bitFile.getSize());

        bitFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.BIT, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildRoute(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_ROUTE_ITEM;
        String mapperId2 = MasterMapper.BUILDER_ROUTE_PLAN;

        RouteFile routeFile = new RouteFile();
        RouteItem1 routeItem;
        RouteItem2 routePlan;
        List<Object> listRouteItem = serviceDao.selectList(mapperId, null);

        routeFile.setApplyDateTime(nowDateTime);
        for(Object itemObj : listRouteItem) {
            routeItem = (RouteItem1) itemObj;

            List<Object> listRoutePlan = serviceDao.selectList(mapperId2,
                    serviceDao.buildMap("ROUTE_ID", routeItem.getRouteId()));

            for(Object planObj : listRoutePlan) {
                routePlan = (RouteItem2) planObj;

                routeItem.addList(routePlan);
            }

            routeFile.addList(routeItem);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(routeFile.getSize());

        routeFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.ROUTE, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildStation(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_STATION_ITEM;

        StationFile stationFile = new StationFile();
        StationItem stationItem;
        List<Object> listStationItem = serviceDao.selectList(mapperId, null);

        stationFile.setApplyDateTime(nowDateTime);
        for(Object itemObj : listStationItem) {
            stationItem = (StationItem) itemObj;

            stationFile.addList(stationItem);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(stationFile.getSize());

        stationFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.STATION, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildLink(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_LINK_ITEM;

        LinkFile linkFile = new LinkFile();
        LinkItem linkItem;
        List<Object> linkList = serviceDao.selectList(mapperId, null);

        linkFile.setApplyDateTime(nowDateTime);
        for(Object itemObj : linkList) {
            linkItem = (LinkItem) itemObj;

            linkFile.addList(linkItem);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(linkFile.getSize());

        linkFile.encode(byteHelper);
        String fileName = getFileName(absoluteDir, prefix, BisFileCode.LINK, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildNode(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_NODE_ITEM;

        NodeFile nodeFile = new NodeFile();
        NodeItem nodeItem;
        List<Object> nodeList = serviceDao.selectList(mapperId, null);

        nodeFile.setApplyDateTime(nowDateTime);
        for(Object itemObj : nodeList) {
            nodeItem = (NodeItem) itemObj;

            nodeFile.addList(nodeItem);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(nodeFile.getSize());

        nodeFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.NODE, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildRouteLink(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_ROUTE_LINK_ITEM1;
        String mapperId2 = MasterMapper.BUILDER_ROUTE_LINK_ITEM2;

        RouteLinkFile routeLinkFile = new RouteLinkFile();
        RouteLinkItem1 routeLinkItem1;
        RouteLinkItem2 routeLinkItem2;
        List<Object> routeList = serviceDao.selectList(mapperId, null);

        routeLinkFile.setApplyDateTime(nowDateTime);
        for(Object item1Obj : routeList) {
            routeLinkItem1 = (RouteLinkItem1) item1Obj;

            List<Object> linkList = serviceDao.selectList(mapperId2,
                    serviceDao.buildMap("ROUTE_ID", routeLinkItem1.getRouteId()));

            for(Object item2Obj : linkList) {
                routeLinkItem2 = (RouteLinkItem2) item2Obj;

                routeLinkItem1.addList(routeLinkItem2);
            }

            routeLinkFile.addList(routeLinkItem1);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(routeLinkFile.getSize());
        routeLinkFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.ROUTE_LINK, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildRouteStation(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_ROUTE_STATION_ITEM1;
        String mapperId2 = MasterMapper.BUILDER_ROUTE_STATION_ITEM2;

        RouteStationFile routeStationFile = new RouteStationFile();
        RouteStationItem1 routeStationItem1;
        RouteStationItem2 routeStationItem2;
        List<Object> routeList = serviceDao.selectList(mapperId, null);

        routeStationFile.setApplyDateTime(nowDateTime);
        for(Object item1Obj : routeList) {
            routeStationItem1 = (RouteStationItem1) item1Obj;

            List<Object> linkList = serviceDao.selectList(mapperId2,
                    serviceDao.buildMap("ROUTE_ID", routeStationItem1.getRouteId()));

            for(Object item2Obj : linkList) {
                routeStationItem2 = (RouteStationItem2) item2Obj;

                routeStationItem1.addList(routeStationItem2);
            }

            routeStationFile.addList(routeStationItem1);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(routeStationFile.getSize());
        routeStationFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.ROUTE_STATION, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildRouteNode(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_ROUTE_NODE_ITEM1;
        String mapperId2 = MasterMapper.BUILDER_ROUTE_NODE_ITEM2;

        RouteNodeFile routeNodeFile = new RouteNodeFile();
        RouteNodeItem1 routeNodeItem1;
        RouteNodeItem2 routeNodeItem2;
        List<Object> routeList = serviceDao.selectList(mapperId, null);

        routeNodeFile.setApplyDateTime(nowDateTime);
        for(Object item1Obj : routeList) {
            routeNodeItem1 = (RouteNodeItem1) item1Obj;

            List<Object> linkList = serviceDao.selectList(mapperId2,
                    serviceDao.buildMap("ROUTE_ID", routeNodeItem1.getRouteId()));

            for(Object item2Obj : linkList) {
                routeNodeItem2 = (RouteNodeItem2) item2Obj;

                routeNodeItem1.addList(routeNodeItem2);
            }

            routeNodeFile.addList(routeNodeItem1);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(routeNodeFile.getSize());
        routeNodeFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.ROUTE_NODE, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildLinkVertex(String nowDateTime) {
        String mapperId = MasterMapper.BUILDER_LINK_VERTEX_ITEM1;
        String mapperId2 = MasterMapper.BUILDER_LINK_VERTEX_ITEM2;

        LinkVertexFile linkVertexFile = new LinkVertexFile();
        LinkVertexItem1 linkVertexItem1;
        LinkVertexItem2 linkVertexItem2;
        List<Object> routeList = serviceDao.selectList(mapperId, null);

        linkVertexFile.setApplyDateTime(nowDateTime);
        for(Object item1Obj : routeList) {
            linkVertexItem1 = (LinkVertexItem1) item1Obj;

            List<Object> linkList = serviceDao.selectList(mapperId2,
                    serviceDao.buildMap("LINK_ID", linkVertexItem1.getLinkId()));

            for(Object item2Obj : linkList) {
                linkVertexItem2 = (LinkVertexItem2) item2Obj;

                linkVertexItem1.addList(linkVertexItem2);
            }

            linkVertexFile.addList(linkVertexItem1);
        }

        ByteHelper byteHelper = new ByteHelper(byteOrder, stringEncoding);
        byteHelper.allocate(linkVertexFile.getSize());
        linkVertexFile.encode(byteHelper);

        String fileName = getFileName(absoluteDir, prefix, BisFileCode.LINK_VERTEX, nowDateTime);
        try {
            writeFile(fileName, byteHelper.getBuffer(), byteHelper.getIndex());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
