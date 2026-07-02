ALTER TABLE t_user ADD COLUMN user_intro VARCHAR(255) DEFAULT '该用户很懒，还没有添加简介';
ALTER TABLE t_user ADD COLUMN user_avatar VARCHAR(500);

CREATE TABLE t_user_avatar_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    avatar_url VARCHAR(500) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_uah_user_id (user_id)
);
