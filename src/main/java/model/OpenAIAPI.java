package model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class OpenAIAPI {

    private static final String API_KEY = "";

    public static String getProvisionalDiagnosis(String emotionData) throws IOException, InterruptedException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "Eres un profesional en salud mental que analiza emociones, da un diagnóstico provisional con los datos que se te dan y das una lista de habitos/formas de lidiar y ayudar a los pacientes que lees. Adicionalmente estas en una App la cual tambien da diagnosticos de profesionales pero tardan su debido tiempo tu trabajo es dar un diagnostico provisional en lo que llega el real."));
        messages.add(Map.of("role", "user", "content", "Dame un diagnóstico a partir de los siguientes datos: " + emotionData));
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            System.err.println("Error en la solicitud: Código " + response.statusCode());
            System.err.println("Respuesta: " + response.body());
            return null;
        }
    }

}