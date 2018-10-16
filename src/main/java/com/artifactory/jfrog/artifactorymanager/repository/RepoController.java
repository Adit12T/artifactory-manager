package com.artifactory.jfrog.artifactorymanager.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepoController {
	
	@Autowired
	private RepoService service;
	
	@GetMapping(path="/ip/artifactory/api/repositories")
	public Repository retrieveUser(@PathVariable String ip) {
		Repository repo = service.findOne(ip);
		
		if(repo==null)
			throw new RepoNotFoundExcep("ip "+ ip); 
		
		return repo;
	}
	
	@GetMapping(path="/artifactory")
	public List<Repository> retrieveAllRepos(){
		return service.findAll();
	}
	

}
