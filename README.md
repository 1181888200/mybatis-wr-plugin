# mybatis-wr-plugin
mybatis通过插件方式实现主从库读写分离

1. 首先我们在本地创建test_master主库  和 test_slave从数据库

    在各自里面创建数据表

        CREATE TABLE `test_user` (
        `id` bigint(20) NOT NULL AUTO_INCREMENT,
        `name` varchar(255) DEFAULT NULL,
        `sex` varchar(255) DEFAULT NULL,
        `job` varchar(255) DEFAULT NULL,
        PRIMARY KEY (`id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
    
2. 然后将项目导入到开发工具中，修改application.properties文件中的数据库对应的名称和密码

3. 启动项目，找到MybatisWrApplicationTests类进行对应的测试

4. 测试说明
    由于主从库需要配置数据库，这里就不涉及了，测试的时候可以先各自在test_user表中造一些数据，然后看 查询的时候是从从库里面获取数据，
    增删改是从主库里面修改数据，即可
