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
public class LinkVertexItem1 implements BaseModel {
    public static final int SIZE = 12;

    private String linkId;
    private short count;
    private List<LinkVertexItem2> items;

    public LinkVertexItem1() {
        items = new ArrayList<>();
    }

    public void addList(LinkVertexItem2 item) {
        this.items.add(item);
        this.count = (short)this.items.size();
    }

    @Override
    public int getSize() {
        return SIZE + (LinkVertexItem2.SIZE * items.size());
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.linkId, 10);
        byteHelper.setShort(this.count);
        for(LinkVertexItem2 item : items) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.linkId = byteHelper.getString(10);
        this.count = byteHelper.getShort();

        int loop = ByteHelper.unsigned(this.count);
        for(int i=0; i<loop; i++) {
            LinkVertexItem2 item = new LinkVertexItem2();
            item.decode(byteHelper);

            this.items.add(item);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
