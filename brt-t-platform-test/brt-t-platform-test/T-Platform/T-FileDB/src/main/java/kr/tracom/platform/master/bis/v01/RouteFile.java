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
public class RouteFile implements BaseModel {
    public static final int SIZE = 14;

    private String applyDateTime;
    private short count;
    private List<RouteItem1> list;

    public RouteFile() {
        list = new ArrayList<>();
    }

    public void addList(RouteItem1 item) {
        this.list.add(item);
        this.count = (short)this.list.size();
    }

    @Override
    public int getSize() {
        int size = SIZE;
        for(RouteItem1 item : list) {
            size += item.getSize();
        }
        return size;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.applyDateTime, 12);
        byteHelper.setShort(this.count);
        for(RouteItem1 item : list) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.applyDateTime = byteHelper.getString(12);
        this.count = byteHelper.getShort();

        int loop = ByteHelper.unsigned(this.count);
        for(int i=0; i<loop; i++) {
            RouteItem1 item = new RouteItem1();
            item.decode(byteHelper);

            this.list.add(item);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
