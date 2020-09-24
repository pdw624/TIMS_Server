package kr.tracom.platform.service.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VersionRoot {
    private String updateDateTime;
    private List<VersionItem> items;

    public VersionRoot() {
        items = new ArrayList<>();
    }

    public void add(VersionItem item) {
        items.add(item);
    }

    public VersionItem get(int i) {
        return items.get(i);
    }

    public int getIndex(String appId, String versionName) {
        VersionItem item;
        for(int i=0; i<items.size(); i++) {
            item = items.get(i);

            if(appId.equalsIgnoreCase(item.getAppId()) && versionName.equalsIgnoreCase(item.getVersionName())) {
                return i;
            }
        }

        return -1;
    }
}
