package com.artifactory.jfrog.artifactorymanager.repository;
import static com.artifactory.jfrog.artifactorymanager.repository.BasicAuth.createHeaders;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class RestTemplateResponse {

	private RestTemplate restTemplate;

	@Autowired
	public RestTemplateResponse(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@SuppressWarnings("rawtypes")
	public ResponseEntity getResponseEntity(String url, String username, String password) {
		@SuppressWarnings("unchecked")
		HttpEntity httpEntity = new HttpEntity(createHeaders(username, password));
		ResponseEntity responseEntity;

		try {
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
		} catch (HttpStatusCodeException ex) {
			throw new RuntimeException(ex.getMessage() + " Error in fetching repositories from jfrog artifactory");
		}
		return responseEntity;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity getResponseEntityForRepositoryAction(String url, String username, String password,
			String action) {

		HttpEntity httpEntity = new HttpEntity(createHeaders(username, password));
		ResponseEntity responseEntity = null;
		try {
			if (action.equalsIgnoreCase("POST")) {
				responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
			} else if (action.equalsIgnoreCase("DELETE")) {
				responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
			}
		} catch (HttpStatusCodeException ex) {
			throw new RuntimeException(ex.getMessage() + " Error in fetching repositories from jfrog artifactory");
		}
		if (action.equalsIgnoreCase("POST") && responseEntity.getStatusCode() == HttpStatus.OK) {
			return new ResponseEntity("Repository created", HttpStatus.OK);
		} else if (action.equalsIgnoreCase("POST") && responseEntity.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity("Repository not created", HttpStatus.BAD_REQUEST);
		}

		if (action.equalsIgnoreCase("DELETE") && responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
			return new ResponseEntity("Repository deleted", HttpStatus.OK);
		} else if (action.equalsIgnoreCase("DELETE") && responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
			return new ResponseEntity("Repository not deleted", HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	// method to get branch action
	// Delete
	// Created
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity getResponseEntityForBranchAction(String url, String username, String password,
			String action) {

		HttpEntity httpEntity = new HttpEntity(createHeaders(username, password));
		ResponseEntity responseEntity = null;
		try {
			if (action.equalsIgnoreCase("POST")) {
				responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
			} else if (action.equalsIgnoreCase("DELETE")) {
				responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
			}
		} catch (HttpStatusCodeException ex) {
			throw new RuntimeException(ex.getMessage() + " Error in fetching branch from your jfrog artifactory");
		}
		if (action.equalsIgnoreCase("POST") && responseEntity.getStatusCode() == HttpStatus.CREATED) {
			return new ResponseEntity("Branch created", HttpStatus.OK);
		} else if (action.equalsIgnoreCase("POST") && responseEntity.getStatusCode() != HttpStatus.CREATED) {
			return new ResponseEntity("Branch not created", HttpStatus.BAD_REQUEST);
		}

		if (action.equalsIgnoreCase("DELETE") && responseEntity.getStatusCode() == HttpStatus.OK) {
			return new ResponseEntity("Branch deleted", HttpStatus.OK);
		} else if (action.equalsIgnoreCase("DELETE") && responseEntity.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity("Branch not deleted", HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
}
