package it.chrccl.carrozzeriaonline.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class CAP {

    String cap;

    String city;

    String province;

    String provinceCode;

    String region;

    Double lat;

    Double lon;

    @JsonCreator
    public CAP(
            @JsonProperty("denominazioneIta") String city,
            @JsonProperty("cap") String cap,
            @JsonProperty("siglaProvincia") String provinceCode,
            @JsonProperty("denominazioneProvincia") String province,
            @JsonProperty("denominazioneRegione") String region,
            @JsonProperty("lat") Double lat,
            @JsonProperty("lon") Double lon
    ) {
        this.city = city;
        this.cap = cap;
        this.provinceCode = provinceCode;
        this.province = province;
        this.region = region;
        this.lat = lat;
        this.lon = lon;
    }

}
