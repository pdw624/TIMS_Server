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
@TimsAttribute(attributeId = BisAtCode.ROUTE_BUS_LOCATION)
public class AtRouteBusLocation extends AtData {
    public static final int SIZE = 21;

    private String bitId;
    private String routeId;
    private byte count;
    private List<AtRouteBusLocationItem> list;

    public AtRouteBusLocation() {
        attrId = BisAtCode.ROUTE_BUS_LOCATION;
        list = new ArrayList<>();
    }

    @Override
    public int getSize() {
        return SIZE + (count * AtRouteBusLocationItem.SIZE);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.bitId = byteHelper.getString(10);
        this.routeId = byteHelper.getString(10);
        this.count = byteHelper.getByte();

        short loop = ByteHelper.unsigned(this.count);
        for(short i=0; i<loop; i++) {
            AtRouteBusLocationItem item = new AtRouteBusLocationItem();
            item.decode(byteHelper);

            list.add(item);
        }
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.bitId, 10);
        byteHelper.setString(this.routeId, 10);
        byteHelper.setByte(this.count);

        for(AtRouteBusLocationItem item : list) {
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
        map.put("BIT_ID", this.bitId);
        map.put("ROUTE_ID", this.routeId);

        return map;
    }
}
