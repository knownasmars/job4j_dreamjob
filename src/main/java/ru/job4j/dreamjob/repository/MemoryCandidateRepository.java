package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@ThreadSafe
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final ConcurrentHashMap<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    public MemoryCandidateRepository() {
        save(Candidate.of(0, "Валерия", "Стажер Java разработчик", LocalDateTime.now()));
        save(Candidate.of(0, "Светлана", "Младший Java разработчик", LocalDateTime.now()));
        save(Candidate.of(0, "Наталья", "Java разработчик", LocalDateTime.now()));
        save(Candidate.of(0, "Марс", "Старший Java разработчик", LocalDateTime.now()));
        save(Candidate.of(0, "Андрей", "Ведущий Java разработчик", LocalDateTime.now()));
        save(Candidate.of(0, "Стас", "Главный Java разработчик", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(),
                Candidate.of(
                        candidate.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate()
                )
        );
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(
                candidate.getId(), (id, oldCandidate) ->
                        Candidate.of(
                                oldCandidate.getId(),
                                candidate.getName(),
                                candidate.getDescription(),
                                candidate.getCreationDate()
                        )) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values()
                .stream()
                .map(x -> Candidate.of(
                        x.getId(),
                        x.getName(),
                        x.getDescription(),
                        x.getCreationDate()))
                .collect(Collectors.toList());
    }
}
