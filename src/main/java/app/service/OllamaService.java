package app.service;
import app.dto.OllamaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class OllamaService {
    ObjectMapper om = new ObjectMapper();

    public OllamaDTO ollamaAPI(String prompt) throws IOException, InterruptedException, URISyntaxException {
        String ollamaURI = "http://192.168.0.45:11434/api/generate";
        String json = om.writeValueAsString(Map.of(
                "model", "qwen2.5:3b",
                "prompt", prompt,
                "stream", false
        ));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(ollamaURI))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return om.readValue(response.body(), OllamaDTO.class);
    }
}
