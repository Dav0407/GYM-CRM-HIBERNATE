package com.epam.gym_crm.db_init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingTypeDataInitializer {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void insertDefaultTrainingTypes() {
        String sql = """
            INSERT INTO training_types (trainingTypeName) VALUES
            ('Cardio'),
            ('Strength Training'),
            ('Yoga'),
            ('Pilates'),
            ('CrossFit'),
            ('HIIT'),
            ('Cycling'),
            ('Zumba'),
            ('Boxing'),
            ('Swimming');
        """;

        jdbcTemplate.execute(sql);
        System.out.println("Default training types inserted successfully.");
    }
}
