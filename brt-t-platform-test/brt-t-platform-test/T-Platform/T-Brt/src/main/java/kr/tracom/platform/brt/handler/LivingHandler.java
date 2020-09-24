package kr.tracom.platform.brt.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import kr.tracom.platform.attribute.brt.AtAirQuality;
import kr.tracom.platform.attribute.brt.AtCookieNews;
import kr.tracom.platform.attribute.brt.AtCookieNewsItem;
import kr.tracom.platform.attribute.brt.AtWeather;
import kr.tracom.platform.attribute.common.AtTimeStamp;
import kr.tracom.platform.brt.dao.BrtMapper;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.dao.ServiceDao;
import kr.tracom.platform.service.manager.ErrorManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;

public class LivingHandler {
	private static final LivingHandler instance = new LivingHandler();
	private final String platformId = AppConfig.get("platform.id");
	
	private ServiceDao serviceDao;
	
	private LivingHandler() {
		serviceDao = new ServiceDao();
	}
	
	public static LivingHandler getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public AtCookieNews sendCookieNews() throws Exception {
		List<Object> list = serviceDao.selectList(BrtMapper.SELECT_NEWS, null);
		List<AtCookieNewsItem> newsList = new ArrayList<AtCookieNewsItem>();
		
		for(Object map : list) {
			AtCookieNewsItem item = new AtCookieNewsItem();
			item.setParseMap((HashMap<String, Object>) map);
			newsList.add(item);
		}
		
		AtCookieNews news = new AtCookieNews();
		news.setNotiStDt(new AtTimeStamp(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT)));
		news.setCount((byte) newsList.size());
		news.setList(newsList);
		
		return news;
	}
	
	@SuppressWarnings("unchecked")
	public AtAirQuality sendAirQuality() throws Exception {
		AtAirQuality airQuality = new AtAirQuality();
		Map<String, Object> result = (HashMap<String, Object>) serviceDao.select(BrtMapper.SELECT_AIR_QUALITY, new HashMap<>());
		airQuality.setParseMap(result);
		
		return airQuality;
	}
	
	@SuppressWarnings("unchecked")
	public AtWeather sendWeather() throws Exception {
		AtWeather weather = new AtWeather();
		weather.setParseMap((HashMap<String, Object>) serviceDao.select(BrtMapper.SELECT_WEATHER, new HashMap<>()));
		
		return weather;
	}
	
	public void sendAllMessageAirQuality(TimsConfig timsConfig) throws Exception { 
		sendMessageAllImp(sendAirQuality(), timsConfig);
	}
	
	public void sendAllMessageWeather(TimsConfig timsConfig) throws Exception {
		sendMessageAllImp(sendWeather(), timsConfig);
	}
	
	public void sendAllMessageCookieNews(TimsConfig timsConfig) throws Exception {
		sendMessageAllImp(sendCookieNews(), timsConfig);
	}
	
	public void sendAllMessageImp(String sessionId, TimsConfig timsConfig) {
		try {
			sendMessageImp(sessionId, sendCookieNews(), timsConfig);
			sendMessageImp(sessionId, sendAirQuality(), timsConfig);
			sendMessageImp(sessionId, sendWeather(), timsConfig);
		} catch(Exception e) {
			ErrorManager.trace(platformId,
					this.getClass().getName(), Thread.currentThread().getStackTrace(),
					"PLATFORM PARAMETER", e.getMessage());
		}
	}
	
	public void sendMessageAllImp(AtData message, TimsConfig timsConfig) throws Exception {
		Iterator<TcpChannelSession> iterator = TcpSessionManager.getSession();
		
		while(iterator.hasNext()) {
			TcpChannelSession tcpChannelSession = iterator.next();

            if (tcpChannelSession != null) {
                TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
                TimsMessage timsMessage = builder.setRequest(message);
    			
    			write(tcpChannelSession, timsMessage);
            }
		}
	}
	
	public void sendMessageImp(String sessionId, AtData message, TimsConfig timsConfig) throws Exception {
		TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(sessionId);
		
        if (tcpChannelSession != null) {
        	TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
            TimsMessage timsMessage = builder.setRequest(message);
			
			write(tcpChannelSession, timsMessage);
        }
	}
	
	private void write(TcpChannelSession tcpChannelSession, TimsMessage timsMessage) throws Exception {
		if(tcpChannelSession != null) {
			TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
					tcpChannelSession.getChannel(),
					tcpChannelSession.getSession(),
					timsMessage);
			
			tcpChannelMessage.setResponse(false);

			TransactionManager.write(tcpChannelMessage);
		}
	}
}
