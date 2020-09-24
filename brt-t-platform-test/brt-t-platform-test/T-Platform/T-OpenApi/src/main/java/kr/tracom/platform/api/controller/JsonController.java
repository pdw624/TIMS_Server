package kr.tracom.platform.api.controller;

import kr.tracom.platform.api.dao.ApiDao;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
public class JsonController {

    @GET
    @Path("session")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> platformSession() {

        ApiDao dao = new ApiDao();
        List<Object> list = dao.selectList("Platform.selectListSession", null);

        return list;
    }

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Test";
    }
}
