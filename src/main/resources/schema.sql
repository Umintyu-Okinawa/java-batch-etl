-- 既存データを消さずに、安全に作成／拡張する版

-- テーブルが無ければ作成
CREATE TABLE IF NOT EXISTS customers (
  id   INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL
);

-- 足りない列だけ追加（存在すれば何もしない）
ALTER TABLE customers ADD COLUMN IF NOT EXISTS country VARCHAR(255) NULL AFTER name;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS email   VARCHAR(200) NULL;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS age     INT NULL;
