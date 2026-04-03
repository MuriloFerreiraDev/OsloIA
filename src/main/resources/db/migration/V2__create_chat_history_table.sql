CREATE TABLE chat_history (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              user_id UUID NOT NULL,
                              user_message TEXT NOT NULL,
                              ai_response TEXT NOT NULL,
                              created_at TIMESTAMP DEFAULT NOW(),
                              CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);