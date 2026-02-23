package com.AchadosPerdidos.API;

import com.AchadosPerdidos.API.Application.Config.EnvironmentConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ApiApplication.class);
		app.addListeners(new EnvironmentConfig());
		app.run(args);
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

}