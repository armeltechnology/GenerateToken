package ng.token.application.userRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import ng.token.application.userModel.User;

public interface UserRepo extends JpaRepository<User, Long> {
	User findByUsername(String username);

}
