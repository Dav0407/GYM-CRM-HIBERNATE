package com.epam.gym_crm.repository_test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.epam.gym_crm.repository_impl.TraineeTrainerRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.TraineeTrainer;
import com.epam.gym_crm.entity.Trainer;

@ExtendWith(MockitoExtension.class)
class TraineeTrainerRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TraineeTrainerRepositoryImpl traineeTrainerRepository;

    private TraineeTrainer traineeTrainer;
    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainer = new Trainer();
        traineeTrainer = new TraineeTrainer();
        traineeTrainer.setTrainee(trainee);
        traineeTrainer.setTrainer(trainer);
    }

    @Test
    void save_NewTraineeTrainer_PersistsEntity() {
        traineeTrainer.setId(null);

        TraineeTrainer result = traineeTrainerRepository.save(traineeTrainer);

        verify(entityManager).persist(traineeTrainer);
        assertThat(result).isEqualTo(traineeTrainer);
    }

    @Test
    void save_ExistingTraineeTrainer_MergesEntity() {
        traineeTrainer.setId(1L);

        when(entityManager.merge(traineeTrainer)).thenReturn(traineeTrainer);
        TraineeTrainer result = traineeTrainerRepository.save(traineeTrainer);

        verify(entityManager).merge(traineeTrainer);
        assertThat(result).isEqualTo(traineeTrainer);
    }

    @Test
    void save_PersistenceException_ThrowsRuntimeException() {
        traineeTrainer.setId(null);
        doThrow(new PersistenceException("DB error")).when(entityManager).persist(traineeTrainer);

        assertThrows(RuntimeException.class, () -> traineeTrainerRepository.save(traineeTrainer));
    }

    @Test
    void findAllByTraineeUsername_ReturnsList() {
        String username = "testUser";
        TypedQuery<TraineeTrainer> query = mock(TypedQuery.class);
        List<TraineeTrainer> expected = Collections.singletonList(traineeTrainer);

        when(entityManager.createQuery(anyString(), eq(TraineeTrainer.class))).thenReturn(query);
        when(query.setParameter("username", username)).thenReturn(query);
        when(query.getResultList()).thenReturn(expected);

        List<TraineeTrainer> result = traineeTrainerRepository.findAllByTraineeUsername(username);

        assertThat(result).isEqualTo(expected);
        verify(query).setParameter("username", username);
    }

    @Test
    void findByTraineeAndTrainer_Found_ReturnsOptional() {
        TypedQuery<TraineeTrainer> query = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(TraineeTrainer.class))).thenReturn(query);
        when(query.setParameter("trainee", trainee)).thenReturn(query);
        when(query.setParameter("trainer", trainer)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(traineeTrainer);

        Optional<TraineeTrainer> result = traineeTrainerRepository.findByTraineeAndTrainer(trainee, trainer);

        assertThat(result).contains(traineeTrainer);
    }

    @Test
    void findByTraineeAndTrainer_NotFound_ReturnsEmpty() {
        TypedQuery<TraineeTrainer> query = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(TraineeTrainer.class))).thenReturn(query);
        when(query.setParameter("trainee", trainee)).thenReturn(query);
        when(query.setParameter("trainer", trainer)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        Optional<TraineeTrainer> result = traineeTrainerRepository.findByTraineeAndTrainer(trainee, trainer);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteAll_WithValidList_RemovesEntities() {
        List<TraineeTrainer> relations = Collections.singletonList(traineeTrainer);
        when(entityManager.contains(traineeTrainer)).thenReturn(true);

        traineeTrainerRepository.deleteAll(relations);

        verify(entityManager).remove(traineeTrainer);
    }

    @Test
    void deleteAll_EmptyList_NoAction() {
        traineeTrainerRepository.deleteAll(Collections.emptyList());
        verify(entityManager, never()).remove(any());
    }

    @Test
    void saveAll_NewEntities_PersistsAll() {
        List<TraineeTrainer> relations = Collections.singletonList(traineeTrainer);
        traineeTrainer.setId(null);

        traineeTrainerRepository.saveAll(relations);

        verify(entityManager).persist(traineeTrainer);
    }

    @Test
    void saveAll_ExistingEntities_MergesAll() {
        traineeTrainer.setId(1L);
        List<TraineeTrainer> relations = Collections.singletonList(traineeTrainer);

        traineeTrainerRepository.saveAll(relations);

        verify(entityManager).merge(traineeTrainer);
    }
}