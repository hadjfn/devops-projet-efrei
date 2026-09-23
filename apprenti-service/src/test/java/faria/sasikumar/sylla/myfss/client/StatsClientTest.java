package faria.sasikumar.sylla.myfss.client;

import faria.sasikumar.sylla.myfss.model.Apprenti;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

// MockWebServer pour mocker stats-service
class StatsClientTest {

    private MockWebServer server;
    private StatsClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(500)).build());
        factory.setReadTimeout(Duration.ofSeconds(2));
        client = new StatsClient(RestClient.builder().requestFactory(factory), server.url("/").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    private Apprenti buildApprenti(long id, String prenom, int annee, boolean archived) {
        Apprenti a = new Apprenti("Nom" + id, prenom, prenom + "@e.fr",
                "0102030405", "BSc", "DA", annee);
        a.setId(id);
        a.setArchived(archived);
        return a;
    }

    @Test
    void fetchSummary_parsesResponseAndSendsPayload() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                    {
                      "total": 2,
                      "active": 1,
                      "archived": 1,
                      "countByYear": {"1": 1, "3": 1},
                      "countByProgramme": {"BSc": 2},
                      "averageYear": 2.0
                    }
                    """));

        Optional<StatsSummary> response = client.fetchSummary(List.of(
                buildApprenti(1L, "Alice", 1, false),
                buildApprenti(2L, "Bob", 3, true)
        ));

        StatsSummary result = response.orElseThrow();
        assertThat(result.total()).isEqualTo(2);
        assertThat(result.active()).isEqualTo(1);
        assertThat(result.archived()).isEqualTo(1);
        assertThat(result.countByYear()).containsEntry(1, 1L).containsEntry(3, 1L);

        RecordedRequest req = server.takeRequest(2, TimeUnit.SECONDS);
        assertThat(req).isNotNull();
        assertThat(req.getPath()).isEqualTo("/api/stats/summary");
        assertThat(req.getMethod()).isEqualTo("POST");
        assertThat(req.getBody().readUtf8()).contains("Alice", "Bob");
    }

    @Test
    void fetchSummary_returnsEmptyWhenServerErrors() {
        server.enqueue(new MockResponse().setResponseCode(500));

        Optional<StatsSummary> response = client.fetchSummary(List.of(
                buildApprenti(1L, "Alice", 1, false)
        ));

        assertThat(response).isEmpty();
    }

    @Test
    void fetchSummary_returnsUnavailableForEmptyOrMalformedResponses() {
        server.enqueue(new MockResponse().setResponseCode(204));
        server.enqueue(new MockResponse().setHeader("Content-Type", "application/json").setBody("invalid json"));
        server.enqueue(new MockResponse().setHeader("Content-Type", "application/json").setBody("{}"));
        assertThat(client.fetchSummary(List.of())).isEmpty();
        assertThat(client.fetchSummary(List.of())).isEmpty();
        assertThat(client.fetchSummary(List.of())).isEmpty();
    }

    @Test
    void fetchSummary_readTimeoutDoesNotBlockDashboardIndefinitely() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofMillis(500));
        client = new StatsClient(RestClient.builder().requestFactory(factory), server.url("/").toString());
        server.enqueue(new MockResponse().setHeadersDelay(5, TimeUnit.SECONDS)
                .setHeader("Content-Type", "application/json").setBody("{}"));
        org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(Duration.ofSeconds(3),
                () -> assertThat(client.fetchSummary(List.of())).isEmpty());
    }

    @Test
    void fetchSummary_returnsEmptyWhenServerUnreachable() throws IOException {
        // shutdown intentional : provoque une erreur de connexion
        server.shutdown();

        Optional<StatsSummary> response = client.fetchSummary(List.of(
                buildApprenti(1L, "Alice", 1, false)
        ));

        assertThat(response).isEmpty();
    }
}
