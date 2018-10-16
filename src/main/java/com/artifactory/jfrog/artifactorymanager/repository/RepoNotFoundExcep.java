package com.artifactory.jfrog.artifactorymanager.repository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RepoNotFoundExcep extends RuntimeException {

	public RepoNotFoundExcep(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
}
