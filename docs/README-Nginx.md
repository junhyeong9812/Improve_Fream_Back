## README - Nginx

### 개요
본 문서는 Nginx를 이용해 API 응답을 캐싱하고, 캐시를 무효화(Purge)하는 흐름을 설명합니다. 캐시를 통해 API 응답 속도를 높이고 서버 부하를 줄일 수 있으며, 새로운 데이터가 등록되거나 기존 데이터가 변경되었을 때는 Purge를 통해 캐시를 즉시 갱신하도록 합니다.

- **상품 API 캐시**: `/api/products/` 경로
- **스타일 API 캐시**: `/api/styles/queries` 경로

### 1. Nginx 캐시 흐름 요약
1. 클라이언트가 `/api/products/` 또는 `/api/styles/queries`에 GET 요청을 보냄
2. Nginx가 캐시에 해당 요청 결과가 있는지 확인
    - **HIT**: 캐시된 응답을 즉시 반환
    - **MISS**: Spring Boot(app) 컨테이너로 프록시, 응답을 받은 후 캐시에 저장
3. 상품/스타일 데이터가 새로 생성되거나 수정되는 경우:
    - Controller 혹은 Service 계층에서 `NginxCachePurgeUtil`를 통해 PURGE 요청을 전송
    - Nginx가 해당 URI의 캐시를 무효화
    - PURGE 이후 해당 URI는 MISS로 처리되어 최신 데이터를 캐싱

이를 통해 고성능 API 응답을 유지하면서, 데이터 변경 시 캐시를 즉시 반영할 수 있습니다.

---

### 2. Nginx 설정

#### 2.1 `nginx.conf` 예시
```nginx
user  nginx;
worker_processes  auto;

events {
    worker_connections  1024;
}

http {
    log_format main '$remote_addr - $remote_user [$time_local] '
                    '"$request" $status $body_bytes_sent '
                    '"$http_referer" "$http_user_agent"';

    access_log /var/log/nginx/access.log main;
    error_log /var/log/nginx/error.log warn;

    # 임시 파일 경로
    proxy_temp_path /var/cache/nginx/temp;

    # query_string이 없는 경우에만 캐시 활성화
    map $query_string $is_query_empty {
        ""       1;
        default  0;
    }

    #--------------------
    # (1) products 캐시 영역
    #--------------------
    proxy_cache_path /var/cache/nginx/products
                     levels=1:2
                     keys_zone=cache_products:10m
                     inactive=10m
                     max_size=1g;

    # (2) styles 캐시 영역
    proxy_cache_path /var/cache/nginx/styles
                     levels=1:2
                     keys_zone=cache_styles:10m
                     inactive=10m
                     max_size=1g;

    # 쿼리스트링 존재 여부에 따라 캐시 우회 결정
    map $query_string $bypass_products_cache {
        ""       0;  
        default  1;
    }
    map $query_string $bypass_styles_cache {
        ""       0;
        default  1;
    }

    server {
        listen 80;
        server_name localhost;

        location / {
            proxy_pass http://app:8080;
        }

        #--------------------------------------------
        # [상품 API 캐시] : /api/products/
        #--------------------------------------------
        location /api/products/ {
            proxy_pass http://app:8080;

            proxy_cache            cache_products;
            proxy_cache_valid      200 10m;
            proxy_cache_valid      404 1m;
            add_header             X-Cache-Status $upstream_cache_status;

            proxy_cache_bypass     $bypass_products_cache;
            proxy_no_cache         $bypass_products_cache;
        }

        #--------------------------------------------
        # [스타일 API 캐시] : /api/styles/queries
        #--------------------------------------------
        location /api/styles/queries {
            proxy_pass http://app:8080;

            proxy_cache            cache_styles;
            proxy_cache_valid      200 10m;
            proxy_cache_valid      404 1m;
            add_header             X-Cache-Status $upstream_cache_status;

            proxy_cache_bypass     $bypass_styles_cache;
            proxy_no_cache         $bypass_styles_cache;
        }

        #--------------------------------------------
        # [Purge 모듈 - 별도 모듈 필요]
        #--------------------------------------------
        location ~ /purge(/.*) {
            allow 127.0.0.1;  # 특정 IP만 허용
            deny all;

            proxy_cache_purge cache_products $scheme$host$1;
            proxy_cache_purge cache_styles   $scheme$host$1;
        }
    }
}
```

#### 주요 설정 설명
- `proxy_cache_path /var/cache/nginx/products ...`: 캐시를 저장할 디렉토리(및 크기/만료 설정)
- `proxy_cache_bypass $bypass_products_cache`: 쿼리 스트링 여부에 따라 캐시 우회
- `location ~ /purge(/.*)`: PURGE URL 패턴. Dockerfile 빌드 시 `ngx_cache_purge` 모듈 필요

---

### 3. Docker Compose 설정

#### 3.1 `docker-compose.yml` 예시
```yaml
version: '3.8'
services:

  nginx:
    build:
      context: .
      dockerfile: Dockerfile-nginx
    image: custom-nginx:1.0
    container_name: nginx_server
    depends_on:
      - app
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/cache:/var/cache/nginx  # 캐시 디렉토리
    ports:
      - "80:80"
```

