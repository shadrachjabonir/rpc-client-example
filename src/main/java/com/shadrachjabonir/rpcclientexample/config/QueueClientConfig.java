package com.shadrachjabonir.rpcclientexample.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Configuration
public class QueueClientConfig implements BeanDefinitionRegistryPostProcessor {
    private String serviceNameFormater(String className) {
        return className.toLowerCase().replace("service", "");
    }

    private Map<String, AmqpProxyFactoryBean> getListProxy() {
        System.out.println("uhuyyyyyyyyyyyy");
        Map<String, AmqpProxyFactoryBean> result = new HashMap<>();

//        String[] allBeanNames = context.getBeanDefinitionNames();
        AmqpProxyFactoryBean proxy;
        System.out.println(QueueServiceListConfig.class.getFields()[0]);
        for (Field f : QueueServiceListConfig.class.getFields()) {
            System.out.println(f.getType().getCanonicalName() + " cekkkkkkiiiidoootttt");
            System.out.println(result.size() + " ahayyyyyy");

            proxy = new AmqpProxyFactoryBean();
            System.out.println(result.size() + " ahayyyyyy");

            proxy.setServiceInterface(f.getType());
            System.out.println(result.size() + " ahayyyyyy");

//            proxy.setAmqpTemplate(new RabbitTemplate());
//            System.out.println(result.size() + " ahayyyyyy");

            result.put(serviceNameFormater(f.getName()), proxy);
            System.out.println(result.size() + " ahayyyyyy");
        }
        System.out.println(result.size()+ " <-----------------");
        return result;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        GenericBeanDefinition beanFactory;
        GenericBeanDefinition beanExchange;
        GenericBeanDefinition beanTemplate;
        GenericBeanDefinition beanQueue;
        GenericBeanDefinition beanAdmin;
        GenericBeanDefinition beanBinding;

        Map<String, AmqpProxyFactoryBean> map = getListProxy();
        System.out.println("haiiiiiiiiiii");

        CachingConnectionFactory connectionFactory;
        Queue queue;

        //connection factory
        connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("10.11.17.34");
        connectionFactory.setUsername("mp2_user");
        connectionFactory.setPassword("mp2_password");
        connectionFactory.setPort(5672);
        connectionFactory.setChannelCacheSize(25);

        //admin
        beanAdmin = new GenericBeanDefinition();
        beanAdmin.setBeanClass(RabbitAdmin.class);
        beanAdmin.getConstructorArgumentValues()
                .addGenericArgumentValue(new RabbitTemplate(connectionFactory));
        registry.registerBeanDefinition("amqpAdmin", beanAdmin);

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey()+ " " +pair.getValue() + " <---- cek");

            //queue
            queue = new Queue("remote." + pair.getKey().toString());
            beanQueue = new GenericBeanDefinition();
            beanQueue.setBeanClass(Queue.class);
            System.out.println("queue name nihhhh : "+queue.getName()) ;
            beanQueue.getConstructorArgumentValues()
                    .addGenericArgumentValue(queue.getName(), String.class.getName());
            registry.registerBeanDefinition(queue.getName(), beanQueue);

            //template
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setRoutingKey(queue.getName());
            rabbitTemplate.setExchange("");
            beanTemplate = new GenericBeanDefinition();
            beanTemplate.setBeanClass(RabbitTemplate.class);
//            beanTemplate.getConstructorArgumentValues()
//                    .addGenericArgumentValue(connectionFactory,ConnectionFactory.class.toString());
            beanTemplate.getPropertyValues()
                    .add("connectionFactory",rabbitTemplate.getConnectionFactory())
                    .add("routingKey", rabbitTemplate.getRoutingKey())
                    .add("exchange", rabbitTemplate.getExchange());
            registry.registerBeanDefinition(pair.getKey().toString() + "Template", beanTemplate);

            //exchange
            DirectExchange exchange = new DirectExchange("");
            beanExchange = new GenericBeanDefinition();
            beanExchange.setBeanClass(DirectExchange.class);
            beanExchange.getConstructorArgumentValues()
                    .addGenericArgumentValue("");
            registry.registerBeanDefinition( pair.getKey().toString()+ "Exchange", beanExchange);

            //binding
            Binding binding = BindingBuilder.bind(queue).to(exchange).withQueueName();
            beanBinding = new GenericBeanDefinition();
            beanBinding.setBeanClass(Binding.class);
            beanBinding.getConstructorArgumentValues()
                    .addIndexedArgumentValue(0,binding.getDestination());
            beanBinding.getConstructorArgumentValues()
                    .addIndexedArgumentValue(1,binding.getDestinationType());
            beanBinding.getConstructorArgumentValues()
                    .addIndexedArgumentValue(2,binding.getExchange());
            beanBinding.getConstructorArgumentValues()
                    .addIndexedArgumentValue(3,binding.getRoutingKey());
            beanBinding.getConstructorArgumentValues()
                    .addIndexedArgumentValue(4,binding.getArguments());
            registry.registerBeanDefinition( pair.getKey().toString()+ "Binding", beanBinding);

            //factory
            beanFactory = new GenericBeanDefinition();
            beanFactory.setBeanClass(AmqpProxyFactoryBean.class);
            AmqpProxyFactoryBean exporter = (AmqpProxyFactoryBean) pair.getValue();
            exporter.setAmqpTemplate(rabbitTemplate);
            System.out.println("haiiiiiiiiiiiiiiiaaaaaaa ---> " + exporter.getServiceInterface().toString());

            beanFactory.getPropertyValues()
                    .add("serviceInterface", exporter.getServiceInterface())
//                    .add("routingKey", rabbitTemplate.getRoutingKey())
                    .add("amqpTemplate", exporter.getAmqpTemplate());
            beanFactory.setLazyInit(true);
            registry.registerBeanDefinition(pair.getKey().toString() + "Factory", beanFactory);

            System.out.println(binding.toString() + "binnnnnnnnnnnnnn");
//            beanTemplate.getConstructorArgumentValues()
//                    .addGenericArgumentValue(connectionFactory, ConnectionFactory.class.toString());
            it.remove(); // avoids a ConcurrentModificationException
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

//        remote.testqueue
//                testqueueTemplate
//        testqueueExchange
//                testqueueBinding
//        testqueueFactory
    }
}
