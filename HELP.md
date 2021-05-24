在线创建方式

网址：https://start.spring.io/
图片
然后创建Controller、Mapper、Service包

图片
SpringBoot整合Redis

引入Redis依赖

<!--SpringBoot与Redis整合依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
完整pom.xml

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.3.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.cyb</groupId>
    <artifactId>chenyb-mobile-redis</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>chenyb-mobile-redis</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--SpringBoot与Redis整合依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
设置Redis的Template

图片
RedisConfig.java

package com.cyb.mobile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @ClassName：RedisConfig
 * @Description：Redis配置类
 * @Author：chenyb
 * @Date：2020/8/16 11:48 下午
 * @Versiion：1.0
 */
@Configuration //当前类为配置类
public class RedisConfig {
    @Bean //redisTemplate注入到Spring容器
    public RedisTemplate<String,String> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,String> redisTemplate=new RedisTemplate<>();
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        redisTemplate.setConnectionFactory(factory);
        //key序列化
        redisTemplate.setKeySerializer(redisSerializer);
        //value序列化
        redisTemplate.setValueSerializer(redisSerializer);
        //value hashmap序列化
        redisTemplate.setHashKeySerializer(redisSerializer);
        //key hashmap序列化
        redisTemplate.setHashValueSerializer(redisSerializer);
        return redisTemplate;
    }
}
设置Redis连接信息

图片
# 连接的那个数据库
spring.redis.database=0
# redis服务的ip地址
spring.redis.host=192.168.199.142
# redis端口号
spring.redis.port=6379
# redis的密码，没设置过密码，可为空
spring.redis.password=12345678
Redis工具类

redisTemplate API

opsForValue ==》String
opsForSet ==》Set
opsForHash ==》hash
opsForZset ==》SortSet
opsForList ==》list队列
RedisUtils.java

图片
package com.cyb.mobile.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName：RedisUtils
 * @Description：Redis工具类
 * @Author：chenyb
 * @Date：2020/8/17 12:05 上午
 * @Versiion：1.0
 */
@Service
public class RedisUtils {
    @Autowired
    private RedisTemplate redisTemplate;

    private static double size = Math.pow(2, 32);


    /**
     * 写入缓存
     *
     * @param key
     * @param offset   位 8Bit=1Byte
     * @return
     */
    public boolean setBit(String key, long offset, boolean isShow) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.setBit(key, offset, isShow);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param offset
     * @return
     */
    public boolean getBit(String key, long offset) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            result = operations.getBit(key, offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }


    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 列表添加
     *
     * @param k
     * @param v
     */
    public void lPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k, v);
    }

    /**
     * 列表获取
     *
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, l, l1);
    }

    /**
     * 集合添加
     *
     * @param key
     * @param value
     */
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 集合获取
     *
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }

    /**
     * 有序集合获取
     *
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        redisTemplate.opsForValue();
        return zset.rangeByScore(key, scoure, scoure1);
    }


    //第一次加载的时候将数据加载到redis中
    public void saveDataToRedis(String name) {
        double index = Math.abs(name.hashCode() % size);
        long indexLong = new Double(index).longValue();
        boolean availableUsers = setBit("availableUsers", indexLong, true);
    }

    //第一次加载的时候将数据加载到redis中
    public boolean getDataToRedis(String name) {

        double index = Math.abs(name.hashCode() % size);
        long indexLong = new Double(index).longValue();
        return getBit("availableUsers", indexLong);
    }

    /**
     * 有序集合获取排名
     *
     * @param key 集合名称
     * @param value 值
     */
    public Long zRank(String key, Object value) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rank(key,value);
    }


    /**
     * 有序集合获取排名
     *
     * @param key
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRankWithScore(String key, long start,long end) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> ret = zset.rangeWithScores(key,start,end);
        return ret;
    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     */
    public Double zSetScore(String key, Object value) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.score(key,value);
    }


    /**
     * 有序集合添加分数
     *
     * @param key
     * @param value
     * @param scoure
     */
    public void incrementScore(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.incrementScore(key, value, scoure);
    }


    /**
     * 有序集合获取排名
     *
     * @param key
     */
    public Set<ZSetOperations.TypedTuple<Object>> reverseZRankWithScore(String key, long start,long end) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> ret = zset.reverseRangeByScoreWithScores(key,start,end);
        return ret;
    }

    /**
     * 有序集合获取排名
     *
     * @param key
     */
    public Set<ZSetOperations.TypedTuple<Object>> reverseZRankWithRank(String key, long start, long end) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> ret = zset.reverseRangeWithScores(key, start, end);
        return ret;
    }
}
控制层

