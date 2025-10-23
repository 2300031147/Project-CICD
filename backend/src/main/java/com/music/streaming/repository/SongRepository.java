package com.music.streaming.repository;

import com.music.streaming.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByArtistId(Long artistId);
    List<Song> findByGenre(String genre);
    
    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.artist.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.album) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Song> searchSongs(String keyword);
    
    List<Song> findTop10ByOrderByPlayCountDesc();
}
