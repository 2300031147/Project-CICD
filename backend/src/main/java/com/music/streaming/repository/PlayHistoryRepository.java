package com.music.streaming.repository;

import com.music.streaming.model.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    
    @Query("SELECT ph FROM PlayHistory ph WHERE ph.user.id = :userId ORDER BY ph.playedAt DESC")
    List<PlayHistory> findByUserIdOrderByPlayedAtDesc(Long userId);
    
    @Query("SELECT ph.song.id, COUNT(ph) as playCount FROM PlayHistory ph WHERE ph.user.id = :userId " +
           "GROUP BY ph.song.id ORDER BY playCount DESC")
    List<Object[]> findMostPlayedSongsByUser(Long userId);
}
