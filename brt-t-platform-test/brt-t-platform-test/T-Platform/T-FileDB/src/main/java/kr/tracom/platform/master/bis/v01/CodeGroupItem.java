package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CodeGroupItem implements BaseModel {
    public static final int SIZE = 80;

    private String groupCode;
    private String groupName1;
    private String groupName2;

    public CodeGroupItem() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.groupCode, 20);
        byteHelper.setString(this.groupName1, 30);
        byteHelper.setString(this.groupName2, 30);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.groupCode = byteHelper.getString(20);
        this.groupName1 = byteHelper.getString(30);
        this.groupName2 = byteHelper.getString(30);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("GROUP_CD", groupCode);
        map.put("GROUP_NAME1", groupName1);
        map.put("GROUP_NAME2", groupName2);

        return map;
    }
}
