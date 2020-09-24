package kr.tracom.platform.master.base;

import kr.tracom.platform.net.util.ByteHelper;

import java.util.Map;

public interface BaseModel {
    int getSize();
    void encode(ByteHelper byteHelper);
    void decode(ByteHelper byteHelper);
    Map<String, Object> toMap();
}
