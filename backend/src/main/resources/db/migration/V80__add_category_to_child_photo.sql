-- 照片分类:可空字段,兼容历史无分类数据;分类为高频过滤条件,建立联合索引
ALTER TABLE child_photo ADD COLUMN category VARCHAR(50);

CREATE INDEX idx_child_photo_user_id_category ON child_photo(user_id, category);
