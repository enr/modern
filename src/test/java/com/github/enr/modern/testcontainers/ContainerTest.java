package com.github.enr.modern.testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public class ContainerTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final PostgreSQLContainer<?> 
    DB = new PostgreSQLContainer<>("postgres:11.1")
            .withLogConsumer(new Slf4jLogConsumer(LOG).withMdc("key", "value"))
            
            // database
            .withDatabaseName("integration-tests-db").withUsername("sa").withPassword("sa");
//            .withEnv("PGDATA", "")
//            .withFileSystemBind("", "", BindMode.READ_WRITE);;

    @BeforeAll
    public static void beforeAll() {
        //DB.followOutput(new Slf4jLogConsumer(LOG));
        DB.start();
    }

    @AfterAll
    public static void afterAll() {
        DB.stop();
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    public void test() throws Exception {
        String jdbcUrl = DB.getJdbcUrl();
        String username = DB.getUsername();
        String password = DB.getPassword();
        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        ResultSet resultSet = conn.createStatement().executeQuery("SELECT 1");
        resultSet.next();
        int result = resultSet.getInt(1);

        assertThat(result).as("result").isEqualTo(1);
    }

}
