package com.spring.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.models.ERole;
import com.spring.models.Role;
import com.spring.models.User;
import com.spring.payload.repository.RoleRepository;
import com.spring.payload.repository.UserRepository;
import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SigupRequest;
import com.spring.payload.response.MessageResponse;
import com.spring.payload.response.UserInforResponse;
import com.spring.security.jwt.JwtUtils;
import com.spring.security.services.UserDetailsImpl;



@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		org.springframework.security.core.Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
				.body( new UserInforResponse(userDetails.getId(),userDetails.getUsername(),userDetails.getEmail(),roles));
				
	}
	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser(){
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(new MessageResponse("Ban da dang xuat"));
	}
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SigupRequest sigupRequest){
		if(userRepository.existsUserByUsername(sigupRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: username nay da duoc su dung"));
		}
		if(userRepository.existsByEmail(sigupRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Email nay da ton tai"));
		}
		User user = new User(sigupRequest.getUsername(),sigupRequest.getEmail(),encoder.encode(sigupRequest.getPassword()));
		Set<String> strRoles = sigupRequest.getRole();
		Set<Role> roles = new HashSet<>();
		
		if(strRoles ==  null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(()-> new RuntimeException("Error: Khong tim thay role"));
			roles.add(userRole);
		}
		else {
			strRoles.forEach(role ->{
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(()-> new RuntimeException("Error: role not found"));
					roles.add(adminRole);
					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR).orElseThrow(()-> new RuntimeException("Error: role not found"));
					roles.add(modRole);
					break;

				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(()-> new RuntimeException("Error: role not found"));
					roles.add(userRole);
					break;
				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	
}
