package com.example.opcuaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OpcuaserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpcuaserverApplication.class, args);
	}

}
