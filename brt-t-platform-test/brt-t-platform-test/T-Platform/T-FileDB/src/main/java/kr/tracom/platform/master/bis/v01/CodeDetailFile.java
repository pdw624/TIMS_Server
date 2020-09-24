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
public class CodeDetailFile implements BaseModel {
    public static final int SIZE = 14;

    private String applyDateTime;
    private short count;
    private List<CodeDetailItem> items;

    public CodeDetailFile() {
        items = new ArrayList<>();
    }

    public void addItems(CodeDetailItem item) {
        this.items.add(item);
        this.count = (short)this.items.size();
    }

    @Override
    public int getSize() {
        int size = SIZE;
        for(CodeDetailItem item : items) {
            size += item.getSize();
        }
        return size;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.allocate(getSize());

        byteHelper.setString(this.applyDateTime, 12);
        byteHelper.setShort(this.count);
        for(CodeDetailItem item : items) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.applyDateTime = byteHelper.getString(12);
        this.count = byteHelper.getShort();

        int loop = ByteHelper.unsigned(this.count);
        for(int i=0; i<loop; i++) {
            CodeDetailItem item = new CodeDetailItem();
            item.decode(byteHelper);

            this.items.add(item);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
