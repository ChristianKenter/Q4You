package app.service;
import app.dto.ResourceDTO;
import app.entities.VideoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FFmpegService {
    ObjectMapper om = new ObjectMapper();

    public byte[] ffmpegAPI(VideoRequest request) throws Exception {
        String ffmpegURI = "http://192.168.0.87:7070/create";
        byte[] json = om.writeValueAsBytes(request);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(ffmpegURI))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(json))
                .build();
        HttpResponse<byte[]> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        ResourceDTO dto = new ResourceDTO(response.body());
        return dto.getFile();
    }
}