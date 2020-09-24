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
public class LinkFile implements BaseModel {
    public static final int SIZE = 14;

    private String applyDateTime;
    private short count;
    private List<LinkItem> list;

    public LinkFile() {
        list = new ArrayList<>();
    }

    public void addList(LinkItem item) {
        this.list.add(item);
        this.count = (short)this.list.size();
    }

    @Override
    public int getSize() {
        return SIZE + (LinkItem.SIZE * list.size());
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.applyDateTime, 12);
        byteHelper.setShort(this.count);
        for(LinkItem item : list) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.applyDateTime = byteHelper.getString(12);
        this.count = byteHelper.getShort();

        int loop = ByteHelper.unsigned(this.count);
        for(int i=0; i<loop; i++) {
            LinkItem item = new LinkItem();
            item.decode(byteHelper);

            this.list.add(item);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
