package com.music.streaming.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @ManyToMany
    @JoinTable(
        name = "user_playlists",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "playlist_id")
    )
    private Set<Playlist> playlists = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "user_following_artists",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> followingArtists = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "user_liked_songs",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private Set<Song> likedSongs = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private boolean enabled = true;

    public enum Role {
        USER, ADMIN, ARTIST
    }
}
