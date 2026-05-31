# Araç Takip — Mikroservis Öğrenme Projesi

Spring Boot 4 ile mikroservis mimarisi öğrenme amaçlı proje.

## Servisler

- **route-service** — Rota yönetimi (OSRM entegrasyonu)
- **tracking-service** — Araç simülasyonu (yakında)

## Çalıştırma

```bash
docker compose up -d
cd route-service && ./mvnw spring-boot:run
```

## Teknolojiler

- Spring Boot 4 / Java 17
- PostgreSQL
- OpenFeign
- Docker