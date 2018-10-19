package com.shadrachjabonir.rpcclientexample.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Configuration
public class DirectClientConfig implements BeanDefinitionRegistryPostProcessor {

    private String serviceNameFormater(String className) {
        return className.toLowerCase().replace("service", "");
    }

    private Map<String, HttpInvokerProxyFactoryBean> getListInvoker() {
        Map<String, HttpInvokerProxyFactoryBean> result = new HashMap<>();
//        String[] allBeanNames = context.getBeanDefinitionNames();
        HttpInvokerProxyFactoryBean invoker;
        for (Field f : DirectServiceListConfig.class.getFields()) {
            System.out.println(f.getType().getCanonicalName() + " cekkkkkk");
            invoker = new HttpInvokerProxyFactoryBean();
            invoker.setServiceUrl("http://localhost:8080/" + serviceNameFormater(f.getName()));
            invoker.setServiceInterface(f.getType());
            result.put(serviceNameFormater(f.getName()) + "Invoker", invoker);
//            System.out.println(m1.getReturnType().getName());
//            System.out.println(m1.getName());

//            for (Class clazz : listOfClass) {
//                System.out.println(clazz.getName() + " iniiii");
//                for (Class clazzz : Arrays.asList(clazz.getInterfaces())) {
//                    System.out.println(clazzz.getName());
//                    System.out.println(m1.getReturnType().getName());
//                    if (clazzz.getName().equals(m1.getReturnType().getName())) {
//                        System.out.println("this is it : " + clazz.getClass().getCanonicalName() + " who implement : " + clazzz.getName());
//                        exporter = new HttpInvokerServiceExporter();
//                        try {
//                            exporter.setService(clazz.newInstance());
//                            exporter.setServiceInterface(clazzz);
//                        } catch (InstantiationException e) {
//                            e.printStackTrace();
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                        result.put("/" + serviceNameFormater(clazzz.getSimpleName()), exporter);
//                    }
//                }
//            }
        }
        return result;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        GenericBeanDefinition bd;
        Map<String, HttpInvokerProxyFactoryBean> map = getListInvoker();

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            bd = new GenericBeanDefinition();
            bd.setBeanClass(HttpInvokerProxyFactoryBean.class);
            HttpInvokerProxyFactoryBean exporter = (HttpInvokerProxyFactoryBean) pair.getValue();
            bd.getPropertyValues()
                    .add("serviceUrl", exporter.getServiceUrl())
                    .add("serviceInterface", exporter.getServiceInterface());
            registry.registerBeanDefinition(pair.getKey().toString(), bd);
            it.remove(); // avoids a ConcurrentModificationException
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
