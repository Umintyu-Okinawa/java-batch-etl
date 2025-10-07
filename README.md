# Java Batch ETL AI

## 概要
Spring Boot（Spring Batch + MyBatis）を使用して、
CSVファイルからデータベース（MariaDB）へ取り込みを行うETLバッチ処理を実装した学習用プロジェクトです。

**ETL処理の基本的な流れを理解・実装できることを目的**にしており、
今後は条件抽出・CSV出力・スケジュール実行など、業務バッチに近づけていく予定です。

---

## 構成技術
| 分類 | 使用技術 |
|------|------------|
| 言語 | Java 21 |
| フレームワーク | Spring Boot 3.5.6 / Spring Batch |
| ORM | MyBatis |
| DB | MariaDB（XAMPP環境） |
| ビルド | Maven |
| 実行 | Spring Batch CLI / jar コマンド |

---

## 実装できた範囲
- CSVファイル（`input/csv/customers.csv`）を Spring Batch で読み込み  
- MyBatis を用いて MariaDB の `customers` テーブルへ INSERT  
- Mapper XML で SQL を明示的に管理（`CustomerMapper.xml`）  
- `application-test.yml` に接続設定を記載し、環境ごとに切り替え可能  
- バッチ実行時、ログに処理状況を出力  
- DB内データを SELECT で確認（phpMyAdmin利用）

---

## 処理の流れ
1. `customers.csv` を `src/main/resources/input/csv/` に配置  
2. 下記コマンドでジョブ実行  
   ```bash
   java -jar target/java-batch-etl-ai-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=test \
     --spring.batch.job.name=csvToDbJob
