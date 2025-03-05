package com.epam.gym_crm.repository_test;

import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.repository_impl.TrainerRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TrainerRepositoryImpl trainerRepository;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setId(1L);
    }

    @Test
    void save_NewTrainer_PersistsEntity() {
        trainer.setId(null);

        Trainer result = trainerRepository.save(trainer);

        verify(entityManager).persist(trainer);
        assertThat(result).isEqualTo(trainer);
    }

    @Test
    void save_ExistingTrainer_MergesEntity() {
        when(entityManager.merge(trainer)).thenReturn(trainer);

        Trainer result = trainerRepository.save(trainer);

        verify(entityManager).merge(trainer);
        assertThat(result).isEqualTo(trainer);
    }

    @Test
    void save_PersistenceException_ThrowsRuntimeException() {
        trainer.setId(null);
        doThrow(new PersistenceException("DB error")).when(entityManager).persist(trainer);

        assertThrows(RuntimeException.class, () -> trainerRepository.save(trainer));
    }

    @Test
    void findById_TrainerExists_ReturnsOptional() {
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);

        Optional<Trainer> result = trainerRepository.findById(1L);

        assertThat(result).contains(trainer);
    }

    @Test
    void findById_TrainerNotExists_ReturnsEmpty() {
        when(entityManager.find(Trainer.class, 1L)).thenReturn(null);

        Optional<Trainer> result = trainerRepository.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ReturnsAllTrainers() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        List<Trainer> expected = Collections.singletonList(trainer);

        when(entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(expected);

        List<Trainer> result = trainerRepository.findAll();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findByUserId_TrainerExists_ReturnsOptional() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        Long userId = 123L;

        when(entityManager.createQuery("SELECT t FROM Trainer t WHERE t.user.id = :userId", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        Optional<Trainer> result = trainerRepository.findByUserId(userId);

        assertThat(result).contains(trainer);
        verify(query).setParameter("userId", userId);
    }

    @Test
    void findByUserId_TrainerNotExists_ReturnsEmpty() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        Long userId = 123L;

        when(entityManager.createQuery("SELECT t FROM Trainer t WHERE t.user.id = :userId", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        Optional<Trainer> result = trainerRepository.findByUserId(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_ReturnsList() {
        String username = "testUser";
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        List<Trainer> expected = Collections.singletonList(trainer);

        when(entityManager.createQuery(
                "SELECT t FROM Trainer t WHERE t NOT IN " +
                        "(SELECT tt.trainer FROM TraineeTrainer tt WHERE tt.trainee.user.username = :traineeUsername)",
                Trainer.class))
                .thenReturn(query);
        when(query.setParameter("traineeUsername", username)).thenReturn(query);
        when(query.getResultList()).thenReturn(expected);

        List<Trainer> result = trainerRepository.findUnassignedTrainersByTraineeUsername(username);

        assertThat(result).isEqualTo(expected);
        verify(query).setParameter("traineeUsername", username);
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_NoResults_ReturnsEmptyList() {
        String username = "testUser";
        TypedQuery<Trainer> query = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter("traineeUsername", username)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Trainer> result = trainerRepository.findUnassignedTrainersByTraineeUsername(username);

        assertThat(result).isEmpty();
    }
}