---

### 4. Dockerfile (Nginx 빌드)
```dockerfile
# 1) 빌드 환경(Alpine)에서 Nginx 소스를 받아 빌드
FROM alpine:3.18 AS builder

RUN apk add --no-cache \
    gcc \
    libc-dev \
    make \
    pcre-dev \
    openssl-dev \
    zlib-dev \
    curl \
    perl

ENV NGINX_VERSION=1.25.2
ENV NGINX_PURGE_MODULE_VERSION=2.3

WORKDIR /tmp
RUN curl -LO http://nginx.org/download/nginx-${NGINX_VERSION}.tar.gz
RUN tar -zxvf nginx-${NGINX_VERSION}.tar.gz

RUN curl -LO https://github.com/FRiCKLE/ngx_cache_purge/archive/refs/tags/${NGINX_PURGE_MODULE_VERSION}.tar.gz
RUN tar -zxvf ${NGINX_PURGE_MODULE_VERSION}.tar.gz

WORKDIR /tmp/nginx-${NGINX_VERSION}
RUN ./configure \
    --prefix=/etc/nginx \
    --conf-path=/etc/nginx/nginx.conf \
    --sbin-path=/usr/local/sbin/nginx \
    --with-http_stub_status_module \
    --with-http_ssl_module \
    --with-http_gzip_static_module \
    --with-http_realip_module \
    --add-module=/tmp/ngx_cache_purge-${NGINX_PURGE_MODULE_VERSION} \
    --without-http_autoindex_module \
    --without-http_fastcgi_module \
    --without-http_uwsgi_module \
    --without-http_scgi_module

RUN make && make install

# 2) 최종 런타임 이미지
FROM alpine:3.18
RUN apk add --no-cache \
    pcre \
    openssl \
    zlib

COPY --from=builder /etc/nginx /etc/nginx
COPY --from=builder /usr/local/sbin/nginx /usr/local/sbin/nginx

RUN mkdir -p /var/cache/nginx
COPY nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

### 5. Purge 유틸 코드
#### 5.1 `NginxCachePurgeUtil.java`
```java
package Fream_back.improve_Fream_Back.utils;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class NginxCachePurgeUtil {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String nginxUrl = "http://nginx:80";

    public void purgeProductCache() {
        String purgeUrl = nginxUrl + "/purge/api/products/";
        RequestEntity<Void> requestEntity = RequestEntity
                .method(HttpMethod.valueOf("PURGE"), URI.create(purgeUrl))
                .build();

        try {
            restTemplate.exchange(requestEntity, String.class);
        } catch (Exception e) {
            // 로그 처리
        }
    }

    public void purgeStyleCache() {
        String purgeUrl = nginxUrl + "/purge/api/styles/queries";
        RequestEntity<Void> requestEntity = RequestEntity
                .method(HttpMethod.valueOf("PURGE"), URI.create(purgeUrl))
                .build();

        try {
            restTemplate.exchange(requestEntity, String.class);
        } catch (Exception e) {
            // 로그 처리
        }
    }
}
```

---

### 6. 동작 예시
#### Controller 예시
```java
@RestController
@RequestMapping("/api/product-colors")
@RequiredArgsConstructor
public class ProductColorCommandController {

   private final ProductColorCommandService productColorCommandService;
   private final UserQueryService userQueryService; // 권한 확인 서비스
   private final NginxCachePurgeUtil nginxCachePurgeUtil;
   
   @PostMapping("/{productId}")
   public ResponseEntity<Void> createProductColor(
           @PathVariable("productId") Long productId,
           @RequestPart("requestDto") ProductColorCreateRequestDto requestDto,
           @RequestPart("thumbnailImage") MultipartFile thumbnailImage,
           @RequestPart(value = "images", required = false) List<MultipartFile> images,
           @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages) {

      String email = extractEmailFromSecurityContext();
      userQueryService.checkAdminRole(email); // 관리자 권한 확인

      productColorCommandService.createProductColor(requestDto, thumbnailImage, images, detailImages, productId);
      nginxCachePurgeUtil.purgeProductCache();
      return ResponseEntity.ok().build();
   }
}
```

---

### 7. 확장: 캐시 키별 Purge & 세분화
1. URI별 Purge: `/api/products/123`처럼 특정 상품 ID로 접근 시 `/purge/api/products/123` 형태로 PURGE 요청 가능
2. 정적 리소스 캐시: 이미지, CSS/JS 등에 대해 유사 구조로 캐시 & Purge 설정 가능
3. HAProxy 등 로드밸런서와 연계: 트래픽 분산 및 캐시 효율성 증가

---

### 결론
Nginx 캐싱 및 Spring Boot 애플리케이션과 PURGE 트리거 구조를 활용하면 빈번한 데이터 조회 API 성능을 크게 향상시키면서 정확한 데이터를 제공할 수 있습니다.

