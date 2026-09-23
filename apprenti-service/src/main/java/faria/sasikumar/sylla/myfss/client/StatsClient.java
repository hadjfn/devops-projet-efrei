package faria.sasikumar.sylla.myfss.client;

import faria.sasikumar.sylla.myfss.model.Apprenti;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

// An unavailable summary is distinct from a valid, empty dataset.
@Slf4j
@Component
public class StatsClient {

    private final RestClient restClient;

    public StatsClient(RestClient.Builder builder,
                       @Value("${stats.service.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<StatsSummary> fetchSummary(List<Apprenti> apprentis) {
        List<ApprentiDto> payload = apprentis.stream().map(ApprentiDto::from).toList();
        try {
            StatsSummary result = restClient.post()
                    .uri("/api/stats/summary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(StatsSummary.class);
            return Optional.ofNullable(result);
        } catch (RestClientException ex) {
            log.warn("stats-service indisponible ({})", ex.getClass().getSimpleName());
            return Optional.empty();
        }
    }
}
