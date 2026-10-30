package app.service;

import app.dto.ResourceDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ResourceService {

    private byte[] API(String path, String file) throws URISyntaxException, IOException, InterruptedException {
        String URI = "http://192.168.0.68:7070/" + path;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URI + file))
                .GET()
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        ResourceDTO dto = new ResourceDTO(response.body());
        return dto.getFile();
    }

    public byte[] imageAPI(String file) throws URISyntaxException, IOException, InterruptedException {
        return API("images/", file);
    }

    public byte[] backgroundMusicAPI(String file) throws URISyntaxException, IOException, InterruptedException {
        return API("sounds/", file);
    }

    public byte[] countdownAPI(String file) throws URISyntaxException, IOException, InterruptedException {
        return API("sounds/", file);
    }

    public byte[] whooshAPI(String file) throws URISyntaxException, IOException, InterruptedException {
        return API("sounds/", file);
    }
}

