CREATE TABLE IF NOT EXISTS Media (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     name TEXT NOT NULL,
                                     duration INTEGER NOT NULL CHECK(duration > 0),
    type TEXT NOT NULL CHECK(type IN ('SONG', 'PODCAST')),
    creator TEXT NOT NULL,

    album TEXT,
    genre TEXT,
    price NUMERIC(5,2) DEFAULT 0.99 CHECK(price >= 0),

    host TEXT,
    episode_number INTEGER DEFAULT 0 CHECK(episode_number >= 0),
    category TEXT,

    UNIQUE(name, type, creator)
    );

CREATE TABLE IF NOT EXISTS Playlist(
                                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                         name TEXT NOT NULL UNIQUE,
                                         description TEXT
);

CREATE TABLE IF NOT EXISTS playlist_items (
                                              playlist_id BIGINT NOT NULL,
                                              media_id BIGINT NOT NULL,
                                              position INTEGER DEFAULT 0 CHECK (position >= 0),

    PRIMARY KEY (playlist_id, media_id),

    CONSTRAINT fk_playlist
    FOREIGN KEY (playlist_id)
    REFERENCES Playlist (id)
    ON DELETE CASCADE,

    CONSTRAINT fk_media
    FOREIGN KEY (media_id)
    REFERENCES media (id)
    ON DELETE CASCADE
    );


INSERT INTO Media (name, duration, type, creator, album, genre, price) VALUES
                                                                           ('Bohemian Rhapsody', 354, 'SONG', 'Queen', 'A Night at the Opera', 'Rock', 1.29),
                                                                           ('Imagine', 183, 'SONG', 'John Lennon', 'Imagine', 'Pop', 0.99),
                                                                           ('Stairway to Heaven', 482, 'SONG', 'Led Zeppelin', 'Led Zeppelin IV', 'Rock', 1.29),
                                                                           ('Billie Jean', 294, 'SONG', 'Michael Jackson', 'Thriller', 'Pop', 0.99),
                                                                           ('Smells Like Teen Spirit', 301, 'SONG', 'Nirvana', 'Nevermind', 'Grunge', 1.29),
                                                                           ('Hotel California', 391, 'SONG', 'Eagles', 'Hotel California', 'Rock', 1.29),
                                                                           ('Sweet Child O Mine', 356, 'SONG', 'Guns N Roses', 'Appetite for Destruction', 'Rock', 1.29),
                                                                           ('Thriller', 357, 'SONG', 'Michael Jackson', 'Thriller', 'Pop', 0.99);

INSERT INTO Media (name, duration, type, creator, host, episode_number, category) VALUES
                                                                                      ('The Joe Rogan Experience', 7200, 'PODCAST', 'Joe Rogan', 'Joe Rogan', 1987, 'Comedy'),
                                                                                      ('Hardcore History', 14400, 'PODCAST', 'Dan Carlin', 'Dan Carlin', 68, 'History'),
                                                                                      ('Serial - Season 1', 2700, 'PODCAST', 'This American Life', 'Sarah Koenig', 1, 'True Crime'),
                                                                                      ('How I Built This', 3600, 'PODCAST', 'NPR', 'Guy Raz', 250, 'Business'),
                                                                                      ('The Daily', 1800, 'PODCAST', 'The New York Times', 'Michael Barbaro', 1500, 'News');

INSERT INTO Playlist (name, description) VALUES
                                              ('Classic Rock Anthems', 'The greatest rock songs of all time'),
                                              ('Pop Legends', 'Iconic pop hits that defined generations'),
                                              ('Educational Podcasts', 'Learn something new every day'),
                                              ('Morning Motivation', 'Start your day with energy');

INSERT INTO playlist_items (playlist_id, media_id, position) VALUES
                                                                 (1, 1, 1),  -- Bohemian Rhapsody
                                                                 (1, 3, 2),  -- Stairway to Heaven
                                                                 (1, 6, 3),  -- Hotel California
                                                                 (1, 7, 4);  -- Sweet Child O Mine

INSERT INTO playlist_items (playlist_id, media_id, position) VALUES
                                                                 (2, 2, 1),  -- Imagine
                                                                 (2, 4, 2),  -- Billie Jean
                                                                 (2, 8, 3);  -- Thriller

INSERT INTO playlist_items (playlist_id, media_id, position) VALUES
                                                                 (3, 10, 1), -- Hardcore History
                                                                 (3, 12, 2), -- How I Built This
                                                                 (3, 13, 3); -- The Daily


CREATE INDEX IF NOT EXISTS idx_media_type ON media(type);
CREATE INDEX IF NOT EXISTS idx_media_creator ON media(creator);
CREATE INDEX IF NOT EXISTS idx_media_name ON media(name);
CREATE INDEX IF NOT EXISTS idx_playlist_items_playlist ON playlist_items(playlist_id);
CREATE INDEX IF NOT EXISTS idx_playlist_items_media ON playlist_items(media_id);