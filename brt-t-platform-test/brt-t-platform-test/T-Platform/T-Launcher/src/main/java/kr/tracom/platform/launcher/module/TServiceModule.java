package kr.tracom.platform.launcher.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.tracom.platform.attribute.manager.AttributeManager;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.common.module.ModuleInterface;
import kr.tracom.platform.launcher.manager.LicenseManager;
import kr.tracom.platform.launcher.manager.RuntimeManager;
import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.model.ServiceArgs;
import kr.tracom.platform.service.model.ServiceSetArgs;



public class TServiceModule implements ModuleInterface {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/* singleton */
	private static class SingletonHolder {
		public static final TServiceModule INSTANCE = new TServiceModule();
	}

	public static TServiceModule getInstance() {
		return SingletonHolder.INSTANCE;
	}
	/* singleton */

	public List<ServiceArgs> serviceList = Collections.synchronizedList(new ArrayList<ServiceArgs>());
	
	public int getSize() {
		return serviceList.size();
	}

	public Iterator<ServiceArgs> getIterator() {
		return serviceList.iterator();
	}

	public ServiceLauncher getLauncher(String serviceId) {
		Iterator<ServiceArgs> serviceIterator = serviceList.iterator();
		while(serviceIterator.hasNext()) {
			ServiceArgs serviceArgs = serviceIterator.next();

			if(serviceId.equals(serviceArgs.getId()) && "Y".equals(serviceArgs.getEnable())) {
				return serviceArgs.getServiceLauncher();
			}
		}
		return null;
	}


	@Override
	public void init(Object args) {
		serviceList.clear();
		serviceList.addAll((List<ServiceArgs>) args);
	}

	@Override
	public void startup() {
		Iterator<ServiceArgs> serviceIterator = serviceList.iterator();
		
		while(serviceIterator.hasNext()) {
			ServiceArgs serviceArgs = serviceIterator.next();
			
			if(serviceArgs.getServiceLauncher() != null) {
				if("Y".equals(serviceArgs.getEnable())) {
					serviceArgs.getServiceLauncher().startup();
				}
			}
		}
		String logMsg = "\r\n\r\n";
		logMsg += "===================================================================\r\n";
		logMsg += "Service module startup !!!\r\n";
		logMsg += "===================================================================\r\n";
		logger.info(logMsg);
	}

	@Override
	public void shutdown() {
		Iterator<ServiceArgs> serviceIterator = serviceList.iterator();
		while(serviceIterator.hasNext()) {
			ServiceArgs svc = serviceIterator.next();

			svc.getAttributeList().clear();
			if(svc.getServiceLauncher() != null) {
				svc.getServiceLauncher().shutdown();
			}
		}
		serviceList.clear();

		String logMsg = "\r\n\r\n";
		logMsg += "===================================================================\r\n";
		logMsg += "Service module shutdown !!!\r\n";
		logMsg += "===================================================================\r\n";
		logger.info(logMsg);
	}

	public void loadDynamic(ServiceSetArgs serviceSetArgs) {
		Iterator<ServiceArgs> serviceIterator = serviceList.iterator();
		while(serviceIterator.hasNext()) {
			ServiceArgs serviceArgs = serviceIterator.next();
			String jarPath = String.format("%s/%s-%s.jar", Constants.JAR_PATH, serviceArgs.getName(), serviceArgs.getVersion());
			ServiceLauncher serviceLauncher = RuntimeManager.loadService(jarPath, serviceArgs.getLauncher());

			if(serviceLauncher != null) {
				// 라이센스 체크
				if(!LicenseManager.licenseCheck(serviceArgs)) {
					serviceArgs.setEnable("N");

					logger.error("License expired [" + serviceArgs.toString() + "]");
					continue;
				}

				// 서비스 ID/버전 체크
				if(serviceArgs.getId().equals(serviceLauncher.ServiceId) &&
						serviceArgs.getVersion().equals(serviceLauncher.ServiceVersion)) {
					serviceLauncher.setServiceArgs(serviceSetArgs);

					List<Short> attrList = AttributeManager.bind(serviceArgs.getAttribute());
					attrList.addAll(AttributeManager.getCommonAttributes());
					Collections.sort(attrList);

					serviceArgs.setAttributeList(attrList);
					serviceArgs.setServiceLauncher(serviceLauncher);
				} else {
					logger.error("ID/Version mismatch [" + serviceArgs.toString() + "]");
				}
			} else {			
				logger.error("Service failed to loadDynamic [" + serviceArgs.toString() + "]");
			}
		}
	}

	public void loadDirect(ServiceSetArgs serviceSetArgs) {
		Iterator<ServiceArgs> serviceIterator = serviceList.iterator();
		while(serviceIterator.hasNext()) {
			ServiceArgs serviceArgs = serviceIterator.next();

			ServiceLauncher serviceLauncher = RuntimeManager.loadService(serviceArgs.getLauncher());
			if(serviceLauncher != null) {
				// 서비스 ID/버전 체크
				if(serviceArgs.getId().equals(serviceLauncher.ServiceId) &&
						serviceArgs.getVersion().equals(serviceLauncher.ServiceVersion)) {
					serviceLauncher.setServiceArgs(serviceSetArgs);

					if(serviceArgs.getAttribute() != null && !serviceArgs.getAttribute().isEmpty()) {
						List<Short> attrList = AttributeManager.bind(serviceArgs.getAttribute());
						//attrList.addAll(AttributeManager.getCommonAttributes());
						Collections.sort(attrList);
						serviceArgs.setAttributeList(attrList);
					} else {
						serviceArgs.setAttributeList(Collections.emptyList());
					}

					serviceArgs.setServiceLauncher(serviceLauncher);
				} else {
					logger.error("ID/Version mismatch [" + serviceArgs.toString() + "]");
				}
			} else {
				logger.error("Service failed to init dynamic [" + serviceArgs.toString() + "]");
			}
		}
	}
}
