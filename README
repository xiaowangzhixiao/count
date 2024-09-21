# Count Server

Count Server 是一个基于 Java 的微服务，用于接收和存储指标数据。它使用 Jetty 作为嵌入式 Web 服务器，HikariCP 作为数据库连接池，并使用 Log4j2 进行日志记录。

## 功能特性

- 接收 JSON 格式的指标数据
- 异步处理和存储数据到 MySQL 数据库
- 使用连接池优化数据库连接
- 详细的日志记录

## 先决条件

- Java 11 或更高版本
- Maven 3.6 或更高版本
- MySQL 数据库

## 构建和运行

1. 克隆仓库：

   ```
   git clone https://github.com/yourusername/count-server.git
   cd count-server
   ```

2. 使用 Maven 构建项目：

   ```
   mvn clean package
   ```

3. 运行应用程序：

   ```
   java -jar target/count-server-1.0-SNAPSHOT.jar
   ```

## Docker 支持

你可以使用 Docker 来运行这个应用。以下是详细的步骤：

1. 构建 Docker 镜像：

   ```
   docker build -t count-server .
   ```

2. 运行 Docker 容器：

   ```
   docker run -d \
     --name count-server \
     -p 8080:8080 \
     -v /path/on/host/logs:/app/logs \
     -e DB_URL=jdbc:mysql://host.docker.internal:3306/your_database \
     -e DB_USER=your_username \
     -e DB_PASSWORD=your_password \
     count-server
   ```

   命令说明：
   - `-d`: 在后台运行容器
   - `--name count-server`: 为容器指定一个名称
   - `-p 8080:8080`: 将容器的 8080 端口映射到主机的 8080 端口
   - `-v /path/on/host/logs:/app/logs`: 将主机上的日志目录挂载到容器中
   - `-e DB_URL=...`: 设置数据库 URL 环境变量
   - `-e DB_USER=...`: 设置数据库用户名环境变量
   - `-e DB_PASSWORD=...`: 设置数据库密码环境变量

   注意：
   - 将 `/path/on/host/logs` 替换为你想在主机上存储日志的实际路径
   - 将 `your_database`、`your_username` 和 `your_password` 替换为你的实际数据库名、用户名和密码
   - `host.docker.internal` 是 Docker Desktop 中用于访问主机网络的特殊 DNS 名称。如果你的 MySQL 数据库运行在其他位置，请相应地修改 DB_URL

3. 数据库配置：

   应用程序使用环境变量来配置数据库连接。确保在运行容器时提供了正确的环境变量：
   
   - `DB_URL`: JDBC URL，格式为 `jdbc:mysql://hostname:port/database`
   - `DB_USER`: 数据库用户名
   - `DB_PASSWORD`: 数据库密码

   如果你的 MySQL 数据库也在 Docker 中运行，你可以使用 Docker 网络来连接它们。首先，创建一个 Docker 网络：

   ```
   docker network create count-network
   ```

   然后，确保你的 MySQL 容器连接到这个网络，并使用 `--network` 选项运行 count-server：

   ```
   docker run -d \
     --name count-server \
     --network count-network \
     -p 8080:8080 \
     -v /path/on/host/logs:/app/logs \
     -e DB_URL=jdbc:mysql://mysql-container:3306/your_database \
     -e DB_USER=your_username \
     -e DB_PASSWORD=your_password \
     count-server
   ```

   这里，`mysql-container` 是你的 MySQL Docker 容器的名称。

4. 验证容器运行状态：

   ```
   docker ps
   ```

   你应该能看到 `count-server` 容器正在运行。

5. 查看日志：

   ```
   docker logs count-server
   ```

   或者直接查看挂载的日志目录 `/path/on/host/logs`。

## 数据库结构
MySQL 表名和结构如下：
```sql
CREATE TABLE metrics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    metric_name VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    dimensions JSON NOT NULL
);
```
请确保你的 MySQL 数据库已经创建了必要的表结构。如果没有，你可能需要在首次运行前执行数据库创建。

目前项目只支持MySql数据库，后续会支持更多数据库。

## 使用方法

发送 POST 请求到 `/count` 端点，数据格式如下：
```json
{
  "metricName": "page_views",
  "records": [
    {
      "timestamp": 1621234567890,
      "page": "/home",
      "user_type": "registered",
      "browser": "chrome"
    },
    {
      "timestamp": 1621234567891,
      "page": "/products",
      "user_type": "guest",
      "browser": "firefox"
    }
  ]
}
```
## 贡献

欢迎提交 Pull Requests。对于重大更改，请先开 issue 讨论您想要改变的内容。

## 许可证

[MIT](https://choosealicense.com/licenses/mit/)