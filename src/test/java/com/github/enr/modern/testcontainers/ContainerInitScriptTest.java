package com.github.enr.modern.testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public class ContainerInitScriptTest extends AbstractContainerDatabaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @EnabledOnOs(OS.LINUX)
    @Test
    public void testExplicitInitScript() throws SQLException {
        try (@SuppressWarnings("rawtypes")
        PostgreSQLContainer postgres = new PostgreSQLContainer<>("postgres:11.1").withInitScript("init_postgresql.sql").withLogConsumer(new Slf4jLogConsumer(LOG))) {
            postgres.start();

            ResultSet resultSet = performQuery(postgres, "SELECT foo FROM bar");

            String firstColumnValue = resultSet.getString(1);
            assertThat(firstColumnValue).as("Value from init script").isEqualTo("hello world");
        }
    }
}
