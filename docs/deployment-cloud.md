# Despliegue Cloud

## Recomendacion inicial

Para una primera demo cloud estable y barata:

- Backend: Render
- Base de datos: Neon PostgreSQL
- Kafka: desactivado en esta primera version cloud

La aplicacion mantiene el flujo interno de riesgo y fraude aunque `APP_KAFKA_ENABLED=false`.

## Variables necesarias

Configura estas variables en Render:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SECURITY_JWT_SECRET`
- `APP_KAFKA_ENABLED=false`

Render ya puede leer `render.yaml` desde la raiz del repo.

## Neon

1. Crea un proyecto en Neon.
2. Copia una cadena de conexion PostgreSQL con `sslmode=require`.
3. Usa esa URL en `SPRING_DATASOURCE_URL`.

Ejemplo orientativo:

```text
jdbc:postgresql://ep-xxxx-pooler.region.aws.neon.tech/neondb?sslmode=require
```

Si Neon te entrega una URL `postgresql://...`, conviertela al formato JDBC:

```text
jdbc:postgresql://HOST/DB?sslmode=require
```

## Render

1. Sube el repo a GitHub.
2. En Render crea un `Blueprint` o un `Web Service`.
3. Si usas Blueprint, apunta a `render.yaml`.
4. Si lo haces manual, usa Docker con:
   - Dockerfile path: `backend/Dockerfile`
   - Docker context: `backend`
   - Health check: `/actuator/health`
5. Carga las variables de entorno.
6. Despliega.

## Siguiente paso

Cuando quieras mostrar arquitectura event-driven en cloud:

- Opcion A: migrar a un proveedor Kafka gestionado
- Opcion B: mantener Render + Neon para demo principal y dejar Kafka solo para local/dev

Para portfolio, la opcion B suele ser la mas rentable al principio.
