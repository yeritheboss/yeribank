CREATE TABLE user_profile (
  user_id UUID PRIMARY KEY REFERENCES app_user(id) ON DELETE CASCADE,
  full_name VARCHAR(160),
  age INTEGER,
  job_title VARCHAR(120)
);
