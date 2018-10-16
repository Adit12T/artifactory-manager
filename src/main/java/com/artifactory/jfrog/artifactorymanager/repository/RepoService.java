package com.artifactory.jfrog.artifactorymanager.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RepoService {

	private static List<Repository> repos = new ArrayList<>();
	static {
		repos.add(new Repository("101.201.301.401", 1, "ABC"));
		repos.add(new Repository("102.202.302.402", 2, "DEF"));
		repos.add(new Repository("103.203.303.403", 3, "GHI"));
		repos.add(new Repository("101.201.301.401", 4, "JKL"));
		repos.add(new Repository("102.202.302.402", 5, "MNO"));
		repos.add(new Repository("103.203.303.403", 6, "PQR"));
		repos.add(new Repository("101.201.301.401", 7, "STU"));
		repos.add(new Repository("102.202.302.402", 8, "VWX"));
		repos.add(new Repository("103.203.303.403", 9, "YZA"));
	}
	public List<Repository> findAll(){
		return repos;
	}
	public Repository findOne(String ip) {
		for(Repository repo:repos){
			if(repo.getIpfrog() == ip) {
				return repo;
			}
		}
		return null;
	}
	
}
