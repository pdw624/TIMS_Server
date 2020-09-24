package kr.tracom.platform.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.service.model.VersionItem;
import kr.tracom.platform.service.model.VersionRoot;
import kr.tracom.platform.service.config.PlatformConfig;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

public class VersionManager {
    private static final String configFile = "version.json";

    public static VersionRoot load() {
        ObjectMapper mapper = new ObjectMapper();
        String saveFile = String.format("%s/%s/%s", AppConfig.getApplicationPath(), Constants.CONFIG_PATH, configFile);
        VersionRoot versionRoot = null;
        try {
            File file = new File(saveFile);
            if(file.exists()) {
                versionRoot = mapper.readValue(file, VersionRoot.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return versionRoot;
    }

    public static VersionItem getItem(String serviceId, String versionName) {
        VersionRoot versionRoot = load();
        if(versionRoot != null) {
            int index = versionRoot.getIndex(serviceId, versionName);
            if(index >= 0) {
                return versionRoot.get(index);
            }
        }
        return  null;
    }

    public static void save(VersionItem newItem) {
        VersionRoot versionRoot = load();
        if(versionRoot == null) {
            versionRoot = new VersionRoot();
            versionRoot.setUpdateDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));
            versionRoot.add(newItem);
        } else {
            versionRoot.setUpdateDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));
            int index = versionRoot.getIndex(newItem.getAppId(), newItem.getVersionName());
            if(index >= 0) {
                VersionItem oldItem = versionRoot.get(index);

                oldItem.update(newItem);
            } else {
                versionRoot.add(newItem);
            }
        }

        String saveFile = String.format("%s/%s/%s", AppConfig.getApplicationPath(), Constants.CONFIG_PATH, configFile);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(saveFile), versionRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
