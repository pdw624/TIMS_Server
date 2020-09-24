package kr.tracom.platform.service.model;


import lombok.Data;

@Data
public class SyncObject {
    private int syncId;
    private Object syncData;

    public SyncObject() {
        this.syncId = 0;
        this.syncData = null;
    }
}
