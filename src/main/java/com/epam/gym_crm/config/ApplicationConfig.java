package com.epam.gym_crm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.epam.gym_crm")
@PropertySource("classpath:application.properties")
public class ApplicationConfig {
}
