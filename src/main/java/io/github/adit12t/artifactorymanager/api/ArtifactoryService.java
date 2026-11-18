package io.github.adit12t.artifactorymanager.api;

import io.github.adit12t.artifactorymanager.client.ArtifactoryClient;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.BuildInfo;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.BuildRef;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.BuildRuns;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.CreateLocalRepository;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.RepositorySummary;
import io.github.adit12t.artifactorymanager.client.ArtifactoryException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ArtifactoryService {

    private final ArtifactoryClient client;

    public ArtifactoryService(ArtifactoryClient client) {
        this.client = client;
    }

    @Cacheable(cacheNames = "repositories", key = "#type == null ? 'ALL' : #type")
    public List<RepositorySummary> repositories(String type) {
        List<RepositorySummary> repos = client.listRepositories(type);
        return repos == null ? List.of() : repos.stream().sorted(Comparator.comparing(RepositorySummary::key)).toList();
    }

    @CacheEvict(cacheNames = "repositories", allEntries = true)
    public void createLocalRepository(String key, String packageType, String description) {
        client.createLocalRepository(new CreateLocalRepository(key, "local", packageType, description));
    }

    @CacheEvict(cacheNames = "repositories", allEntries = true)
    public void deleteRepository(String key) {
        client.deleteRepository(key);
    }

    @Cacheable("builds")
    public List<BuildSummary> builds() {
        var list = client.listBuilds();
        if (list == null || list.builds() == null) {
            return List.of();
        }
        return list.builds().stream()
                .map(ref -> new BuildSummary(decode(ref), ref.lastStarted()))
                .sorted(Comparator.comparing(BuildSummary::lastStarted, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public List<String> buildNumbers(String buildName) {
        BuildRuns runs = client.listBuildRuns(buildName);
        if (runs == null || runs.buildsNumbers() == null) {
            return List.of();
        }
        return runs.buildsNumbers().stream().map(run -> run.uri().replaceFirst("^/", "")).toList();
    }

    public BuildInfo build(String buildName, String buildNumber) {
        BuildInfo info = client.getBuild(buildName, buildNumber);
        if (info == null) {
            throw new ArtifactoryException(HttpStatus.NOT_FOUND, "Not found in Artifactory.");
        }
        return info;
    }

    private static String decode(BuildRef ref) {
        return java.net.URLDecoder.decode(ref.uri().replaceFirst("^/", ""), java.nio.charset.StandardCharsets.UTF_8);
    }

    public record BuildSummary(String name, String lastStarted) {
    }
}
