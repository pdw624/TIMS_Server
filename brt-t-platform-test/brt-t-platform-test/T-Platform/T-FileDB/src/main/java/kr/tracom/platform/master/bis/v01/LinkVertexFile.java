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
public class LinkVertexFile implements BaseModel {
    public static final int SIZE = 14;

    private String applyDateTime;
    private short count;
    private List<LinkVertexItem1> items;

    public LinkVertexFile() {
        items = new ArrayList<>();
    }

    public void addList(LinkVertexItem1 item) {
        this.items.add(item);
        this.count = (short)this.items.size();
    }

    @Override
    public int getSize() {
        int size = SIZE;
        for(LinkVertexItem1 item : items) {
            size += item.getSize();
        }
        return size;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.applyDateTime, 12);
        byteHelper.setShort(this.count);
        for(LinkVertexItem1 item : items) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.applyDateTime = byteHelper.getString(12);
        this.count = byteHelper.getShort();

        int loop = ByteHelper.unsigned(this.count);
        for(int i=0; i<loop; i++) {
            LinkVertexItem1 item = new LinkVertexItem1();
            item.decode(byteHelper);

            this.items.add(item);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
