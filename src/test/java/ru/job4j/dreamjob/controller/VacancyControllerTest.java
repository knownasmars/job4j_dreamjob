package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class VacancyControllerTest {

    private VacancyService vacancyService;

    private CityService cityService;

    private VacancyController vacancyController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        vacancyService = mock(VacancyService.class);
        cityService = mock(CityService.class);
        vacancyController = new VacancyController(vacancyService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var vacancyOne = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancyTwo = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedVacancies = List.of(vacancyOne, vacancyTwo);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);

        var model = new ConcurrentModel();
        var view = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");

        assertThat(view).isEqualTo("vacancies/list");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city = new City(1, "Москва");
        var anotherCity = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city, anotherCity);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = vacancyController.getCreationPage(model);
        var actualVacancies = model.getAttribute("cities");

        assertThat(view).isEqualTo("vacancies/create");
        assertThat(actualVacancies).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(vacancy);

        var model = new ConcurrentModel();
        var view = vacancyController.create(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);

    }

    @Test
    public void whenPostVacancyAndUpdateThenRedirectToVacanciesPage() throws IOException {
        var vacancy = new Vacancy(1, "test", "test", now(), true, 1, 2);
        var updatedVacancy = mock(Vacancy.class);
        when(vacancyService.update(any(), any())).thenReturn(true);

        var model = new ConcurrentModel();
        vacancyController.create(vacancy, testFile, model);
        var view = vacancyController.update(updatedVacancy, testFile, model);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenPostVacancyAndUpdateUnsuccessfulThenGetError() throws IOException {
        var updatedVacancy = mock(Vacancy.class);
        when(vacancyService.update(any(), any())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = vacancyController.update(updatedVacancy, testFile, model);
        var actual = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actual).isEqualTo("Вакансия с указанным идентификатором не найдена");
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage2222() {
        var expectedException = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.create(new Vacancy(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenGetVacancyByIdThenSuccessfullyFound() {
        var vacancy = new Vacancy(1, "test", "test", now(), true, 1, 2);
        when(vacancyService.findById(any(Integer.class))).thenReturn(Optional.of(vacancy));

        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, vacancy.getId());
        var foundVacancy = model.getAttribute("vacancy");

        assertThat(view).isEqualTo("vacancies/one");
        assertThat(foundVacancy).isEqualTo(vacancy);
    }

    @Test
    public void whenGetVacancyByIdThenError() {
        var vacancy = new Vacancy(1, "test", "test", now(), true, 1, 2);
        when(vacancyService.findById(any(Integer.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, vacancy.getId());
        var message = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Вакансия с указанным идентификатором не найдена");
    }

    @Test
    public void whenDeleteVacancyThenNoVacancies() {
        int vacancyId = 1;
        when(vacancyService.deleteById(any(Integer.class))).thenReturn(true);

        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, vacancyId);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenDeleteVacancyByIdThatNotFoundThenError() {
        int vacancyId = 1;
        when(vacancyService.deleteById(any(Integer.class))).thenReturn(false);

        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, vacancyId);
        var message = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Вакансия с указанным идентификатором не найдена");
    }
}