package proyecto.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;  // 👈 IMPORTANTE

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@PostConstruct
	public void testMailEnv() {
		System.out.println("MAIL_USERNAME=" + System.getenv("MAIL_USERNAME"));
		System.out.println("MAIL_FROM=" + System.getenv("MAIL_FROM"));
	}
}
