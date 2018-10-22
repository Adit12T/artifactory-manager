package com.artifactory.jfrog.artifactorymanager.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
public class Repository {
	
	@Size(min=13, max=15, message="IP should have 13 to 15 characters") 
	private String ipfrog;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Size(min=2, message="Name should have atleast 2 characters") 
	private String name;
	
	protected Repository() {
		
	}


	public Repository(String ipfrog, Integer id, String name) {
		super();
		this.ipfrog = ipfrog;
		this.id = id;
		this.name = name;
	}

	public String getIpfrog() {
		return ipfrog;
	}

	public void setIpfrog(String ipfrog) {
		this.ipfrog = ipfrog;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return String.format("Repository [ipfrog=%s, id=%s, name=%s]", ipfrog, id, name);
	}
	
	 	
}