package com.music.streaming.dto;

import lombok.Data;

@Data
public class SongDTO {
    private Long id;
    private String title;
    private String artistName;
    private Long artistId;
    private String album;
    private Integer duration;
    private String genre;
    private String fileUrl;
    private String coverImageUrl;
    private Integer playCount;
}
