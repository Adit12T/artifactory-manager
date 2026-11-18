package io.github.adit12t.artifactorymanager.api;

import io.github.adit12t.artifactorymanager.api.ArtifactoryService.BuildSummary;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.BuildInfo;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.RepositorySummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1")
@SecurityRequirement(name = "apiKey")
public class ArtifactoryController {

    /** Artifactory repository keys: letters, digits, dot, dash, underscore. */
    static final String KEY = "^[A-Za-z0-9][A-Za-z0-9._-]{0,63}$";
    private static final String NAME = "^[A-Za-z0-9][A-Za-z0-9 ._:/-]{0,127}$";

    private final ArtifactoryService service;

    public ArtifactoryController(ArtifactoryService service) {
        this.service = service;
    }

    @GetMapping("/repositories")
    @Tag(name = "Repositories")
    @Operation(summary = "List repositories, optionally by type (local, remote, virtual, federated)")
    public List<RepositorySummary> repositories(
            @RequestParam(required = false) @Pattern(regexp = "^(local|remote|virtual|federated)$") String type) {
        return service.repositories(type);
    }

    @PostMapping("/repositories")
    @ResponseStatus(HttpStatus.CREATED)
    @Tag(name = "Repositories")
    @Operation(summary = "Create a local repository (admin key)")
    public void create(@Valid @RequestBody CreateRepositoryRequest request) {
        service.createLocalRepository(request.key(), request.packageType().name().toLowerCase(), request.description());
    }

    @DeleteMapping("/repositories/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Tag(name = "Repositories")
    @Operation(summary = "Delete a repository and everything in it (admin key)")
    public void delete(@PathVariable @Pattern(regexp = KEY) String key) {
        service.deleteRepository(key);
    }

    @GetMapping("/builds")
    @Tag(name = "Builds")
    @Operation(summary = "List builds, most recently started first")
    public List<BuildSummary> builds() {
        return service.builds();
    }

    @GetMapping("/builds/{name}")
    @Tag(name = "Builds")
    @Operation(summary = "List the run numbers of one build")
    public List<String> buildNumbers(@PathVariable @Pattern(regexp = NAME) String name) {
        return service.buildNumbers(name);
    }

    @GetMapping("/builds/{name}/{number}")
    @Tag(name = "Builds")
    @Operation(summary = "Build info: modules, artifacts, dependencies and VCS revision")
    public BuildInfo build(@PathVariable @Pattern(regexp = NAME) String name,
                           @PathVariable @Pattern(regexp = "^[A-Za-z0-9._-]{1,64}$") String number) {
        return service.build(name, number);
    }

    public record CreateRepositoryRequest(
            @Pattern(regexp = KEY) String key,
            @jakarta.validation.constraints.NotNull PackageType packageType,
            @Size(max = 200) String description) {
    }

    public enum PackageType { MAVEN, GRADLE, NPM, PYPI, DOCKER, GENERIC, HELM, NUGET, GO }
}
