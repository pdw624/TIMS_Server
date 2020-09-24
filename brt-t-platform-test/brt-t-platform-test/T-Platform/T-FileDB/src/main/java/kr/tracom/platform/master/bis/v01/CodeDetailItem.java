package kr.tracom.platform.master.bis.v01;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CodeDetailItem implements BaseModel {
    public static final int SIZE = 83;

    private String groupCode;
    private String detailCode;
    private String detailName1;
    private String detailName2;

    public CodeDetailItem() {

    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setString(this.groupCode, 20);
        byteHelper.setString(this.detailCode, 3);
        byteHelper.setString(this.detailName1, 30);
        byteHelper.setString(this.detailName2, 30);
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.groupCode = byteHelper.getString(20);
        this.detailCode = byteHelper.getString(3);
        this.detailName1 = byteHelper.getString(30);
        this.detailName2 = byteHelper.getString(30);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("GROUP_CD", groupCode);
        map.put("DETAIL_CD", detailCode);
        map.put("DETAIL_NAME1", detailName1);
        map.put("DETAIL_NAME2", detailName2);

        return map;
    }
}
