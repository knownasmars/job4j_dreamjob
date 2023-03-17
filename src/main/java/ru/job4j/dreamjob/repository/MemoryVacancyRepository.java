package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@ThreadSafe
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final ConcurrentHashMap<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    public MemoryVacancyRepository() {
        save(Vacancy.of(0, "Intern Java Developer", "Стажер Java разработчик", LocalDateTime.now()));
        save(Vacancy.of(0, "Junior Java Developer", "Младший Java разработчик", LocalDateTime.now()));
        save(Vacancy.of(0, "Junior+ Java Developer", "Java разработчик", LocalDateTime.now()));
        save(Vacancy.of(0, "Middle Java Developer", "Старший Java разработчик", LocalDateTime.now()));
        save(Vacancy.of(0, "Middle+ Java Developer", "Ведущий Java разработчик", LocalDateTime.now()));
        save(Vacancy.of(0, "Senior Java Developer", "Главный Java разработчик", LocalDateTime.now()));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.getAndIncrement());
        vacancies.put(vacancy.getId(),
                Vacancy.of(
                        vacancy.getId(),
                        vacancy.getTitle(),
                        vacancy.getDescription(),
                        vacancy.getCreationDate()
                ));
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
                        Vacancy.of(
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
        return vacancies.values()
                .stream()
                .map(x -> Vacancy.of(
                        x.getId(),
                        x.getTitle(),
                        x.getDescription(),
                        x.getCreationDate()))
                .collect(Collectors.toList());
    }
}