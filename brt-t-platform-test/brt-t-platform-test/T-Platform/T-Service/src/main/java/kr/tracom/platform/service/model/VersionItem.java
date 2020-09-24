package kr.tracom.platform.service.model;

import lombok.Data;

@Data
public class VersionItem {
    private String appId;
    private String versionName;
    private String versionNumber;
    private String filePath;
    private long fileSize;
    private String applyDateTime;
    private String updateDateTime;

    public void update(VersionItem item) {
        this.versionNumber = item.getVersionNumber();
        this.filePath = item.getFilePath();
        this.fileSize = item.getFileSize();
        this.applyDateTime = item.getApplyDateTime();
        this.updateDateTime = item.getUpdateDateTime();
    }
}
