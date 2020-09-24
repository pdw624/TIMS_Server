package kr.tracom.platform.attribute.common;

import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.net.protocol.TimsAttribute;
import kr.tracom.platform.net.protocol.TimsLogBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@TimsAttribute(attributeId = AtCode.SERVER_CONNECTION)
public class AtServerConnection extends AtData {
    public static final int SIZE = 1;

    private byte count;
    private List<AtServerConnectionItem> items;

    public AtServerConnection() {
        attrId = AtCode.SERVER_CONNECTION;
        items = new ArrayList<>();
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.count = byteHelper.getByte();

        short loop = ByteHelper.unsigned(this.count);
        for(short i=0; i<loop; i++) {
            AtServerConnectionItem item = new AtServerConnectionItem();
            item.decode(byteHelper);

            items.add(item);
        }
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setByte(this.count);

        for(AtServerConnectionItem item : items) {
            item.encode(byteHelper);
        }
    }

    @Override
    public String getLog() {
        return TimsLogBuilder.getLog(this);
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
