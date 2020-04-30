package com.code_generator_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class CodeGeneratorServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeGeneratorServerApplication.class, args);
	}

}
