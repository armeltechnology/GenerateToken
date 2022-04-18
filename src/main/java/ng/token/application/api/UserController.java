package ng.token.application.api;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import  static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ng.token.application.userModel.RoleUser;
import ng.token.application.userModel.User;
import ng.token.application.userService.UserService;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/allUsers")
	public ResponseEntity<List<User>> getAllUsers(){
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	@PostMapping("/role/save")
	public ResponseEntity<RoleUser> saveRole(@RequestBody RoleUser role){
		URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/role/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveRole(role));
	}
	
	@PostMapping("/user/save") 
	public ResponseEntity<User> saveUser(@RequestBody User user){
		URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/user/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveUser(user));
	}
	
	@PostMapping("/role/addRole")
	public ResponseEntity<?> addRoleToUser(@RequestBody RoleTOuserForm form){
		userService.addRoleToUser(form.getUsername(),form.getRolename());
		
		return ResponseEntity.ok().build();
	}
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String authorizationHeader=request.getHeader(AUTHORIZATION);
		
		if (authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")) {
			
			try {
				String refresh_Token=authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier=JWT.require(algorithm).build();
				DecodedJWT decodeJwt= verifier.verify(refresh_Token);
				String username=decodeJwt.getSubject();
				User user=userService.getUser(username);
				String access_Token=JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("role",user.getRoles().stream().map(
								RoleUser::getName).collect(Collectors.toList()))
						.sign(algorithm);
				
				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_Token", access_Token);
				tokens.put("refresh_Token", refresh_Token);
				response.setContentType(APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
				
				
				
				
			} catch (Exception e) {
				
				response.setHeader("error", e.getMessage());
				response.setStatus(org.springframework.http.HttpStatus.FORBIDDEN.value());
				//response.sendError(org.springframework.http.HttpStatus.FORBIDDEN.value());
				Map<String, String> error = new HashMap<>();
				error.put("error_message", e.getMessage());
			
				response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
			
		}else {
			
			throw new RuntimeException("refresh token is missing ");
		}
		
	}
	
}

@Data
class RoleTOuserForm{
	private String username;
	private String rolename;
	
}
