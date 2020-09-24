package kr.tracom.platform.launcher.http.handler;

import kr.tracom.platform.common.util.FileUtil;
import kr.tracom.platform.common.util.StringUtil;
import kr.tracom.platform.launcher.http.builder.JsonBuilder;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.Session;

import java.io.*;

import static kr.tracom.platform.launcher.http.builder.JsonBuilder.FAIL_CD;
import static kr.tracom.platform.launcher.http.builder.JsonBuilder.SUCS_CD;

public class WebHandler extends HttpHandler {
    private final String SESSION_KEY = "MANAGER_ID";
    private final String CHAR_SET = "UTF-8";
    private final int SESSION_TIMEOUT = 10 * 60 * 1000;

    private String webRoot;
    private String context;

    public WebHandler(String rootPath, String context) {
        this.context = context;
        this.webRoot = rootPath + context + "/";
    }

    @Override
    public void service(Request request, Response response) throws Exception {
        byte[] contentData;
        String uri = getUri(request.getRequestURI());
        String contentType = MimeHandler.getMIMEType(uri);
        String filePath = webRoot + uri;

        /*
        String remoteIp = request.getRemoteHost();
        if("localhost".equalsIgnoreCase(remoteIp) || "127.0.0.1".equals(remoteIp)) {
            contentData = getFileByte(webRoot + "error.html");
            responseWrite(response, contentType, contentData);
            return;
        }
        */

        if(FileUtil.isExist(filePath)) {
            contentData = getFileByte(filePath);
        } else {
            contentData = getFileByte(webRoot + "error.html");
        }

        responseWrite(response, contentType, contentData);
    }

    private String getUri(String uri) {
        return uri.replaceAll(context + "/", "");
    }

    private byte[] getFileByte(String path) throws IOException {
        File file = new File(path);
        FileInputStream fs = new FileInputStream(path);
        byte[] fileByte = new byte[(int) file.length()];
        fs.read(fileByte, 0, fileByte.length);
        fs.close();

        //Path p = Paths.get(path);
        //byte[] fileByte = Files.readAllBytes(p);

        return fileByte;
    }

    private void responseWrite(Response response, String contentType, byte[] contentData) throws IOException {
        response.setContentType(contentType);
        response.setContentLength(contentData.length);

        OutputStream output = response.getOutputStream();
        output.write(contentData);
        output.flush();
    }

    private boolean isLogin(Request request) {
        Session session = request.getSession();
        if(session == null) return false;

        Object value = session.getAttribute(SESSION_KEY);
        if(value == null) return false;

        return true;
    }

    private byte[] login(Request request) throws UnsupportedEncodingException {
        String json;
        String userId = StringUtil.nvl(request.getParameter("userId"));
        String userPw = StringUtil.nvl(request.getParameter("userPw"));

        if("channel".equals(userId) && "channel".equals(userPw)) {
            Session session = request.getSession();
            session.setAttribute(SESSION_KEY, session.getIdInternal());
            session.setSessionTimeout(SESSION_TIMEOUT);

            json = JsonBuilder.getJson(SUCS_CD, JsonBuilder.SUCS_MESSAGE);
        } else {
            json = JsonBuilder.getJson(FAIL_CD, "Login Fail");
        }
        return json.getBytes(CHAR_SET);
    }

    private byte[] logout(Request request) throws UnsupportedEncodingException {
        String json;
        Session session = request.getSession();
        session.removeAttribute(SESSION_KEY);

        json = JsonBuilder.getJson(SUCS_CD, "Logout success");
        return json.getBytes(CHAR_SET);
    }
}
