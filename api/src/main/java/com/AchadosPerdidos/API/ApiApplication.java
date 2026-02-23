package com.AchadosPerdidos.API;

import com.AchadosPerdidos.API.Application.Config.EnvironmentConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(
		basePackages = "com.AchadosPerdidos.API.Domain.Repository",
		excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
				type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
				classes = com.AchadosPerdidos.API.Domain.Repository.ChatMessageRepository.class
		)
)
@EnableMongoRepositories(
		basePackages = "com.AchadosPerdidos.API.Domain.Repository",
		includeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
				type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
				classes = com.AchadosPerdidos.API.Domain.Repository.ChatMessageRepository.class
		)
)
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ApiApplication.class);
		app.addListeners(new EnvironmentConfig());
		app.run(args);
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

}