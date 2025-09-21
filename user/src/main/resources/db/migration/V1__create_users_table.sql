CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role user_role NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Insert an initial admin user (password: admin123)
-- Note: Change this password after first login in production
INSERT INTO users (username, email, password, first_name, last_name, role, enabled)
VALUES (
    'admin',
    'admin@quizzy.com',
    '$2a$12$XH9X7f3W8pN2ZQ1Jk5hYIuJjKvLmNpOqRrStUvWxYzA1B2C3D4E5F',
    'System',
    'Administrator',
    'ADMIN',
    TRUE
) ON CONFLICT (email) DO NOTHING;
