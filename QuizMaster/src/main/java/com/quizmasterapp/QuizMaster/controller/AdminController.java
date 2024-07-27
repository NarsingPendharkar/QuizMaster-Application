package com.quizmasterapp.QuizMaster.controller;

import java.net.http.HttpResponse;
import java.util.List;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizmasterapp.QuizMaster.Model.Users;
import com.quizmasterapp.QuizMaster.Service.Userservice;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

	@Autowired
	private Userservice userservice;

	@GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("username", username);
            return "home";
        } else {
            return "redirect:/login"; // Redirect to login if not logged in
        }
    }

	@GetMapping("/register")
	public String registerUser() {

		return "Register";
	}

	@GetMapping("/login")
	public String loginPage() {

		return "Login";
	}

	@PostMapping("/validatelogin")
	public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password,
			HttpSession session, Model model) {
		Users foundUser = userservice.loginValidate(username);
		System.out.println(username);
		System.out.println(password);
		System.out.println(foundUser.getPassword()+" "+foundUser.getUsername());
		if (foundUser != null && foundUser.getPassword().equals(password)) {
			session.setAttribute("username", username);
			return ResponseEntity.status(HttpStatus.FOUND)
	                .header(HttpHeaders.LOCATION, "/home")
	                .body("Login successful");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
		}
	}

	@PostMapping("/createuser")
	public ResponseEntity<String> createUser(@ModelAttribute Users user) {
		userservice.registerUser(user);
		return new ResponseEntity<>("User Created", HttpStatus.OK);
	}

	@GetMapping("/getusers")
	public ResponseEntity<List<Users>> getUsers() {
		List<Users> allusers = userservice.listUsers();
		System.out.println(allusers.toString());
		return new ResponseEntity<List<Users>>(allusers, HttpStatus.OK);
	}

	@GetMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok("Logged out");
	}

}
