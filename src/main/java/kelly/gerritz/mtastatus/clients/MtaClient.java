package kelly.gerritz.mtastatus.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MtaClient {
    private final String mtaUrl;
    private RestTemplate restTemplate;

    @Autowired
    public MtaClient(@Value("${mta.url}") String mtaUrl) {
        this.mtaUrl = mtaUrl;
        restTemplate = new RestTemplate();
    }

    public String fetchMtaInfo() {
        return restTemplate.getForObject(mtaUrl, String.class);
    }
}
