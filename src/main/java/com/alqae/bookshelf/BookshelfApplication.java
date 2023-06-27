package com.alqae.bookshelf;

import com.alqae.bookshelf.models.*;
import com.alqae.bookshelf.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class BookshelfApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookshelfApplication.class, args);
	}

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;

	@Bean
	CommandLineRunner init(){
		return args -> {

			UserEntity userEntity = UserEntity.builder()
					.email("admin@mail.com")
					.firstName("Admin")
					.lastName("***")
					.password(passwordEncoder.encode("123456"))
					.roles(Set.of(RoleEntity.builder().name(ERole.ADMIN).build()))
					.status(EStatus.ACTIVE)
					.build();

			userRepository.save(userEntity);
		};
	}
}
