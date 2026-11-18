package io.github.adit12t.artifactorymanager.client;

import org.springframework.http.HttpStatus;

/** A failure talking to Artifactory, with the HTTP status this service should answer with. */
public class ArtifactoryException extends RuntimeException {

    private final HttpStatus status;

    public ArtifactoryException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
