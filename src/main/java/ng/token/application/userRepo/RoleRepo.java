package ng.token.application.userRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import ng.token.application.userModel.RoleUser;

public interface RoleRepo extends JpaRepository<RoleUser, Long> {
	RoleUser findByName(String name);

}