图片
RedisController.java

package com.cyb.mobile.controller;

import com.cyb.mobile.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName：TestController
 * @Description：Redis控制器
 * @Author：chenyb
 * @Date：2020/8/17 12:07 上午
 * @Versiion：1.0
 */
@RestController
public class RedisController {
    @Autowired
    private RedisUtils redisUtils;
    @RequestMapping("setAndGet")
    public String test(String k,String v){
        redisUtils.set(k,v);
        return (String) redisUtils.get(k);
    }
}
测试

图片
SpringBoot整合Mybatis

添加依赖

<!--mybatis依赖-->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.3</version>
</dependency>
<!--mysql驱动-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<!--druid-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.23</version>
</dependency>
完整pom.xml

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.3.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.cyb</groupId>
    <artifactId>chenyb-mobile-redis</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>chenyb-mobile-redis</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--SpringBoot与Redis整合依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!--mybatis依赖-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.3</version>
        </dependency>
        <!--mysql驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <!--druid-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.23</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
设置配置文件

application.properties
# 连接的那个数据库
spring.redis.database=0
# redis服务的ip地址
spring.redis.host=192.168.199.142
# redis端口号
spring.redis.port=6379
# redis的密码，没设置过密码，可为空
spring.redis.password=12345678
# 端口号
server.port=8081
# ========================数据库相关配置=====================
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/nba?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=root
# 使用阿里巴巴druid数据源，默认使用自带
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
#开启控制台打印sql
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
# mybatis 下划线转驼峰配置，两者都可以
# mybatis.configuration.mapUnderscoreToCamelCase=true
mybatis.configuration.map-underscore-to-camel-case=true
# 配置扫描
mybatis.mapper-locations=classpath:mapper/*.xml
# 实体类所在的包别名
mybatis.type-aliases-package=com.cyb.mobile.domain
启动类上添加扫描路径

图片
NbaPlayer.java（实体类）

图片
NbaPlayerMapper.xml

图片
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.cyb.mobile.mapper.NbaPlayerMapper">
    <select id="ListNbaPlayer" resultType="NbaPlayer">
        SELECT * FROM nba_player
    </select>
</mapper>
NbaPlayerMapper.java

图片
NbaPlayerService.java

图片
NbaPlayerServiceImpl.java

图片
控制器(Controller)

图片
测试

图片
redis作为mybatis缓存

用户第一次访问的时候获取数据库的值，再次访问时直接从缓存中获取数据
设置缓存过期时间
   练手项目，学习强化，点击这里
代码演示

添加FastJSON依赖
<!--fastjson依赖-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.73</version>
</dependency>
~
@RequestMapping("test")
public Object test(){
    //step1 先从redis中取
    String strJson=(String) redisUtils.get("nbaPlayerCache");
    if (strJson==null){
        System.out.println("从db取值");
        // step2如果拿不到则从DB取值
       List<NbaPlayer> listNbaPlayer=nbaPlayerService.ListNbaPlayer();
       // step3 DB非空情况刷新redis值
       if (listNbaPlayer!=null){
           redisUtils.set("nbaPlayerCache", JSON.toJSONString(listNbaPlayer));
           return listNbaPlayer;
       }
       return null;
    }else
    {
        System.out.println("从redis缓存取值");
        return JSONObject.parseArray(strJson,NbaPlayer.class);
    }
}
注意

项目8080是对外端口(向外部暴露的端口)，区别于内部进程号，查内部端口用ps -ef|grep port，查外部端口用lsof -i:port
图片
图片
压测工具

上面我们已经SpringBoot整合Redis和Mybatis，但是无法知道具体QPS多少，此时我们可以使用压测工具来测压，参考：
https://www.cnblogs.com/chenyanbin/p/13332068.html