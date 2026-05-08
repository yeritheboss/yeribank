INSERT INTO user_profile (user_id, full_name, age, job_title)
SELECT
  'b32cd2d0-913f-4b4c-9ace-9800dfe90274',
  'Gerangel Berroteran Diaz',
  35,
  'Architect Software Engineer'
WHERE EXISTS (
  SELECT 1 FROM app_user WHERE id = 'b32cd2d0-913f-4b4c-9ace-9800dfe90274'
);

INSERT INTO user_profile (user_id, full_name, age, job_title)
SELECT
  'f5258161-0d19-4dd5-b0ff-af219cff18a0',
  'Geminis Diaz',
  25,
  'Marketing Designer'
WHERE EXISTS (
  SELECT 1 FROM app_user WHERE id = 'f5258161-0d19-4dd5-b0ff-af219cff18a0'
);
