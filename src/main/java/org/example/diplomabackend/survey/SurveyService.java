package org.example.diplomabackend.survey;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.survey.entities.SurveyEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SurveyService {

    public final RestTemplate restTemplate;

    public final Map<String, Double> answerWeights = Map.of(
            "Дуже рідко", 0.0,
            "Рідко", 0.25,
            "Іноді", 0.5,
            "Часто", 0.75,
            "Дуже часто", 1.0
    );

    public ResponseEntity<?> processSurvey(SurveyEntity survey){

        Map<String, List<String>> emotionKeys = Map.of(
                "ang", List.of(survey.getAng_1(), survey.getAng_2()),
                "fru", List.of(survey.getFru_1(), survey.getFru_2()),
                "hap", List.of(survey.getHap_1(), survey.getHap_2()),
                "exc", List.of(survey.getExc_1(), survey.getExc_2()),
                "neu", List.of(survey.getNeu_1(), survey.getNeu_2()),
                "sad", List.of(survey.getSad_1(), survey.getSad_2())
        );

        Map<String, Double> emotionScores = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : emotionKeys.entrySet()) {
            double sum = 0;
            for (String answer : entry.getValue()) {
                sum += answerWeights.getOrDefault(answer, 0.0);
            }
            emotionScores.put(entry.getKey(), sum / entry.getValue().size());
        }

        Map<String, Double> audioresult = new HashMap<>();

        audioresult.put("ang", survey.getAudio_ang());
        audioresult.put("ang", survey.getAudio_ang());
        audioresult.put("fru", survey.getAudio_fru());
        audioresult.put("hap", survey.getAudio_hap());
        audioresult.put("exc", survey.getAudio_exc());
        audioresult.put("neu", survey.getAudio_neu());
        audioresult.put("sad", survey.getAudio_sad());

        Map<String, Double> finalScores = new HashMap<>();

        for (String emotion : audioresult.keySet()) {
            double blended = audioresult.get(emotion) * 0.7 + emotionScores.get(emotion) * 0.3;
            finalScores.put(emotion, blended);
        }

        return ResponseEntity.ok(finalScores);
    }

    public ResponseEntity<?> processAudio(MultipartFile audio) throws IOException {
        ByteArrayResource resource = new ByteArrayResource(audio.getBytes()){
            @Override
            public String getFilename() {
                return "audio.wav";
            }
        };
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        //TODO: Move Python URL to some kind of configuration
        ResponseEntity<String> result = restTemplate.postForEntity("http://localhost:8000/predict", request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(result.getBody());
        JsonNode probabilities = root.get("probabilities");

        return ResponseEntity.ok(probabilities);
    }
}
