package com.music.streaming.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlaylistDTO {
    private Long id;
    private String name;
    private String description;
    private String coverImageUrl;
    private List<SongDTO> songs;
    private boolean isPublic;
}
