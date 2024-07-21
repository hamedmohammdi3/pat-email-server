package com.fanap.fanrp.pat.patemailserver;

import com.fanap.fanrp.pat.patemailserver.config.PatMailServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        PatMailServerProperties.class
})
public class PatEmailServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatEmailServerApplication.class, args);
    }

}
