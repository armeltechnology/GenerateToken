package ng.token.application;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ng.token.application.userModel.RoleUser;
import ng.token.application.userModel.User;
import ng.token.application.userService.UserService;

@SpringBootApplication
public class ProjectGenerateTokenApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectGenerateTokenApplication.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	CommandLineRunner run(UserService userservice) {
		
		return args -> {
			userservice.saveRole(new RoleUser(null,"ROLE_USER"));
			userservice.saveRole(new RoleUser(null,"ROLE_ADMIN"));
			userservice.saveRole(new RoleUser(null,"ROLE_MANAGER"));
			
			userservice.saveUser(new User(null,"armel","armel","1223",new ArrayList<>()));
			userservice.saveUser(new User(null,"ariane","ariane","1234",new ArrayList<>()));
			userservice.saveUser(new User(null,"toto","toto","12345",new ArrayList<>()));
			
			userservice.addRoleToUser("toto", "ROLE_USER");
			userservice.addRoleToUser("armel", "ROLE_ADMIN");
			userservice.addRoleToUser("ariane", "ROLE_MANAGER");
		};
		
	}

}
