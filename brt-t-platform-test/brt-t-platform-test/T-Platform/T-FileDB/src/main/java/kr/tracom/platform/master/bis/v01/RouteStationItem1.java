package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class RouteStationItem1 implements BaseModel {
    public static final int SIZE = 12;

    private String routeId;
    private short count;
    private List<RouteStationItem2> list;

    public RouteStationItem1() {
        list = new ArrayList<>();
    }

    public void addList(RouteStationItem2 item) {
        this.list.add(item);
        this.count = (short) this.list.size();
    }

    @Override
    public int getSize() {
        return SIZE + (RouteStationItem2.SIZE * list.size());
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.routeId, 10);
        byteHelper.setShort(this.count);
        for(RouteStationItem2 item : list) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.routeId = byteHelper.getString(10);
        this.count = byteHelper.getShort();

        int loop = ByteHelper.unsigned(this.count);
        for(int i=0; i<loop; i++) {
            RouteStationItem2 item = new RouteStationItem2();
            item.decode(byteHelper);

            this.list.add(item);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
