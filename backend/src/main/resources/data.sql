-- Sample data for Music Streaming Platform
-- This script initializes the database with test data

-- Insert sample artists
INSERT INTO artists (name, bio, country, follower_count, created_at, updated_at) VALUES
('The Beatles', 'Legendary British rock band', 'United Kingdom', 150000, NOW(), NOW()),
('Queen', 'British rock band formed in London', 'United Kingdom', 120000, NOW(), NOW()),
('Michael Jackson', 'King of Pop', 'United States', 200000, NOW(), NOW()),
('Adele', 'British singer-songwriter', 'United Kingdom', 100000, NOW(), NOW()),
('Ed Sheeran', 'English singer-songwriter', 'United Kingdom', 95000, NOW(), NOW()),
('Taylor Swift', 'American singer-songwriter', 'United States', 180000, NOW(), NOW()),
('Bruno Mars', 'American singer-songwriter', 'United States', 85000, NOW(), NOW()),
('Coldplay', 'British rock band', 'United Kingdom', 110000, NOW(), NOW()),
('Imagine Dragons', 'American pop rock band', 'United States', 90000, NOW(), NOW()),
('Billie Eilish', 'American singer-songwriter', 'United States', 140000, NOW(), NOW());

-- Insert sample songs
INSERT INTO songs (title, artist_id, album, duration, genre, file_url, cover_image_url, play_count, created_at, updated_at, released_at) VALUES
('Hey Jude', 1, 'The Beatles 1967-1970', 431, 'Rock', '/music/hey-jude.mp3', '/images/beatles-1967-1970.jpg', 15000, NOW(), NOW(), '1968-08-26'),
('Let It Be', 1, 'Let It Be', 243, 'Rock', '/music/let-it-be.mp3', '/images/let-it-be.jpg', 12000, NOW(), NOW(), '1970-03-06'),
('Bohemian Rhapsody', 2, 'A Night at the Opera', 354, 'Rock', '/music/bohemian-rhapsody.mp3', '/images/night-opera.jpg', 25000, NOW(), NOW(), '1975-10-31'),
('We Will Rock You', 2, 'News of the World', 122, 'Rock', '/music/we-will-rock-you.mp3', '/images/news-world.jpg', 18000, NOW(), NOW(), '1977-10-07'),
('Billie Jean', 3, 'Thriller', 294, 'Pop', '/music/billie-jean.mp3', '/images/thriller.jpg', 30000, NOW(), NOW(), '1983-01-02'),
('Beat It', 3, 'Thriller', 258, 'Pop', '/music/beat-it.mp3', '/images/thriller.jpg', 22000, NOW(), NOW(), '1983-02-14'),
('Someone Like You', 4, '21', 285, 'Pop', '/music/someone-like-you.mp3', '/images/21.jpg', 28000, NOW(), NOW(), '2011-01-24'),
('Rolling in the Deep', 4, '21', 228, 'Pop', '/music/rolling-deep.mp3', '/images/21.jpg', 32000, NOW(), NOW(), '2010-11-29'),
('Shape of You', 5, '÷ (Divide)', 233, 'Pop', '/music/shape-of-you.mp3', '/images/divide.jpg', 35000, NOW(), NOW(), '2017-01-06'),
('Perfect', 5, '÷ (Divide)', 263, 'Pop', '/music/perfect.mp3', '/images/divide.jpg', 29000, NOW(), NOW(), '2017-03-03'),
('Shake It Off', 6, '1989', 219, 'Pop', '/music/shake-it-off.mp3', '/images/1989.jpg', 27000, NOW(), NOW(), '2014-08-18'),
('Blank Space', 6, '1989', 231, 'Pop', '/music/blank-space.mp3', '/images/1989.jpg', 26000, NOW(), NOW(), '2014-11-10'),
('Uptown Funk', 7, 'Uptown Special', 269, 'Funk', '/music/uptown-funk.mp3', '/images/uptown-special.jpg', 33000, NOW(), NOW(), '2014-11-10'),
('Just the Way You Are', 7, 'Doo-Wops & Hooligans', 220, 'Pop', '/music/just-the-way.mp3', '/images/doo-wops.jpg', 24000, NOW(), NOW(), '2010-07-20'),
('Viva la Vida', 8, 'Viva la Vida or Death and All His Friends', 242, 'Rock', '/music/viva-la-vida.mp3', '/images/viva.jpg', 28000, NOW(), NOW(), '2008-05-25'),
('Fix You', 8, 'X&Y', 293, 'Rock', '/music/fix-you.mp3', '/images/xy.jpg', 21000, NOW(), NOW(), '2005-09-05'),
('Radioactive', 9, 'Night Visions', 187, 'Rock', '/music/radioactive.mp3', '/images/night-visions.jpg', 31000, NOW(), NOW(), '2012-02-06'),
('Believer', 9, 'Evolve', 204, 'Rock', '/music/believer.mp3', '/images/evolve.jpg', 29000, NOW(), NOW(), '2017-02-01'),
('Bad Guy', 10, 'When We All Fall Asleep, Where Do We Go?', 194, 'Pop', '/music/bad-guy.mp3', '/images/wwafawdwg.jpg', 34000, NOW(), NOW(), '2019-03-29'),
('Everything I Wanted', 10, 'When We All Fall Asleep, Where Do We Go?', 245, 'Pop', '/music/everything-wanted.mp3', '/images/wwafawdwg.jpg', 25000, NOW(), NOW(), '2019-11-13');
