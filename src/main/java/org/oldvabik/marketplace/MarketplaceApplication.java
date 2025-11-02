package org.oldvabik.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MarketplaceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MarketplaceApplication.class, args);
	}
}
