package kr.tracom.platform.net.protocol.factory;

import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsHeader;
import kr.tracom.platform.net.protocol.TimsHeaderTypeA;
import kr.tracom.platform.net.protocol.TimsHeaderTypeB;

public class TimsHeaderFactory {
	public static TimsHeader getHeader(TimsConfig timsConfig) {
		if ("A".equals(timsConfig.getHeaderType())) {
			return new TimsHeaderTypeA(timsConfig);
		} else if ("B".equals(timsConfig.getHeaderType()))  {
			return new TimsHeaderTypeB(timsConfig);
		} else if ("C".equals(timsConfig.getHeaderType()))  {
			return new TimsHeader(timsConfig);
		}
		return null;
	}

	public static byte getHeaderSize(TimsConfig timsConfig) {
		if ("A".equals(timsConfig.getHeaderType())) {
			return TimsHeaderTypeA.SIZE;
		} else if ("B".equals(timsConfig.getHeaderType()))  {
			return TimsHeaderTypeB.SIZE;
		} else if ("C".equals(timsConfig.getHeaderType()))  {
			return TimsHeader.SIZE;
		}
		return 0;
	}
}
