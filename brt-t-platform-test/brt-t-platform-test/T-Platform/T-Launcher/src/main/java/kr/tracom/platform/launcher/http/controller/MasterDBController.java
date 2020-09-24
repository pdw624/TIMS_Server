package kr.tracom.platform.launcher.http.controller;

import kr.tracom.platform.bis.TBisLauncher;
import kr.tracom.platform.launcher.http.builder.JsonBuilder;
import kr.tracom.platform.launcher.module.TServiceModule;
import kr.tracom.platform.master.TFileDB;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/master")
public class MasterDBController {
    private final String SVC_ID = TFileDB.SVC_BIS_ID;

    @GET
    @Path("/buildDB")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String masterDB() {
        String result = TFileDB.getInstance().buildBisMaster(SVC_ID, "");

        if(!result.isEmpty()) {
            return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
        } else {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, "");
        }
    }

    @GET
    @Path("/loadDB")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String loadDB() {
        String result = TFileDB.getInstance().loadBisMaster(SVC_ID);

        TBisLauncher bisLauncher = (TBisLauncher) TServiceModule.getInstance().getLauncher(SVC_ID);
        bisLauncher.initBisDB();

        if(!result.isEmpty()) {
            return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
        } else {
            return JsonBuilder.getJson(JsonBuilder.FAIL_CD, "");
        }
    }
}
