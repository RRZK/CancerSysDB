

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
            url = "jdbc:mysql://CancersysMariaDB:3306/cancersys"
            dialect = org.hibernate.dialect.MySQL5Dialect
            username = "csysUser"
            password = "csysPassword"
//            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
//            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }

    }

}




environments {
    development {
        cancersys.config.BasePath = "/srv/cancersys/"
        cancersys.config.tempFilepath = "/srv/cancersys/ImportedFiles/"
        cancersys.config.dataFilepath = "/srv/cancersys/Data/"

    }
    test{
        cancersys.config.BasePath = "/srv/cancersys/"
        cancersys.config.tempFilepath = "/srv/cancersys/ImportedFiles/"
        cancersys.config.dataFilepath = "/srv/cancersys/Data/"
    }
    production {
        cancersys.config.BasePath = "/srv/cancersys/"
        cancersys.config.tempFilepath = "/srv/cancersys/ImportedFiles/"
        cancersys.config.dataFilepath = "/srv/cancersys/Data/"
    }
}


//By default start in private mode. For Public mode change in external config file

cancersys.config.systemType = "private"