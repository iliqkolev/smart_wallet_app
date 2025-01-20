package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@EntityScan(basePackages = "app")
public class SmartWalletAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartWalletAppApplication.class, args);
	}

}
