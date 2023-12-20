package io.security.corespringsecurity;

import io.security.corespringsecurity.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({JwtProperties.class})
@SpringBootApplication
public class CoreSpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreSpringSecurityApplication.class, args);
	}

}
