package ng.token.application.userService;

import java.util.List;

import ng.token.application.userModel.RoleUser;
import ng.token.application.userModel.User;

public interface UserService {
	User saveUser(User user);
	RoleUser saveRole(RoleUser role);
	void addRoleToUser(String username, String roleName);
	User getUser(String Uusername);
	List<User> getUsers();

}
