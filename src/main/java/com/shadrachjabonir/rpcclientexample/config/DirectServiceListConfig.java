package com.shadrachjabonir.rpcclientexample.config;

import com.shadrachjabonir.rpcclientexample.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectServiceListConfig {

    public TestService testService;

}
