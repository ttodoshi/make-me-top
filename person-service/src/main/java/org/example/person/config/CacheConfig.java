package org.example.person.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "personByIdCache",
                "personExistsByIdCache",
                "peopleByPersonIdInCache",
                "keeperByIdCache",
                "keeperExistsByIdCache",
                "keeperByPersonIdAndCourseIdCache",
                "keepersByPersonIdCache",
                "keepersByCourseIdCache",
                "keepersByKeeperIdInCache",
                "keepersByPersonIdAndCourseIdInCache",
                "allKeepersCache",
                "explorerByIdCache",
                "explorerExistsByIdCache",
                "explorersByPersonIdAndCourseIdCache",
                "explorersByPersonIdCache",
                "explorersByCourseIdCache",
                "explorersByExplorerIdInCache",
                "explorersByGroup_CourseIdInCache",
                "explorersByPersonIdAndGroup_CourseIdInCache",
                "allExplorersCache",
                "explorerGroupByIdCache",
                "explorerGroupsByKeeperIdCache",
                "explorerGroupsByKeeperIdInCache",
                "explorerGroupsByGroupIdInCache"
        );
    }
}
