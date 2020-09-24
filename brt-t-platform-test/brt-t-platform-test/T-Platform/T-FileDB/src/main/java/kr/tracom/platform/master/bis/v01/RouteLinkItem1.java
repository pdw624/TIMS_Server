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
public class RouteLinkItem1 implements BaseModel {
    public static final int SIZE = 12;

    private String routeId;
    private short count;
    private List<RouteLinkItem2> list;

    public RouteLinkItem1() {
        list = new ArrayList<>();
    }

    public void addList(RouteLinkItem2 item) {
        this.list.add(item);
        this.count = (short)this.list.size();
    }

    @Override
    public int getSize() {
        return SIZE + (RouteLinkItem2.SIZE * list.size());
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.routeId, 10);
        byteHelper.setShort(this.count);

        for(RouteLinkItem2 item : list) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.routeId = byteHelper.getString(10);
        this.count = byteHelper.getShort();

        int loop = ByteHelper.unsigned(this.count);
        for(int i=0; i<loop; i++) {
            RouteLinkItem2 item = new RouteLinkItem2();
            item.decode(byteHelper);

            this.list.add(item);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
