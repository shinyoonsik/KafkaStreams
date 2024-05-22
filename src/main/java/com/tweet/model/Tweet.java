package com.crypto.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tweet {

    @JsonProperty(value = "CreatedAt")
    private Long createdAt;

    @JsonProperty(value = "Id")
    private Long id;

    @JsonProperty(value = "Lang")
    private String lang;

    @JsonProperty(value = "Retweet")
    private Boolean retweet;

    @JsonProperty(value = "Text")
    private String text;

    @JsonProperty(value = "Warning")
    private int warning = 0;

    public Boolean isRetweet(){
        return this.retweet;
    }

    public static Tweet translateToEng(Tweet original, String text){
        return Tweet.builder()
                .createdAt(original.getCreatedAt())
                .id(original.getId())
                .lang("en")
                .retweet(original.getRetweet())
                .text(text)
                .warning(original.getWarning())
                .build();
    }
}
