package kr.tracom.platform.master.bis.bit;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class MonitorFile implements BaseModel {
    public static final int SIZE = 13;

    private String applyDateTime;
    private byte count;
    private List<MonitorItem> items;

    public MonitorFile() {
        this.count = 0;
        items = new ArrayList<MonitorItem>();
    }

    public void addItem(MonitorItem item) {
        this.items.add(item);
        //this.count++;
    }

    @Override
    public int getSize() {
        return SIZE + (MonitorItem.SIZE * items.size());
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.applyDateTime, 12);
        byteHelper.setByte(this.count);

        for(MonitorItem item : items) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.applyDateTime = byteHelper.getString(12);
        this.count = byteHelper.getByte();

        short loop = ByteHelper.unsigned(this.count);
        for(short i=0; i<loop; i++) {
            MonitorItem item = new MonitorItem();
            item.decode(byteHelper);

            this.items.add(item);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
