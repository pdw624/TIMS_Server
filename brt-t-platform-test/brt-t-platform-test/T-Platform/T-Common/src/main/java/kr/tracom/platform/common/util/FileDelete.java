package kr.tracom.platform.common.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileDelete {

    public int deleteFiles(String path, String fileExt) {
        ExtensionFilter filter = new ExtensionFilter(fileExt);
        File dir = new File(path);

        String[] list = dir.list(filter);
        File file;

        if (list.length == 0) {
            return 0;
        }

        int affect = 0;
        for (int i = 0; i < list.length; i++) {
            file = new File(FileUtil.combine(path, list[i]));
            boolean isDeleted =  file.delete();

            if(isDeleted) affect++;
        }

        return affect;
    }

    class ExtensionFilter implements FilenameFilter {

        private String extension;

        public ExtensionFilter( String extension ) {
            this.extension = extension;
        }
        public boolean accept(File dir, String name) {
            return (name.endsWith(extension));
        }
    }
}
