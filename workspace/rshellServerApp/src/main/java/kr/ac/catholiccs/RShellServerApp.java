package kr.ac.catholiccs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RShellServerApp {
	public static final Logger logger = LoggerFactory.getLogger(RShellServerApp.class);
	
	public static void main(String[] args) {
		SpringApplication.run(RShellServerApp.class, args);
	}

}
