CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(64) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('USER', 'CLUB_MANAGER', 'ADMIN') NOT NULL DEFAULT 'USER',
    avatar_url VARCHAR(512),
    cover_url VARCHAR(512),
    bio VARCHAR(512),
    following_count INT NOT NULL DEFAULT 0,
    favorites_count INT NOT NULL DEFAULT 0,
    last_login_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS clubs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(180) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    tagline VARCHAR(255),
    description TEXT,
    category TINYINT NOT NULL,
    logo_url VARCHAR(512),
    cover_url VARCHAR(512),
    members_count INT NOT NULL DEFAULT 0,
    events_count INT NOT NULL DEFAULT 0,
    posts_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_clubs_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    kind ENUM('GENERAL', 'HASHTAG', 'SYSTEM') NOT NULL DEFAULT 'GENERAL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tags_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS club_members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    club_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('MEMBER', 'MANAGER') NOT NULL DEFAULT 'MEMBER',
    status ENUM('ACTIVE', 'PENDING', 'REMOVED') NOT NULL DEFAULT 'ACTIVE',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_club_members_club_user (club_id, user_id),
    KEY idx_club_members_user (user_id),
    CONSTRAINT fk_club_members_club FOREIGN KEY (club_id) REFERENCES clubs (id),
    CONSTRAINT fk_club_members_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS buildings (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(180) NOT NULL,
    lat DECIMAL(10, 7) NOT NULL,
    lng DECIMAL(10, 7) NOT NULL,
    campus_zone VARCHAR(64),
    category TINYINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(140) NOT NULL,
    description_long TEXT,
    start_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    building_id VARCHAR(32),
    room VARCHAR(64),
    club_id BIGINT,
    cover_url VARCHAR(512),
    is_official TINYINT(1) NOT NULL DEFAULT 0,
    registration_notes TEXT,
    views_count INT NOT NULL DEFAULT 0,
    likes_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_events_slug (slug),
    KEY idx_events_start_date (start_date),
    KEY idx_events_club (club_id),
    KEY idx_events_building (building_id),
    KEY idx_events_official (is_official),
    CONSTRAINT fk_events_building FOREIGN KEY (building_id) REFERENCES buildings (id),
    CONSTRAINT fk_events_club FOREIGN KEY (club_id) REFERENCES clubs (id),
    CONSTRAINT chk_events_time CHECK (end_time >= start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS event_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_event_tags_event_tag (event_id, tag_id),
    CONSTRAINT fk_event_tags_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS event_registrations (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     event_id BIGINT NOT NULL,
--     user_id BIGINT NOT NULL,
--     status ENUM('REGISTERED', 'WAITLIST', 'CANCELLED', 'CHECKED_IN') NOT NULL DEFAULT 'REGISTERED',
--     source ENUM('WEB', 'MOBILE', 'QR', 'ADMIN') NOT NULL DEFAULT 'WEB',
--     registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     remarks VARCHAR(255),
--     deleted TINYINT(1) NOT NULL DEFAULT 0,
--     UNIQUE KEY uk_event_registrations_event_user (event_id, user_id),
--     KEY idx_event_registrations_user (user_id),
--     CONSTRAINT fk_event_registrations_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
--     CONSTRAINT fk_event_registrations_user FOREIGN KEY (user_id) REFERENCES users (id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS event_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_event_favorites_user_event (user_id, event_id),
    KEY idx_event_favorites_event (event_id),
    CONSTRAINT fk_event_favorites_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_favorites_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS follows ( 
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_user_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follows_unique (follower_user_id, target_id),
    KEY idx_follows_target (target_id),
    CONSTRAINT fk_follows_follower FOREIGN KEY (follower_user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    author_user_id BIGINT,
    author_club_id BIGINT,
    content VARCHAR(500) NOT NULL,
    is_pinned TINYINT(1) NOT NULL DEFAULT 0,
    likes_count INT NOT NULL DEFAULT 0,
    comments_count INT NOT NULL DEFAULT 0,
    reposts_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_posts_author_user (author_user_id),
    KEY idx_posts_author_club (author_club_id),
    CONSTRAINT fk_posts_author_user FOREIGN KEY (author_user_id) REFERENCES users (id),
    CONSTRAINT fk_posts_author_club FOREIGN KEY (author_club_id) REFERENCES clubs (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post_media (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    type ENUM('IMAGE', 'VIDEO') NOT NULL,
    url VARCHAR(512) NOT NULL,
    order_index INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_media_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS post_likes (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     post_id BIGINT NOT NULL,
--     user_id BIGINT NOT NULL,
--     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     UNIQUE KEY uk_post_likes_post_user (post_id, user_id),
--     CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
--     CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content VARCHAR(200) NOT NULL,
    parent_comment_id BIGINT,
    likes_count INT NOT NULL DEFAULT 0,
    reviewed TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_post_comments_post (post_id),
    KEY idx_post_comments_parent (parent_comment_id),
    CONSTRAINT fk_post_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_comments_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_post_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES post_comments (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS comment_likes (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     comment_id BIGINT NOT NULL,
--     user_id BIGINT NOT NULL,
--     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     UNIQUE KEY uk_comment_likes_comment_user (comment_id, user_id),
--     CONSTRAINT fk_comment_likes_comment FOREIGN KEY (comment_id) REFERENCES post_comments (id) ON DELETE CASCADE,
--     CONSTRAINT fk_comment_likes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_tags_post_tag (post_id, tag_id),
    CONSTRAINT fk_post_tags_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS notifications (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     user_id BIGINT NOT NULL,
--     type ENUM('LIKE', 'COMMENT', 'FOLLOW', 'EVENT_UPDATE', 'SYSTEM') NOT NULL,
--     payload JSON,
--     is_read TINYINT(1) NOT NULL DEFAULT 0,
--     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     read at DATETIME,
--     CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS search_suggestions (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     kind ENUM('TAG', 'CLUB', 'USER') NOT NULL,
--     label VARCHAR(255) NOT NULL,
--     value VARCHAR(255) NOT NULL,
--     popularity_score DECIMAL(10, 4) NOT NULL DEFAULT 0,
--     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS reports (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     reporter_id BIGINT NOT NULL,
--     target_type ENUM('POST', 'COMMENT', 'USER', 'EVENT') NOT NULL,
--     target_id BIGINT NOT NULL,
--     reason VARCHAR(255) NOT NULL,
--     status ENUM('OPEN', 'REVIEWED', 'ACTIONED') NOT NULL DEFAULT 'OPEN',
--     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     resolved_at DATETIME,
--     resolved_by BIGINT,
--     CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users (id),
--     CONSTRAINT fk_reports_resolver FOREIGN KEY (resolved_by) REFERENCES users (id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS media_assets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hash VARCHAR(128) NOT NULL,
    url VARCHAR(512) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    width INT,
    height INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_media_assets_hash (hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS webhook_subscriptions (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     target_url VARCHAR(512) NOT NULL,
--     secret VARCHAR(255),
--     event_types VARCHAR(255) NOT NULL,
--     is_active TINYINT(1) NOT NULL DEFAULT 1,
--     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS audit_log (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     actor_user_id BIGINT NOT NULL,
--     action VARCHAR(120) NOT NULL,
--     target_type VARCHAR(80),
--     target_id BIGINT,
--     metadata JSON,
--     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     CONSTRAINT fk_audit_log_actor FOREIGN KEY (actor_user_id) REFERENCES users (id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS rate_limit_events (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     user_id BIGINT,
--     endpoint VARCHAR(255) NOT NULL,
--     action VARCHAR(80),
--     occurred_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     metadata JSON,
--     KEY idx_rate_limit_user (user_id),
--     CONSTRAINT fk_rate_limit_user FOREIGN KEY (user_id) REFERENCES users (id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
