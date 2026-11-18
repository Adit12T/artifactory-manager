package io.github.adit12t.artifactorymanager.client;

import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.BuildInfo;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.BuildInfoEnvelope;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.BuildList;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.BuildRuns;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.CreateLocalRepository;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.RepositorySummary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.function.Supplier;

/** Thin, typed wrapper around the Artifactory REST API. Timeouts come from spring.http.client.* settings. */
@Component
public class ArtifactoryClient {

    private final RestClient rest;

    public ArtifactoryClient(RestClient.Builder builder, ArtifactoryProperties props) {
        this.rest = builder
                .baseUrl(props.baseUrl().toString().replaceAll("/+$", ""))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.accessToken())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public List<RepositorySummary> listRepositories(String type) {
        return call(() -> rest.get()
                .uri(uri -> type == null ? uri.path("/api/repositories").build()
                        : uri.path("/api/repositories").queryParam("type", type).build())
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<RepositorySummary>>() { }));
    }

    public void createLocalRepository(CreateLocalRepository request) {
        call(() -> rest.put().uri("/api/repositories/{key}", request.key())
                .contentType(MediaType.APPLICATION_JSON).body(request)
                .retrieve().toBodilessEntity());
    }

    public void deleteRepository(String key) {
        call(() -> rest.delete().uri("/api/repositories/{key}", key).retrieve().toBodilessEntity());
    }

    public BuildList listBuilds() {
        return call(() -> rest.get().uri("/api/build").retrieve().body(BuildList.class));
    }

    public BuildRuns listBuildRuns(String buildName) {
        return call(() -> rest.get().uri("/api/build/{name}", buildName).retrieve().body(BuildRuns.class));
    }

    public BuildInfo getBuild(String buildName, String buildNumber) {
        BuildInfoEnvelope envelope = call(() -> rest.get().uri("/api/build/{name}/{number}", buildName, buildNumber)
                .retrieve().body(BuildInfoEnvelope.class));
        return envelope == null ? null : envelope.buildInfo();
    }

    /** Maps Artifactory and network failures to clear statuses; never echoes Artifactory's raw error body. */
    private <T> T call(Supplier<T> request) {
        try {
            return request.get();
        } catch (RestClientResponseException e) {
            int code = e.getStatusCode().value();
            if (code == 404) {
                throw new ArtifactoryException(HttpStatus.NOT_FOUND, "Not found in Artifactory.");
            }
            if (code == 400 || code == 409) {
                throw new ArtifactoryException(HttpStatus.valueOf(code), "Artifactory rejected the request.");
            }
            if (code == 401 || code == 403) {
                throw new ArtifactoryException(HttpStatus.BAD_GATEWAY,
                        "Artifactory refused this service's credentials. Check ARTIFACTORY_ACCESS_TOKEN.");
            }
            throw new ArtifactoryException(HttpStatus.BAD_GATEWAY, "Artifactory returned an error (" + code + ").");
        } catch (ResourceAccessException e) {
            throw new ArtifactoryException(HttpStatus.GATEWAY_TIMEOUT, "Artifactory did not respond in time.");
        }
    }
}
