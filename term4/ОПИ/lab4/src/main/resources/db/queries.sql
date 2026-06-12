-- Create points table
CREATE TABLE IF NOT EXISTS points (
                                      id SERIAL PRIMARY KEY,
                                      x DOUBLE PRECISION,
                                      y DOUBLE PRECISION,
                                      radius DOUBLE PRECISION,
                                      hit BOOLEAN,
                                      check_time TIMESTAMP,
                                      execution_time BIGINT
);

-- Save point
INSERT INTO points (x, y, radius, hit, check_time, execution_time)
VALUES (?, ?, ?, ?, ?, ?);

-- Get all points
SELECT * FROM points ORDER BY check_time DESC;

-- Delete point by id
DELETE FROM points WHERE id = ?;

-- Delete all points
DELETE FROM points;