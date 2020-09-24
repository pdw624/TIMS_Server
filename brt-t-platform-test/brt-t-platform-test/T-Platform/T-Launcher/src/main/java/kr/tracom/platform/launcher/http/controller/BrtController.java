package kr.tracom.platform.launcher.http.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.tracom.platform.brt.handler.LivingHandler;
import kr.tracom.platform.launcher.TLauncher;
import kr.tracom.platform.launcher.http.builder.JsonBuilder;
import kr.tracom.platform.net.config.TimsConfig;

@Path("/brt")
public class BrtController extends ChannelController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private TimsConfig timsConfig = TLauncher.getInstance().getTimsConfig();

    public String jsonp(@QueryParam("callback") String callback) {
        return JsonBuilder.getJsonP(callback, JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
    }

    @GET
    @Path("/sendNews")
    public String sendNews() {
        logger.info("sendNews 실행");
        
        try {
        	LivingHandler.getInstance().sendAllMessageCookieNews(timsConfig);
        } catch(Exception e) {
        	e.printStackTrace();	
        	return "fail";
        }

        return "success";
    }
    
    @GET
    @Path("/sendAirQuality")
    public String sendAirQuality() {
        logger.info("sendAirQuality 실행");
        
        try {
        	LivingHandler.getInstance().sendAllMessageAirQuality(timsConfig);
        } catch(Exception e) {
        	e.printStackTrace();	
        	return "fail";
        }

        return "success";
    }
    
    @GET
    @Path("/sendWeather")
    public String sendWeather() {
        logger.info("sendWeather 실행");
        
        try {
        	LivingHandler.getInstance().sendAllMessageWeather(timsConfig);
        } catch(Exception e) {
        	e.printStackTrace();	
        	return "fail";
        }

        return "success";
    }
}
