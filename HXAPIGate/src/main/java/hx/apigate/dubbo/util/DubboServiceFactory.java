package hx.apigate.dubbo.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;

import hx.apigate.Entrance;
/**
 * 
 * 
 *
 */
public class DubboServiceFactory {

    private ApplicationConfig application;
    private RegistryConfig registry;
    private Map<String, Object> cache = new ConcurrentHashMap<>();
    
    private static class SingletonHolder {
        private static DubboServiceFactory INSTANCE = new DubboServiceFactory();
    }

    private DubboServiceFactory(){

        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(Entrance.nodeName); 
        applicationConfig.setQosEnable(false);
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(Entrance.zkAddr); 
        registryConfig.setRegister(false);
        
        this.application = applicationConfig;
        this.registry = registryConfig;
        

    }

    public static DubboServiceFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private String DubboService = "DubboService";
    
    
    public Object genericInvoke(String interfaceClass, String methodName, Object[] parameters){
    	System.out.println("当前调用得接口名和方法为："+interfaceClass+";"+methodName);
        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        reference.setInterface(interfaceClass); 
        reference.setGeneric("true"); 
        
        
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(application)
                .registry(registry)
                .reference(reference)
                .start();
        
        
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference); 
        if(genericService == null) {
        	cache.destroy(reference);
        	throw new IllegalStateException("服务不可用");
        }
        return genericService.$invoke(methodName, new String[] {"java.util.Map"}, parameters);
    }

}