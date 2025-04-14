package com.example.springBootLearn;

import com.example.common.CommonModuleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CommonModuleConfiguration.class)
public class SpringBootLearnApplication {

	public static void main(String[] args) {
		// Enable DevTools restart
		System.setProperty("spring.devtools.restart.enabled", "true");
		System.setProperty("spring.devtools.livereload.enabled", "true");
		
		SpringApplication.run(SpringBootLearnApplication.class, args);
	}

}
