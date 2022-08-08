package com.spring.security.services;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spring.models.User;
import com.spring.payload.repository.UserRepository;
@Service
public class UserDetailsServicelmpl implements UserDetailsService{

	@Autowired
	UserRepository userRepository;
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Khong the tim thay ten nay " + username));
		return UserDetailsImpl.build(user);
	}

}
