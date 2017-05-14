package gunlee.me;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;

@SpringBootApplication
@EnableScheduling
public class GolfCrawlApplication {

	public static void main(String[] args) {
		System.setProperty("SERVER_PORT", "7979");
		SpringApplication.run(GolfCrawlApplication.class, args);
	}

	@Bean
	public ScheduledExecutorFactoryBean scheduledExecutorService() {
		ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
		bean.setPoolSize(5);

		return bean;
	}

}
