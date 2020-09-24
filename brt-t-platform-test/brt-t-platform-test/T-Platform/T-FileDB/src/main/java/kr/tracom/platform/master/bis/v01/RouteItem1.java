package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class RouteItem1 implements BaseModel {
    public static final int SIZE = 84;

    private String routeId;
    private String routeName1;
    private String routeName2;
    private String routeType;
    private String runType;
    private int routeLength;
    private short journeyTime;
    private short count;
    private List<RouteItem2> list;

    public RouteItem1() {
        list = new ArrayList<>();
    }

    public void addList(RouteItem2 item) {
        this.list.add(item);
        this.count = (short)this.list.size();
    }

    @Override
    public int getSize() {
        return SIZE + (RouteItem2.SIZE * list.size());
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.routeId, 10);
        byteHelper.setString(this.routeName1, 30);
        byteHelper.setString(this.routeName2, 30);
        byteHelper.setString(this.routeType, 3);
        byteHelper.setString(this.runType, 3);
        byteHelper.setInt(this.routeLength);
        byteHelper.setShort(this.journeyTime);
        byteHelper.setShort(this.count);

        for(RouteItem2 item : list) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.routeId = byteHelper.getString(10);
        this.routeName1 = byteHelper.getString(30);
        this.routeName2 = byteHelper.getString(30);
        this.routeType = byteHelper.getString(3);
        this.runType = byteHelper.getString(3);
        this.routeLength = byteHelper.getInt();
        this.journeyTime = byteHelper.getShort();
        this.count = byteHelper.getShort();

        int loop = ByteHelper.unsigned(this.count);
        for(int i=0; i<loop; i++) {
            RouteItem2 item = new RouteItem2();
            item.decode(byteHelper);

            this.list.add(item);
        }

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("ROUTE_ID", routeId);
        map.put("ROUTE_NAME1", routeName1);
        map.put("ROUTE_NAME2", routeName2);
        map.put("ROUTE_TYPE", routeType);
        map.put("RUN_TYPE", runType);
        map.put("ROUTE_LENGTH", routeLength);
        map.put("JOURNEY_TIME", journeyTime);

        return map;
    }
}
