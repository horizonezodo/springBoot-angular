package com.spring.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.models.Student;
import com.spring.payload.repository.StudentRepository;

@CrossOrigin(origins = "*" , maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	
	@Autowired
	StudentRepository studentRepository;
	
	@GetMapping("/getAll")
	public List<Student> getAllStudent(){
		return studentRepository.findAll();
	}
	
	@PostMapping("/createNewOne")
	public Student createStudent(@RequestBody Student student) {
		return studentRepository.save(student);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student){
		Student s = studentRepository.findById(id).orElseThrow(()-> new RuntimeException("Khong ton tai id nay"));
		
		s.setName(student.getName());
		s.setEmail(student.getEmail());
		s.setMark(student.getMark());
		
		Student updateStudent = studentRepository.save(s);
		return ResponseEntity.ok(updateStudent);
	}
	
	@GetMapping("/getOne/{id}")
	public ResponseEntity<Student> getStudentbyId(@PathVariable Long id){
		Student student = studentRepository.findById(id).orElseThrow(()-> new RuntimeException("Khong ton tai id nay"));
		return ResponseEntity.ok(student);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity< Map<String, Boolean>> deleteStudent(@PathVariable Long id){
		Student st = studentRepository.findById(id).orElseThrow(()-> new RuntimeException("Khong tim thay id nay"));
		
		studentRepository.delete(st);
		Map<String, Boolean> reponse = new HashMap<>();
		reponse.put("deleted", Boolean.TRUE);
		return ResponseEntity.ok(reponse);
	}
	
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content .";
	}
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}
	
	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Broad.";
	}
	
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}	
	}
	
