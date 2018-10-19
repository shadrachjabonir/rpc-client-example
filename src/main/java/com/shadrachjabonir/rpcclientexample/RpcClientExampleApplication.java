package com.shadrachjabonir.rpcclientexample;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

@SpringBootApplication
@Configuration
@EnableRabbit
public class RpcClientExampleApplication {
//	@Bean
//	public HttpInvokerProxyFactoryBean invoker() {
//		HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
//		invoker.setServiceUrl("http://localhost:8080/test");
//		invoker.setServiceInterface(TestService.class);
//		return invoker;
//	}

	public static void main(String[] args) {
		ApplicationContext ac = SpringApplication.run(RpcClientExampleApplication.class, args);
		String[] allBeanNames = ac.getBeanDefinitionNames();

		for(String beanName : allBeanNames) {
			System.out.println(beanName);
		}
//		TestService service = ac.getBean(TestService.class);
//		System.out.println(service.makeTest("Shadrach",29, true));
//		System.out.println("=================");
//		System.out.println((AmqpProxyFactoryBean)(ac.getBean("testqueueFactory")));
		TestQueueService service2 = ac.getBean(TestQueueService.class);
		System.out.println(service2.doQueue("Shadrach",29, true));
		System.out.println("=================");


	}
}
