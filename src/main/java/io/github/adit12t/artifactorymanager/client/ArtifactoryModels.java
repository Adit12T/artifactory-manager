package io.github.adit12t.artifactorymanager.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The parts of Artifactory's JSON responses this service uses. Unknown fields are ignored. */
public final class ArtifactoryModels {

    private ArtifactoryModels() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RepositorySummary(String key, String type, String packageType, String description, String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BuildList(List<BuildRef> builds) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BuildRef(String uri, String lastStarted) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BuildRuns(String uri, List<BuildRunRef> buildsNumbers) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BuildRunRef(String uri, String started) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BuildInfoEnvelope(BuildInfo buildInfo) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BuildInfo(String name, String number, String started, String url, String vcsRevision,
                            List<Module> modules) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Module(String id, List<Artifact> artifacts, List<Dependency> dependencies) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Artifact(String name, String type, String sha256) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Dependency(String id, String type, String sha256) {
    }

    public record CreateLocalRepository(String key, String rclass, String packageType, String description) {
    }
}
