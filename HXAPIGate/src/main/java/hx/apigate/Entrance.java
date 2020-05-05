package hx.apigate;

import java.lang.reflect.Method;
import java.util.Set;


import org.reflections.Reflections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import hx.apigate.base.IProcessor;
import hx.apigate.util.LocalCache;

/**
 * 浩心API网关入口程序
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.xianglong.work</p>
 * @author yangcheng
 * @date
 */
@SpringBootApplication
public class Entrance {
	public static void main(String[] args) {
		SpringApplication.run(Entrance.class, args);
		
		Reflections reflections = new Reflections("hx.apigate");
		Set<Class<? extends IProcessor>> subTypes = reflections.getSubTypesOf(IProcessor.class);
		IProcessor[] array = new IProcessor[subTypes.size()];
		for (Class<? extends IProcessor> class1 : subTypes) {
			try {
				IProcessor processor = class1.newInstance();
				array[processor.getStartOrder()] = processor;
				
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 0 ; i < array.length ; i++) {
			try {
				array[i].start();
				LocalCache.eventProcessorCache.add(array[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
}
