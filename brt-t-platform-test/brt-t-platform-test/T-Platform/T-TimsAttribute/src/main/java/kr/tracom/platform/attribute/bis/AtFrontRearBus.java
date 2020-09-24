package kr.tracom.platform.attribute.bis;

import kr.tracom.platform.attribute.BisAtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = BisAtCode.BUS_FRONT_REAR)
public class AtFrontRearBus extends AtData {
    public static final int SIZE = 11;

    private String impId;
    private byte count;
    private List<AtFrontRearBusItem> items;

    public AtFrontRearBus() {
        attrId = BisAtCode.BUS_FRONT_REAR;
        count = 0;
        items = new ArrayList<>();
    }

    public void addItem(AtFrontRearBusItem item) {
        items.add(item);
        count++;
    }

    @Override
    public int getSize() {
        return SIZE + (count * AtFrontRearBusItem.SIZE);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.impId = byteHelper.getString(10);
        this.count = byteHelper.getByte();

        short loop = ByteHelper.unsigned(this.count);
        for(short i=0; i<loop; i++) {
            AtFrontRearBusItem item = new AtFrontRearBusItem();
            item.decode(byteHelper);

            items.add(item);
        }
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.impId, 10);
        byteHelper.setByte(this.count);

        for(AtFrontRearBusItem item : items) {
            item.encode(byteHelper);
        }
    }

    @Override
    public String getLog() {
        return TimsLogBuilder.getLog(this);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("IMP_ID", this.impId);

        return map;
    }
}
