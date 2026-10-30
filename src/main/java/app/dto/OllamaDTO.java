package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaDTO {

    String response;

    public List<String> getOptions(){
        List<String> options = new ArrayList<>();

        Scanner scan = new Scanner(response);
        while (scan.hasNextLine())
            options.add(scan.nextLine().trim());
        scan.close();

        return options;
    }

    public String getQuestion(){
        return response.substring(0,response.indexOf('?'));
    }

    public String getAnswer(){
        return response.substring(response.indexOf("Answer"));
    }
}
