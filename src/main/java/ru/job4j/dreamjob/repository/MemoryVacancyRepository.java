package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.utils.HabrCareerDateTimeParser;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        HabrCareerDateTimeParser h = new HabrCareerDateTimeParser();
        save(new Vacancy(
                1,
                "Intern Java Developer",
                "Стажер. Вакансия для людей без опыта",
                h.parse(LocalDateTime.now())
        ));
        save(new Vacancy(
                2,
                "Junior Java Developer",
                "Начинающий. Вакансия для людей с опытом полгода",
                LocalDateTime.now()
        ));
        save(new Vacancy(3,
                "Junior+ Java Developer",
                "Специалист. Вакансия для тех, кто закончил Job4j",
                h.parse(LocalDateTime.now())
        ));
        save(new Vacancy(
                4,
                "Middle Java Developer",
                "Профессионал. Вакансия для людей с опытом > 1 года",
                h.parse(LocalDateTime.now())
        ));
        save(new Vacancy(
                5,
                "Middle+ Java Developer",
                "Профессионал. Вакансия для людей с опытом > 2 лет",
                h.parse(LocalDateTime.now())
        ));
        save(new Vacancy(
                6,
                "Senior Java Developer",
                "Профессионал. Вакансия для людей с опытом > 3 лет",
                h.parse(LocalDateTime.now())
        ));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(
                vacancy.getId(), (id, oldVacancy) ->
                        new Vacancy(
                                oldVacancy.getId(),
                                vacancy.getTitle(),
                                vacancy.getDescription(),
                                vacancy.getCreationDate()
                        )) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}