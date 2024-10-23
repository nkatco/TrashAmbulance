package org.company.trashambulance.services;

import org.company.trashambulance.models.FactResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RandomFactsService {

    private static final String RANDOM_FACT_URL = "https://uselessfacts.jsph.pl/api/v2/facts/random?language=ru";

    @Autowired
    private RestTemplate restTemplate;

    public String getRandomFact() {
        ResponseEntity<FactResponse> responseEntity = restTemplate.getForEntity(RANDOM_FACT_URL, FactResponse.class);

        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)) {
            FactResponse response = responseEntity.getBody();
            return response != null ? response.getText() : "No fact available.";
        } else {
            return "Failed to fetch fact.";
        }
    }
}
