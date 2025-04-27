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
