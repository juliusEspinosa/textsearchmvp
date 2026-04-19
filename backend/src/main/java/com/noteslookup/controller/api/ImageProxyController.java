package com.noteslookup.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/image-proxy")
public class ImageProxyController {

    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final SSLContext sslContext;

    public ImageProxyController() {
        try {
            TrustManager[] trustAll = {new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] c, String a) {}
                public void checkServerTrusted(X509Certificate[] c, String a) {}
            }};
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new java.security.SecureRandom());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public ResponseEntity<byte[]> proxy(@RequestParam String file) {
        String directUrl = cache.computeIfAbsent(file, this::resolveImageUrl);
        if (directUrl == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            var conn = (HttpsURLConnection) URI.create(directUrl).toURL().openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (InputStream is = conn.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                String contentType = conn.getContentType();
                return ResponseEntity.ok()
                        .header("Content-Type", contentType != null ? contentType : "image/jpeg")
                        .header("Cache-Control", "public, max-age=86400")
                        .body(bytes);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    private String resolveImageUrl(String filename) {
        try {
            String apiUrl = "https://wikimon.net/api.php?action=query&titles=File:"
                    + filename + "&prop=imageinfo&iiprop=url&format=json";
            var conn = (HttpsURLConnection) URI.create(apiUrl).toURL().openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (InputStream is = conn.getInputStream()) {
                String json = new String(is.readAllBytes());
                int urlIdx = json.indexOf("\"url\":\"");
                if (urlIdx < 0) return null;
                int start = urlIdx + 7;
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
