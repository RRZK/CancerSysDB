dataSource {
    pooled = true
    jmxExport = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""


}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    //flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
    development {

        dataSource {
            //logSql = true
            maxActive = 50
            maxIdle = 15
            minIdle = 2
            initialSize = 5
            minEvictableIdleTimeMillis = 300000
            timeBetweenEvictionRunsMillis = 300000
            maxWait = 10000
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            driverClassName = "com.mysql.jdbc.Driver"
            pooled = true
            url = "jdbc:mysql://localhost:3306/Cancersys"
            dialect = org.hibernate.dialect.MySQL5Dialect
            username = ""
            password = ""
/*
            logSql = true
*/


            //dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            //url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }

    }
    test {

        dataSource {


            //logSql = true
            maxActive = 50
            maxIdle = 15
            minIdle = 2
            initialSize = 5
            minEvictableIdleTimeMillis = 300000
            timeBetweenEvictionRunsMillis = 300000
            maxWait = 10000
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            driverClassName = "com.mysql.jdbc.Driver"
            pooled = true
            url = "jdbc:mysql://localhost:3306/CancersysTesting"
            dialect = org.hibernate.dialect.MySQL5Dialect
            username = ""
            password = ""
//            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
//            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }

    }
    production {

        dataSource {


            //logSql = true
            maxActive = 50
            maxIdle = 15
            minIdle = 2
            initialSize = 5
            minEvictableIdleTimeMillis = 300000
            timeBetweenEvictionRunsMillis = 300000
            maxWait = 10000
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            driverClassName = "com.mysql.jdbc.Driver"
            pooled = true
            url = "jdbc:mysql://localhost:3306/Cancersys"
            dialect = org.hibernate.dialect.MySQL5Dialect
            username = ""
            password = ""
//            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
//            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }

    }

}
