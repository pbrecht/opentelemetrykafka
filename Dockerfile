FROM openjdk:8-jdk-alpine
COPY spring-petclinic-2.7.3.jar spring-petclinic-2.7.3.jar
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV otel.resource.attributes.service.name otel-collector
ENV otel.traces.exporter zipkin
ENV otel.exporter.zipkin.endpoint http://otel-collector:9411
ENV otel.exporter.otlp.endpoint http://otel-collector:4317
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar" ,"-jar", "/spring-petclinic-2.7.3.jar"]
