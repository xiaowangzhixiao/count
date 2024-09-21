package com.manxiaozhi.count;

import com.manxiaozhi.count.storage.Storage;
import com.manxiaozhi.count.storage.StorageFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.Request;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;

public class CountServer extends AbstractHandler {

    private static final String STORAGE_TYPE = System.getProperty("storage.type", "jdbc");
    private static Storage storage;
    private static ExecutorService executorService;
    private static final Logger logger = LoggerFactory.getLogger(CountServer.class);

    static {
        storage = StorageFactory.createStorage(STORAGE_TYPE);
        executorService = new ThreadPoolExecutor(5, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));
    }
    
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("/count".equals(target) && "POST".equalsIgnoreCase(request.getMethod())) {
            JSONObject jsonRequest = parseJsonRequest(request);
            
            if (jsonRequest != null && jsonRequest.has("metricName") && jsonRequest.has("records")) {
                String metricName = jsonRequest.getString("metricName");
                JSONArray records = jsonRequest.getJSONArray("records");

                CompletableFuture.runAsync(() -> {
                    try {
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject dimension = records.getJSONObject(i);
                            if (dimension.has("timestamp")) {
                                saveToDatabase(metricName, dimension);
                            } else {
                                logger.warn("跳过缺少timestamp的数据: {}", dimension);
                            }
                        }
                        logger.info("数据成功保存到数据库");
                    } catch (Exception e) {
                        logger.error("保存数据到数据库时发生错误", e);
                    }
                }, executorService);

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("{\"status\":\"success\",\"message\":\"数据已接收，正在异步处理\"}");
            } else {
                logger.warn("收到无效的JSON请求");
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("{\"status\":\"error\",\"message\":\"无效的 JSON 格式\"}");
            }

            baseRequest.setHandled(true);
        }
    }

    private JSONObject parseJsonRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        try {
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            logger.warn("解析 JSON 请求时出错", e);
            return null;
        }
    }

    private void saveToDatabase(String metricName, JSONObject dimension) throws Exception {
        storage.saveMetric(metricName, dimension);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new CountServer());
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closeDataSource();
            shutdownExecutorService();
        }));
        
        server.start();
        server.join();
    }

    public static void closeDataSource() {
        if (storage != null) {
            storage.close();
        }
    }

    public static void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}