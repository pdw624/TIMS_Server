package kr.tracom.platform.net.protocol.attribute;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AtStyle extends ToStringStyle  {
    private static final long serialVersionUID = 1L;

    public AtStyle() {
        super();
        this.setUseClassName(false);
        this.setUseIdentityHashCode(false);
        this.setContentStart("{" + SystemUtils.LINE_SEPARATOR + "\t");
        this.setFieldNameValueSeparator(SystemUtils.PATH_SEPARATOR);
        this.setNullText("");
        this.setFieldSeparator("," + SystemUtils.LINE_SEPARATOR + "\t");
        this.setContentEnd(SystemUtils.LINE_SEPARATOR + "}");
    }
}
