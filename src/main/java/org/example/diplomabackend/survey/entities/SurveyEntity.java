package org.example.diplomabackend.survey.entities;

import lombok.Data;

@Data
public class SurveyEntity {
    private String ang_1;
    private String ang_2;
    private String fru_1;
    private String fru_2;
    private String hap_1;
    private String hap_2;
    private String exc_1;
    private String exc_2;
    private String neu_1;
    private String neu_2;
    private String sad_1;
    private String sad_2;
    private Double audio_ang;
    private Double audio_fru;
    private Double audio_hap;
    private Double audio_exc;
    private Double audio_neu;
    private Double audio_sad;
}
