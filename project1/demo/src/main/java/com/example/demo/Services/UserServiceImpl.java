package com.example.demo.Services;

import com.example.demo.Entities.User;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.OraclePagingQueryProvider;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl {

    private final DataSource dataSource;

    public UserServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();

        try (Connection _ = dataSource.getConnection()) {

            JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
            reader.setDataSource(dataSource);
            reader.setRowMapper(new BeanPropertyRowMapper<>(User.class));
            reader.setPageSize(25);

            OraclePagingQueryProvider queryProvider = new OraclePagingQueryProvider();
            queryProvider.setSelectClause("account_id, first_name, last_name");
            queryProvider.setFromClause("FROM user_info");

            Map<String, Order> sortKeys = new HashMap<>();
            sortKeys.put("account_id", Order.ASCENDING);
            queryProvider.setSortKeys(sortKeys);

            reader.setQueryProvider(queryProvider);
            reader.afterPropertiesSet();

            ExecutionContext executionContext = new ExecutionContext();
            reader.open(executionContext);
            User user;
            while(true) {
                user = reader.read();

                if(user == null)
                    break;

                users.add(user);
                System.out.println(user);
            }
            reader.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}