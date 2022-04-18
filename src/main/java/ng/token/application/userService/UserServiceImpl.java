package ng.token.application.userService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.token.application.userModel.RoleUser;
import ng.token.application.userModel.User;
import ng.token.application.userRepo.RoleRepo;
import ng.token.application.userRepo.UserRepo;
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
	
	private final UserRepo userRepo ;
	private final RoleRepo rolesRepo;
	private final PasswordEncoder passwordEncoder;
		
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	User user=userRepo.findByUsername(username);
	
	if(user==null) {
		log.error("This User is not found in the Database");
		throw new UsernameNotFoundException("This User is not found in the Database");
	}else {
		log.info("This User {} is not found in the Database", username);
	}
	Collection<SimpleGrantedAuthority> authorities= new ArrayList<>();
	user.getRoles().forEach(role ->{
		authorities.add(new SimpleGrantedAuthority(role.getName()));
	});
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
	}
	

	@Override
	public User saveUser(User user) {
		log.info("Saving new User {} to the Database",user.getName());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public RoleUser saveRole(RoleUser role) {
		log.info("Saving new Role {}to the Database",role.getName());
		return rolesRepo.save(role);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		log.info("Add role {} to user {}",roleName,username);
		User user=userRepo.findByUsername(username);
		RoleUser role=rolesRepo.findByName(roleName);
		user.getRoles().add(role);
		
		
		
	}

	@Override
	public User getUser(String Uusername) {
		log.info("Get user {}",Uusername);
		return userRepo.findByUsername(Uusername);
	}

	@Override
	public List<User> getUsers() {
		log.info("get all users");
		return userRepo.findAll();
	}

	

}
