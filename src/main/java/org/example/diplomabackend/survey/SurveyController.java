package org.example.diplomabackend.survey;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.survey.entities.SurveyEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<?> processSurvey(
            @RequestBody SurveyEntity survey
    ) {
        return surveyService.processSurvey(survey);
    }

    @PostMapping("/audio")
    public ResponseEntity<?> processAudio(
            @RequestPart("audio") MultipartFile audio
    ) throws IOException {
        return surveyService.processAudio(audio);
    }

}
