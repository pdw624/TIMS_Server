package kr.tracom.platform.launcher.http.handler;

import kr.tracom.platform.common.util.FileUtil;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StaticHandler extends HttpHandler {
    private String webRoot;

    public StaticHandler(String rootPath) {
        this.webRoot = rootPath + File.separator;
    }

    @Override
    public void service(Request request, Response response) throws Exception {
        byte[] contentData;
        String uri = request.getRequestURI();
        String contentType = MimeHandler.getMIMEType(uri);
        String filePath = FileUtil.combine(webRoot, uri);

        if(FileUtil.isExist(filePath)) {
            contentData = getFileByte(filePath);
        } else {
            contentData = getFileByte(webRoot + "error.html");
        }

        responseWrite(response, contentType, contentData);
    }

    @SuppressWarnings("resource")
    private byte[] getFileByte(String path) throws IOException {
        File file = new File(path);
        FileInputStream fs = new FileInputStream(file);
        byte[] fileByte = new byte[(int) file.length()];
        fs.read(fileByte, 0, fileByte.length);

        return fileByte;
    }

    private void responseWrite(Response response, String contentType, byte[] contentData) throws IOException {
        response.setContentType(contentType);
        response.setContentLength(contentData.length);

        OutputStream output = response.getOutputStream();
        output.write(contentData);
        output.flush();
    }
}
