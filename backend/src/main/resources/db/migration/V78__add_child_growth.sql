ALTER TABLE app_user ADD COLUMN birth_date DATE;
ALTER TABLE app_user ADD COLUMN sex VARCHAR(10);

CREATE TABLE growth_measurement (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    measured_at DATE NOT NULL,
    height_cm DECIMAL(5,1),
    weight_kg DECIMAL(5,2),
    note VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    CHECK (height_cm IS NOT NULL OR weight_kg IS NOT NULL)
);

CREATE INDEX idx_growth_measurement_user_id_measured_at
    ON growth_measurement(user_id, measured_at);
