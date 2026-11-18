package io.github.adit12t.artifactorymanager;

import io.github.adit12t.artifactorymanager.client.ArtifactoryClient;
import io.github.adit12t.artifactorymanager.client.ArtifactoryException;
import io.github.adit12t.artifactorymanager.client.ArtifactoryModels.CreateLocalRepository;
import io.github.adit12t.artifactorymanager.client.ArtifactoryProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** The client against a scripted Artifactory: URLs, auth header, parsing and error mapping. */
class ArtifactoryClientTests {

    private MockRestServiceServer server;
    private ArtifactoryClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ArtifactoryClient(builder, new ArtifactoryProperties(
                URI.create("http://artifactory.test/artifactory/"), "test-token"));
    }

    @Test
    void listsRepositoriesWithBearerToken() {
        server.expect(requestTo("http://artifactory.test/artifactory/api/repositories?type=local"))
                .andExpect(header("Authorization", "Bearer test-token"))
                .andRespond(withSuccess("""
                        [{"key":"libs-release","type":"LOCAL","packageType":"Maven","url":"http://x","extra":1}]
                        """, MediaType.APPLICATION_JSON));

        var repos = client.listRepositories("local");

        assertThat(repos).singleElement().satisfies(r -> {
            assertThat(r.key()).isEqualTo("libs-release");
            assertThat(r.packageType()).isEqualTo("Maven");
        });
        server.verify();
    }

    @Test
    void createsALocalRepository() {
        server.expect(requestTo("http://artifactory.test/artifactory/api/repositories/team-npm"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"key\":\"team-npm\",\"rclass\":\"local\",\"packageType\":\"npm\"}"))
                .andRespond(withSuccess());

        client.createLocalRepository(new CreateLocalRepository("team-npm", "local", "npm", null));
        server.verify();
    }

    @Test
    void readsBuildInfo() {
        server.expect(requestTo("http://artifactory.test/artifactory/api/build/orders-service/42"))
                .andRespond(withSuccess("""
                        {"buildInfo":{"name":"orders-service","number":"42","vcsRevision":"abc123",
                          "modules":[{"id":"io.example:orders:1.0","artifacts":[{"name":"orders.jar","type":"jar"}]}]}}
                        """, MediaType.APPLICATION_JSON));

        var info = client.getBuild("orders-service", "42");

        assertThat(info.vcsRevision()).isEqualTo("abc123");
        assertThat(info.modules().get(0).artifacts().get(0).name()).isEqualTo("orders.jar");
    }

    @Test
    void mapsErrorsWithoutLeakingArtifactoryMessages() {
        server.expect(requestTo("http://artifactory.test/artifactory/api/build/missing"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).body("internal path /var/opt/jfrog"));
        server.expect(requestTo("http://artifactory.test/artifactory/api/build"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> client.listBuildRuns("missing"))
                .isInstanceOf(ArtifactoryException.class)
                .hasMessageNotContaining("/var/opt")
                .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
        assertThatThrownBy(() -> client.listBuilds())
                .extracting("status").isEqualTo(HttpStatus.BAD_GATEWAY);
    }
}
