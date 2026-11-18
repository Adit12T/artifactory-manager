package io.github.adit12t.artifactorymanager;

import io.github.adit12t.artifactorymanager.api.ArtifactoryService;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.RepositorySummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "artifactory.access-token=test-token",
        "app.api-keys.read=read-key-0123456789abcdef0123456789",
        "app.api-keys.admin=admin-key-0123456789abcdef012345678"})
@AutoConfigureMockMvc
class ArtifactoryControllerSecurityTests {

    private static final String READ = "read-key-0123456789abcdef0123456789";
    private static final String ADMIN = "admin-key-0123456789abcdef012345678";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ArtifactoryService service;

    @Test
    void requiresAValidKey() throws Exception {
        mvc.perform(get("/api/v1/repositories")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/repositories").header("X-API-Key", "wrong")).andExpect(status().isUnauthorized());
    }

    @Test
    void readKeyCanListButNotChange() throws Exception {
        when(service.repositories(null)).thenReturn(List.of(new RepositorySummary("libs", "LOCAL", "Maven", null, null)));

        mvc.perform(get("/api/v1/repositories").header("X-API-Key", READ))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].key").value("libs"));
        mvc.perform(delete("/api/v1/repositories/libs").header("X-API-Key", READ))
                .andExpect(status().isForbidden());
        verify(service, never()).deleteRepository(any());
    }

    @Test
    void adminKeyCanCreateAndDelete() throws Exception {
        mvc.perform(post("/api/v1/repositories").header("X-API-Key", ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"team-npm\",\"packageType\":\"NPM\"}"))
                .andExpect(status().isCreated());
        verify(service).createLocalRepository("team-npm", "npm", null);

        mvc.perform(delete("/api/v1/repositories/team-npm").header("X-API-Key", ADMIN))
                .andExpect(status().isNoContent());
    }

    @Test
    void rejectsUnsafeRepositoryKeys() throws Exception {
        mvc.perform(delete("/api/v1/repositories/..%2F..%2Fapi").header("X-API-Key", ADMIN))
                .andExpect(status().is4xxClientError());
        mvc.perform(post("/api/v1/repositories").header("X-API-Key", ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"bad key!\",\"packageType\":\"NPM\"}"))
                .andExpect(status().isBadRequest());
        verify(service, never()).deleteRepository(any());
        verify(service, never()).createLocalRepository(any(), any(), any());
    }
}
