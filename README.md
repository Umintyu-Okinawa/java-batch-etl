<div id="top"></div>

# Java Batch ETL（Spring Boot × MyBatis × MariaDB）

<p align="left">
  <img src="https://img.shields.io/badge/Java-21-007396.svg?logo=java&style=for-the-badge">
  <img src="https://img.shields.io/badge/SpringBoot-3.5-6DB33F.svg?logo=springboot&style=for-the-badge&logoColor=white">
  <img src="https://img.shields.io/badge/MyBatis-3.5.15-D71F00.svg?logo=databricks&style=for-the-badge">
  <img src="https://img.shields.io/badge/MariaDB-11.3.2-003545.svg?logo=mariadb&style=for-the-badge">
  <img src="https://img.shields.io/badge/Maven-Build-1565C0.svg?logo=apache-maven&style=for-the-badge">
</p>

---

## 目次

1. [プロジェクトについて](#プロジェクトについて)
2. [使用技術](#使用技術)
3. [ディレクトリ構成](#ディレクトリ構成)
4. [開発環境構築](#開発環境構築)
5. [コマンド一覧](#コマンド一覧)
6. [トラブルシューティング](#トラブルシューティング)

---

## プロジェクトについて

CSV ⇄ MariaDB 間のデータ連携（ETL）を自動化するバッチアプリケーションです。  
Spring Batch と MyBatis を組み合わせ、実務レベルのデータ処理・エラーハンドリング・ログ記録を再現しています。  

XAMPP + phpMyAdmin 環境で実装しました。  

---

## 使用技術

| 分類 | 技術 |
|:--|:--|
| 言語 | Java 21 |
| フレームワーク | Spring Boot 3.5, Spring Batch |
| ORM | MyBatis |
| データベース | MariaDB（XAMPP） |
| テスト | JUnit5, SpringBatchTest |
| ビルド | Maven（mvnw対応） |
| 管理ツール | phpMyAdmin（XAMPP付属） |

---

## ディレクトリ構成

<details>
<summary>ディレクトリツリー</summary>

```text

java-batch-etl/
├─ src/
│ ├─ main/java/com/example/batch/
│ │ ├─ config/ # Job / Step / Reader / Writer 定義
│ │ ├─ domain/ # Entityクラス
│ │ ├─ job/ # Job構成（csvToDb, dbToCsv）
│ │ ├─ listener/ # JobExecutionListener
│ │ ├─ repository/ # MyBatis Mapper
│ │ └─ service/ # バッチスケジューラ
│ ├─ resources/
│ │ ├─ input/ # 入力CSV
│ │ ├─ output/ # 出力CSV
│ │ ├─ mapper/ # MyBatis XML
│ │ └─ application.yml
└─ pom.xml

```
</details>

---

## 開発環境構築

## 1. 前提
- Java 21  
- XAMPP（MariaDB + phpMyAdmin）または Docker  
- Maven 3.9 以上（`mvnw` でも可）

## 2. DB作成（phpMyAdmin）

```sql
CREATE DATABASE etl_demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE etl_demo;

CREATE TABLE customers (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50),
  country VARCHAR(50)
);

CREATE TABLE batch_job_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  job_name VARCHAR(100),
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20),
  read_count INT,
  write_count INT,
  skip_count INT
);
```

## 3. application.yml 設定

```
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/etl_demo
    username: root
    password:
    driver-class-name: org.mariadb.jdbc.Driver
  batch:
    jdbc:
      initialize-schema: always
```

---

## 4. 実行

 ビルド
./mvnw clean package -DskipTests

 CSV → DB 登録ジョブ
java -jar target/java-batch-etl-0.0.1-SNAPSHOT.jar \
  --spring.batch.job.name=csvToDbJob

 DB → CSV 出力ジョブ
java -jar target/java-batch-etl-0.0.1-SNAPSHOT.jar \
  --spring.batch.job.name=dbToCsvJob
  
## 5. 確認
phpMyAdmin: etl_demo.customers にデータが入っている

phpMyAdmin: etl_demo.batch_job_log に実行ログが記録されている

src/main/resources/output/ に CSV ファイルが生成されている

## コマンド一覧

```
./mvnw clean package -DskipTests	ビルド（テスト除外）
java -jar target/java-batch-etl-0.0.1-SNAPSHOT.jar --spring.batch.job.name=csvToDbJob	CSV→DB登録
java -jar target/java-batch-etl-0.0.1-SNAPSHOT.jar --spring.batch.job.name=dbToCsvJob	DB→CSV出力
mvn test	JUnitテスト実行
docker compose up -d	Dockerで起動（任意）
```

## トラブルシューティング
| エラー内容                                         | 原因                                      | 対処方法                                                             |
| --------------------------------------------- | --------------------------------------- | ---------------------------------------------------------------- |
| **CannotGetJdbcConnectionException**          | DBに接続できない（MySQLが起動していない）                | XAMPPを起動し、**MySQLを「Start」** にする。                                 |
| **Access denied for user 'root'@'localhost'** | `application.yml` のユーザー名またはパスワードが間違っている | `spring.datasource.username` と `password` の設定を再確認。               |
| **CSVが読み込まれない**                               | ファイルの場所・列数・文字コードが不一致                    | `src/main/resources/input/` に正しいCSVを配置し、**列数と文字コード(UTF-8)** を確認。 |
| **出力CSVが空になる**                                | DBにデータが存在しない                            | `SELECT * FROM customers;` を実行して、データが入っているか確認。                   |


<p align="right">(<a href="#top">トップへ戻る</a>)</p>

## 作者情報

名前：仲村莉穏

GitHub：https://github.com/Umintyu-Okinawa

学習分野：Spring Batch / MyBatis / Maven / JUnit5 / phpMyAdmin




