package com.epam.gym_crm.repository_test;

import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.repository_impl.TraineeRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TraineeRepositoryImpl traineeRepository;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
    }

    @Test
    void save_NewTrainee_PersistsAndReturnsTrainee() {
        trainee.setId(null);

        Trainee savedTrainee = traineeRepository.save(trainee);

        verify(entityManager).persist(trainee);
        assertThat(savedTrainee).isEqualTo(trainee);
    }

    @Test
    void save_ExistingManagedTrainee_MergesAndReturnsTrainee() {
        trainee.setId(1L);
        when(entityManager.contains(trainee)).thenReturn(true);
        when(entityManager.merge(trainee)).thenReturn(trainee);

        Trainee result = traineeRepository.save(trainee);

        verify(entityManager).merge(trainee);
        assertThat(result).isEqualTo(trainee);
    }

    @Test
    void save_ExistingUnmanagedTrainee_PersistsTrainee() {
        trainee.setId(2L);
        when(entityManager.contains(trainee)).thenReturn(false);

        Trainee result = traineeRepository.save(trainee);

        verify(entityManager).persist(trainee);
        assertThat(result).isEqualTo(trainee);
    }

    @Test
    void save_PersistenceException_ThrowsRuntimeException() {
        trainee.setId(null);
        doThrow(new PersistenceException("DB error")).when(entityManager).persist(trainee);

        assertThrows(RuntimeException.class, () -> traineeRepository.save(trainee));
        verify(entityManager).persist(trainee);
    }

    @Test
    void findById_TraineeExists_ReturnsOptionalTrainee() {
        Long id = 1L;
        when(entityManager.find(Trainee.class, id)).thenReturn(trainee);

        Optional<Trainee> result = traineeRepository.findById(id);

        assertThat(result).contains(trainee);
    }

    @Test
    void findById_TraineeNotExists_ReturnsEmptyOptional() {
        Long id = 1L;
        when(entityManager.find(Trainee.class, id)).thenReturn(null);

        Optional<Trainee> result = traineeRepository.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserId_TraineeExists_ReturnsOptionalTrainee() {
        Long userId = 123L;
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainee t WHERE t.user.id = :userId", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainee));

        Optional<Trainee> result = traineeRepository.findByUserId(userId);

        assertThat(result).contains(trainee);
        verify(query).setParameter("userId", userId);
        verify(query).getResultStream();
    }

    @Test
    void findByUserId_TraineeNotExists_ReturnsEmptyOptional() {
        Long userId = 123L;
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainee t WHERE t.user.id = :userId", Trainee.class))
                .thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        Optional<Trainee> result = traineeRepository.findByUserId(userId);

        assertThat(result).isEmpty();
        verify(query).setParameter("userId", userId);
        verify(query).getResultStream();
    }
}