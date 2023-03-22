package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;

import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    private HttpServletRequest request;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        request = new MockHttpServletRequest();
    }

    @Test
    public void whenGetRegistationPage() {
        assertThat(userController.getRegistationPage()).isEqualTo("users/register");
    }

    @Test
    public void whenTryToRegisterThenIndexPage() {
        var user = mock(User.class);
        when(userService.save(user)).thenReturn(Optional.of(user));

        ConcurrentModel model = new ConcurrentModel();
        var view = userController.register(model, user);
        assertThat(view).isEqualTo("redirect:/index");
    }

    @Test
    public void whenTryRegisterWithExistingDataThenErrorPage() {
        var user = mock(User.class);
        when(userService.save(user)).thenReturn(Optional.empty());

        ConcurrentModel model = new ConcurrentModel();
        var view = userController.register(model, user);
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenGetLoginPage() {
        assertThat(userController.getLoginPage()).isEqualTo("users/login");
    }

    @Test
    public void whenPostTryToLogin() {
        var user = mock(User.class);

        when(userService.findByEmailAndPassword(any(), any()))
                .thenReturn(Optional.of(user));

        ConcurrentModel model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/vacancies");

    }

    @Test
    public void logout() {
        assertThat(userController.logout(request.getSession())).isEqualTo("redirect:/users/login");
    }
}