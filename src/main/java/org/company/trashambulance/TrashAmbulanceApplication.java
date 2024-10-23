package org.company.trashambulance;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class TrashAmbulanceApplication {

	private static final Logger logger = LoggerFactory.getLogger(TrashAmbulanceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TrashAmbulanceApplication.class, args);
	}

	@PostConstruct
	public void init() {
		LocalDateTime dateTime = LocalDateTime.now();
		logger.info("[LOCAL] Bot started in {}", dateTime.toString());
	}
}
