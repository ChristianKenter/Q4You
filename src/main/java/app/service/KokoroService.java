package app.service;
import app.dto.KokoroDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class KokoroService {
    ObjectMapper om = new ObjectMapper();

    public byte[] kokoroAPI(String ollamaResponse) throws IOException, InterruptedException, URISyntaxException {
        String kokoroURI = "http://192.168.0.35:5001/tts";
        String json = om.writeValueAsString(Map.of("text", ollamaResponse));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(kokoroURI))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        KokoroDTO dto = new KokoroDTO(response.body());

        return dto.getWav();
    }
}
