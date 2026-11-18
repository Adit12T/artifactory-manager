package io.github.adit12t.artifactorymanager.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

/**
 * Where Artifactory is and how to reach it. The access token comes from the environment and is
 * sent as a Bearer token; it is never logged or returned.
 */
@Validated
@ConfigurationProperties("artifactory")
public record ArtifactoryProperties(
        @NotNull URI baseUrl,
        @NotBlank String accessToken) {

    @Override
    public String toString() {
        return "ArtifactoryProperties[baseUrl=" + baseUrl + ", accessToken=****]";
    }
}
