curl -X POST http://localhost:8080/count \
     -H "Content-Type: application/json" \
     -d '{
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
     }'