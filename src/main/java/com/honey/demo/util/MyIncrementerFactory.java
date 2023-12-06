package com.honey.demo.util;

import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.SqlServerSequenceMaxValueIncrementer;

import javax.sql.DataSource;

public class MyIncrementerFactory implements DataFieldMaxValueIncrementerFactory {

    private final DataSource dataSource;

    public MyIncrementerFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataFieldMaxValueIncrementer getIncrementer(String databaseType, String incrementerName) {
        return new SqlServerSequenceMaxValueIncrementer(dataSource, incrementerName);
    }

    @Override
    public boolean isSupportedIncrementerType(String databaseType)       {
        return true;
    }

    @Override
    public String[] getSupportedIncrementerTypes() {
        return null; // method should not get called anyway
    }
}