package io.github.adit12t.artifactorymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class ArtifactoryManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtifactoryManagerApplication.class, args);
    }
}
