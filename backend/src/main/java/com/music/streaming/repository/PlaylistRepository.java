package com.music.streaming.repository;

import com.music.streaming.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    
    @Query("SELECT p FROM Playlist p JOIN p.users u WHERE u.id = :userId")
    List<Playlist> findByUserId(Long userId);
    
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true")
    List<Playlist> findAllPublic();
}
