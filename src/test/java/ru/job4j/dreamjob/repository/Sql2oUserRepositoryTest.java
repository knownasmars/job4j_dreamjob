package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.sql.SQLException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearVacancies() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        String name = "Vasya";
        String email = "i.Abramov@mail.ru";
        String password = "555";
        var optionalUser =
                sql2oUserRepository.save(new User(5, name, email, password));
        var expected =
                sql2oUserRepository.findByEmail(optionalUser.get().getEmail());
        assertThat(optionalUser).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void whenUserAlreadyExistsThenException() {
        String name = "Vasya";
        String email = "i.Abramov@mail.ru";
        String password = "555";
        User uvasya = new User(5, name, email, password);
        sql2oUserRepository.save(uvasya);
        assertThatThrownBy(() -> sql2oUserRepository.save(uvasya))
                .isInstanceOf(Sql2oException.class);
    }
}