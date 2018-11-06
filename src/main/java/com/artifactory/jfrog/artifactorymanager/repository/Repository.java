package com.artifactory.jfrog.artifactorymanager.repository;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Repository {
	
	@Autowired
	RestTemplate restTemplate;
	
	protected Repository() {
		
	}
	
	@RequestMapping(value = "/template/build")
	   public String getBuild() {
	      HttpHeaders headers = new HttpHeaders();
	      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      HttpEntity <String> entity = new HttpEntity<String>(headers);
	      
	      return restTemplate.exchange("http://localhost:8081/artifactory/api/build", HttpMethod.GET, entity, String.class).getBody();
	   }
		 		
	@RequestMapping(value = "/template/repositories")
	   public String getArtifactoryList() {
	      HttpHeaders headers = new HttpHeaders();
	      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      HttpEntity <String> entity = new HttpEntity<String>(headers);
	      
	      return restTemplate.exchange("http://localhost:8081/artifactory/api/repositories", HttpMethod.GET, entity, String.class).getBody();
	   }
		 	
}