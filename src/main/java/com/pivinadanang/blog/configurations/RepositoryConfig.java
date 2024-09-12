package com.pivinadanang.blog.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.pivinadanang.blog.repositories")
public class RepositoryConfig {

}
