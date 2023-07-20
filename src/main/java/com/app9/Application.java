package com.app9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.models.User;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private String url = "http://94.198.50.185:7081/api/users";
	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	ApplicationContext ctx;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public String getSeesionId(ResponseEntity<String> response) {
		String setCookie = response.getHeaders().get("Set-Cookie").get(0);
		return setCookie.substring(setCookie.indexOf("=") + 1, setCookie.indexOf(";"));
	}

	public String getCookieSet(ResponseEntity<String> response) {
		return response.getHeaders().get("Set-Cookie").get(0);
	}

	public String getAllUsers() {
		System.out.println("Get response:");
		ResponseEntity<String> response = restTemplate.getForEntity(url,
				String.class);
		System.out.println(response);
		System.out.println();
		return getCookieSet(response);
	}

	public void saveUser(User user, String sessionId) {
		System.out.println("Save response:");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Cookie", sessionId);
		HttpEntity<String> request = new HttpEntity<>(user.toString(), headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		System.out.println(response);
		System.out.println();
	}

	public void changeUser(User user, String sessionId) {
		System.out.println("Change response:");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", sessionId);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(user.toString(), headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
		System.out.println(response);
		System.out.println();
	}

	public void deleteUser(User user, String sessionId) {
		System.out.println("Delete response:");
		String url = this.url + "/3";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", sessionId);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(user.toString(), headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
		System.out.println(response);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("==========================================================================");
		User user = new User(3L, "James", "Brown", (byte) 25);
		String sessionHeader = getAllUsers();
		saveUser(user, sessionHeader);
		user.setName("Thomas");
		user.setLastName("Shelby");
		changeUser(user, sessionHeader);
		deleteUser(user, sessionHeader);
		System.out.println("Answer: 5ebfeb e7cb97 5dfcf9");
		System.out.println("==========================================================================");
		((ConfigurableApplicationContext) ctx).close();
	}
}
