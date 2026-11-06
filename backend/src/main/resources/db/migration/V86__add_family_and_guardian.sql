-- 家庭账号：family 1 ── n guardian_credential / app_user（孩子档案）
CREATE TABLE family (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 家长登录凭证：邮箱统一小写；密码只存 BCrypt 摘要；失败 5 次锁 15 分钟
CREATE TABLE guardian_credential (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    family_id INTEGER NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    failed_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (family_id) REFERENCES family(id)
);
CREATE UNIQUE INDEX idx_guardian_credential_email ON guardian_credential(email);
CREATE INDEX idx_guardian_credential_family_id ON guardian_credential(family_id);

-- 监护人同意：记录家庭、同意人、版本与时间
CREATE TABLE guardian_consent (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    family_id INTEGER NOT NULL,
    guardian_id INTEGER NOT NULL,
    consent_version VARCHAR(32) NOT NULL,
    consented_at TIMESTAMP NOT NULL,
    FOREIGN KEY (family_id) REFERENCES family(id),
    FOREIGN KEY (guardian_id) REFERENCES guardian_credential(id)
);
CREATE INDEX idx_guardian_consent_family_id ON guardian_consent(family_id, consent_version);

-- 孩子档案归属家庭；存量 user_id=1 归入默认家庭 id=1（站点所有者注册时认领）
ALTER TABLE app_user ADD COLUMN family_id INTEGER REFERENCES family(id);
INSERT INTO family (id) VALUES (1);
UPDATE app_user SET family_id = 1 WHERE family_id IS NULL;
CREATE INDEX idx_app_user_family_id ON app_user(family_id);
