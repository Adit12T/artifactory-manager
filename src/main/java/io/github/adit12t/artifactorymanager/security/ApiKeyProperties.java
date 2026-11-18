package io.github.adit12t.artifactorymanager.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Two API keys from the environment: one can read, the other can also create and delete repositories. */
@Validated
@ConfigurationProperties("app.api-keys")
public record ApiKeyProperties(@NotBlank @Size(min = 32) String read, @NotBlank @Size(min = 32) String admin) {

    @Override
    public String toString() {
        return "ApiKeyProperties[read=****, admin=****]";
    }
}
