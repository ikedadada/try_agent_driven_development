# hello-jetty: Jetty + Maven 最小構成 WebAPI

## 概要

Jetty を組み込んだ最小構成の WebAPI（Hello World）を Maven プロジェクトとして構築します。

## 作業記録

### 1. プロジェクト作成

```
mvn archetype:generate -DgroupId=com.example -DartifactId=hello-jetty -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

### 2. Jetty 依存追加

`pom.xml`に以下を追記：

- org.eclipse.jetty:jetty-server
- org.eclipse.jetty:jetty-servlet

### 3. API 実装

- `App.java`を Jetty のエントリポイント兼サーブレットに書き換え。
- `/hello`エンドポイントで`Hello World`を返す API を実装。

### 4. サーバ起動方法

```
cd hello-jetty
mvn compile exec:java -Dexec.mainClass="com.example.App"
```

- ブラウザや curl で http://localhost:8080/hello にアクセスし、`Hello World`が表示されることを確認。

## JSON 形式でのレスポンス強化・クエリパラメータ対応

- Jackson を導入し、Java オブジェクトを JSON 文字列に変換して返却するように`App.java`を修正。
- `/hello`エンドポイントで`name`クエリパラメータを受け取り、指定があれば`{"message": "Hello :name:"}`、なければ`{"message": "Hello World"}`を返す。
- 例:
  - `curl 'http://localhost:8080/hello'` → `{"message":"Hello World"}`
  - `curl 'http://localhost:8080/hello?name=Copilot'` → `{"message":"Hello Copilot:"}`

## 実行可能 jar（fat jar）の作成と実行

### ビルド

```
mvn clean package
```

`target/hello-jetty-1.0-SNAPSHOT.jar` が生成されます。

### 実行

```
java -jar target/hello-jetty-1.0-SNAPSHOT.jar
```

Jetty サーバが起動し、API が利用可能になります。

### 5. 環境ごとのプロパティ切り替え対応

- `src/main/resources`配下に `application-local.properties`, `application-stg.properties`, `application-prd.properties` を作成し、各環境ごとに `port` を指定。
- `pom.xml` に Apache Commons Configuration の依存を追加。
- `Config.java` を新規作成し、`-Denv=local|stg|prd` で環境を切り替え、該当プロパティファイルから値を取得できるようにした。
- `App.java` で `Config.getPort()` を使い、環境ごとに Listen するポートを切り替え。

#### 実行例

- デフォルト（local）
  ```
  java -jar target/hello-jetty-1.0-SNAPSHOT.jar
  # → application-local.properties の port で起動
  ```
- stg 環境
  ```
  java -Denv=stg -jar target/hello-jetty-1.0-SNAPSHOT.jar
  # → application-stg.properties の port で起動
  ```
- prd 環境

  ```
  java -Denv=prd -jar target/hello-jetty-1.0-SNAPSHOT.jar
  # → application-prd.properties の port で起動
  ```

- 他クラスからは `Config.getPort()` でポート番号、`Config.getEnv()` で現在の環境名を取得可能。

## ユーザー管理 API (CRUD) の追加

### 概要

- `/users` エンドポイントを追加し、ユーザー（id, name, email）をメモリ上で管理するシンプルな REST API を実装。
- `User.java` エンティティを新規作成。
- `App.java` に `UserServlet` を追加し、CRUD（GET, POST, PUT, DELETE）を実装。
- ID は UUID（Java 標準の `java.util.UUID`）で自動生成。
- Jackson で JSON シリアライズ/デシリアライズ。

### 使い方例

#### ユーザー一覧取得

```
curl -X GET http://localhost:8080/users
```

#### ユーザー新規作成

```
curl -X POST http://localhost:8080/users -H 'Content-Type: application/json' -d '{"name":"Taro","email":"taro@example.com"}'
```

#### ユーザー取得（ID 指定）

```
curl -X GET http://localhost:8080/users/{id}
```

#### ユーザー更新

```
curl -X PUT http://localhost:8080/users/{id} -H 'Content-Type: application/json' -d '{"name":"Hanako","email":"hanako@example.com"}'
```

#### ユーザー削除

```
curl -X DELETE http://localhost:8080/users/{id}
```

- データはメモリ上のみで永続化されません。
- UUID は自動生成されます。
