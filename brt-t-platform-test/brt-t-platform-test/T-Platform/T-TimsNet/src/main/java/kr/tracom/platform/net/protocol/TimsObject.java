package kr.tracom.platform.net.protocol;

import kr.tracom.platform.net.util.ByteHelper;

public interface TimsObject {
    int getSize();
    void decode(ByteHelper byteHelper);
    void encode(ByteHelper byteHelper);
    String getLog();	
}
