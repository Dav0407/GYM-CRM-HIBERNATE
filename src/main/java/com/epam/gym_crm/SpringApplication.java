package com.epam.gym_crm;

import com.epam.gym_crm.config.ApplicationConfig;
import com.epam.gym_crm.dto.*;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.Training;
import com.epam.gym_crm.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class SpringApplication {

    private static final Log LOG = LogFactory.getLog(SpringApplication.class);
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Service beans
    private static UserService userService;
    private static TraineeService traineeService;
    private static TrainerService trainerService;
    private static TraineeTrainerService traineeTrainerService;
    private static TrainingService trainingService;
    private static TrainingTypeService trainingTypeService;

    public static void main(String[] args) {
        LOG.info("Welcome to the GYM Management System!");

        initializeServices();

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1 -> createTrainee();
                case 2 -> createTrainer();
                case 3 -> handleLogin(userService, "trainee");
                case 4 -> handleLogin(userService, "trainer");
                case 10 -> {
                    running = false;
                    System.out.println("Exiting the GYM Management System. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void initializeServices() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        userService = context.getBean(UserService.class);
        traineeService = context.getBean(TraineeService.class);
        trainerService = context.getBean(TrainerService.class);
        traineeTrainerService = context.getBean(TraineeTrainerService.class);
        trainingService = context.getBean(TrainingService.class);
        trainingTypeService = context.getBean(TrainingTypeService.class);
    }

    private static void displayMainMenu() {
        System.out.println("\nPlease select an option:");
        System.out.println("1. Create Trainee");
        System.out.println("2. Create Trainer");
        System.out.println("3. Log in as Trainee");
        System.out.println("4. Log in as Trainer");
        System.out.println("10. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(SCANNER.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

    private static void handleLogin(UserService userService, String userType) {
        System.out.println("Enter your username: ");
        String username = SCANNER.nextLine();

        Object user = null;
        if ("trainee".equals(userType)) {
            user = traineeService.getTraineeByUsername(username);
        } else if ("trainer".equals(userType)) {
            user = trainerService.getTrainerByUsername(username);
        }

        if (user != null) {
            System.out.println("Enter your password: ");
            String password = SCANNER.nextLine();
            if (userService.isPasswordValid(username, password)) {
                if ("trainee".equals(userType)) {
                    traineeMenu((Trainee) user);
                } else {
                    trainerMenu((Trainer) user);
                }
            } else {
                System.out.println("Invalid username or password.");
            }
        } else {
            System.out.println(userType.substring(0, 1).toUpperCase() + userType.substring(1) + " not found.");
        }
    }

    private static void trainerMenu(Trainer trainer) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\nTrainer Dashboard:");
            System.out.println(trainer.getUser().getUsername() + "\n");
            System.out.println("Hello " + trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName() + " !");
            System.out.println("1. Get trainer by username.");
            System.out.println("2. Change trainer password.");
            System.out.println("3. Update trainer profile.");
            System.out.println("4. Activate/De-activate trainer.");
            System.out.println("5. List unassigned trainers");
            System.out.println("6. Add training.");
            System.out.println("7. Get trainer training list.");
            System.out.println("8. Log out.");
            System.out.print("Enter your choice: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1 -> getByUsername("trainer");
                case 2 -> changePassword(trainer.getUser().getUsername());
                case 3 -> updateTrainerProfile(trainer);
                case 4 -> switchStatus("trainer", trainer.getUser().getUsername());
                case 5 -> getUnassignedTrainers(trainer);
                case 6 -> addTraining(trainer);
                case 7 -> getTrainerTrainingList(trainer);
                case 8 -> loggedIn = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void traineeMenu(Trainee trainee) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\nTrainee Dashboard:");
            System.out.println("1. Get trainee by username.");
            System.out.println("2. Change trainee password.");
            System.out.println("3. Update trainee profile.");
            System.out.println("4. Activate/De-activate trainee.");
            System.out.println("5. Delete trainee profile by username.");
            System.out.println("6. Get trainee training list.");
            System.out.println("7. Update trainers list");
            System.out.println("8. Log out");
            System.out.print("Enter your choice: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1 -> getByUsername("trainee");
                case 2 -> changePassword(trainee.getUser().getUsername());
                case 3 -> updateTraineeProfile(trainee);
                case 4 -> switchStatus("trainee", trainee.getUser().getUsername());
                case 5 -> deleteTrainee();
                case 6 -> getTraineeTrainingList(trainee);
                case 7 -> updateTrainersList(trainee);
                case 8 -> loggedIn = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void getByUsername(String userType) {
        System.out.println("Username:");
        String username = SCANNER.nextLine();

        if ("trainee".equals(userType)) {
            Trainee trainee = traineeService.getTraineeByUsername(username);
            if (trainee != null) {
                LOG.info(trainee.toString());
            } else {
                LOG.error("Trainee not found.");
            }
        } else if ("trainer".equals(userType)) {
            Trainer trainer = trainerService.getTrainerByUsername(username);
            if (trainer != null) {
                LOG.info(trainer.toString());
            } else {
                LOG.error("Trainer not found.");
            }
        }
    }

    private static void changePassword(String username) {
        System.out.println("Enter your old password: ");
        String oldPassword = SCANNER.nextLine();
        if (userService.isPasswordValid(username, oldPassword)) {
            System.out.println("Enter your new password: ");
            String newPassword = SCANNER.nextLine();
            userService.changePassword(username, oldPassword, newPassword);
        } else {
            System.out.println("Invalid password.");
        }
    }

    private static void switchStatus(String userType, String username) {
        if ("trainee".equals(userType)) {
            traineeService.updateStatus(username);
            System.out.println("Trainee status updated successfully.");
        } else if ("trainer".equals(userType)) {
            trainerService.updateStatus(username);
            System.out.println("Trainer status updated successfully.");
        }
    }

    private static Date readDate(String prompt) {
        Date date = null;
        DATE_FORMAT.setLenient(false);

        while (date == null) {
            System.out.print(prompt);
            String dateInput = SCANNER.nextLine();
            try {
                date = DATE_FORMAT.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        return date;
    }

    private static String readTrainingType() {
        System.out.print("Enter training type ");
        System.out.println("[Cardio/Strength Training/Yoga/Pilates/CrossFit/HIIT/Cycling/Zumba/Boxing/Swimming]: ");
        return SCANNER.nextLine();
    }

    private static void getTrainerTrainingList(Trainer trainer) {
        System.out.println("Enter trainee username: ");
        String username = SCANNER.nextLine();

        Date from = readDate("Enter date from (yyyy-MM-dd): ");
        Date to = readDate("Enter date to (yyyy-MM-dd): ");

        GetTrainerTrainingsRequestDTO request = GetTrainerTrainingsRequestDTO.builder()
                .TrainerUsername(trainer.getUser().getUsername())
                .TraineeUsername(username)
                .from(from)
                .to(to)
                .build();

        displayTrainingList(trainingService.getTrainerTrainings(request));
    }

    private static void getTraineeTrainingList(Trainee trainee) {
        System.out.println("Enter trainer username: ");
        String trainerUsername = SCANNER.nextLine();

        Date from = readDate("Enter date from (yyyy-MM-dd): ");
        Date to = readDate("Enter date to (yyyy-MM-dd): ");
        String type = readTrainingType();

        GetTraineeTrainingsRequestDTO request = GetTraineeTrainingsRequestDTO.builder()
                .traineeUsername(trainee.getUser().getUsername())
                .trainerUsername(trainerUsername)
                .from(from)
                .to(to)
                .trainingType(type)
                .build();

        displayTrainingList(trainingService.getTraineeTrainings(request));
    }

    private static void displayTrainingList(List<Training> trainings) {
        for (int i = 0; i < trainings.size(); i++) {
            System.out.println("Training " + i + ": " + trainings.get(i));
        }
        System.out.println();
    }

    private static void addTraining(Trainer trainer) {
        System.out.println("Enter your training name: ");
        String trainingName = SCANNER.nextLine();

        System.out.println("Enter trainee username: ");
        String traineeUsername = SCANNER.nextLine();

        String trainingType = readTrainingType();
        Date trainingDate = readDate("Enter training date (yyyy-MM-dd): ");

        System.out.println("Enter training duration (minutes):");
        int trainingDuration = Integer.parseInt(SCANNER.nextLine());

        AddTrainingRequestDTO request = AddTrainingRequestDTO.builder()
                .trainingName(trainingName)
                .trainerUsername(trainer.getUser().getUsername())
                .traineeUsername(traineeUsername)
                .trainingTypeName(trainingType)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .build();

        trainingService.addTraining(request);
        System.out.println("Training added successfully.");
    }

    private static void getUnassignedTrainers(Trainer trainer) {
        List<Trainer> list = trainerService.getNotAssignedTrainersByTraineeUsername(trainer.getUser().getUsername());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Trainer " + i + ": " + list.get(i));
        }
        System.out.println();
    }

    private static void updateTrainerProfile(Trainer trainer) {
        System.out.println("Enter new first name: ");
        String firstName = SCANNER.nextLine();

        System.out.println("Enter new last name: ");
        String lastName = SCANNER.nextLine();

        System.out.println("Enter new username: ");
        String username = SCANNER.nextLine();

        String specialization = readTrainingType();

        UpdateTrainerProfileRequestDTO request = UpdateTrainerProfileRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .trainingTypeName(specialization)
                .build();

        Trainer updatedTrainer = trainerService.updateTrainerProfile(trainer.getId(), request);

        if (updatedTrainer != null) {
            LOG.info("Trainer updated successfully: " + updatedTrainer);
        } else {
            LOG.error("Trainer update failed");
        }
    }

    private static void updateTraineeProfile(Trainee trainee) {
        System.out.println("Enter your new first name: ");
        String firstName = SCANNER.nextLine();

        System.out.println("Enter your new last name: ");
        String lastName = SCANNER.nextLine();

        System.out.println("Enter your new username: ");
        String username = SCANNER.nextLine();

        Date dateOfBirth = readDate("Enter date of birth (yyyy-MM-dd): ");

        System.out.println("Enter your new address: ");
        String address = SCANNER.nextLine();

        UpdateTraineeProfileRequestDTO request = UpdateTraineeProfileRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .dateOfBirth(dateOfBirth)
                .address(address)
                .build();

        Trainee updatedTrainee = traineeService.updateTraineeProfile(trainee.getId(), request);
        if (updatedTrainee != null) {
            LOG.info("Trainee updated successfully: " + updatedTrainee);
        } else {
            LOG.error("Trainee update failed");
        }
    }

    private static void updateTrainersList(Trainee trainee) {
        System.out.println("Enter your trainer usernames list as this format {username1, username2, username3, username4}: ");
        String input = SCANNER.nextLine();

        // Extract usernames from input
        List<String> usernames = Arrays.stream(input.replaceAll("[{}]", "").split(","))
                .map(String::trim)
                .filter(username -> !username.isEmpty())
                .toList();

        traineeTrainerService.updateTraineeTrainers(trainee.getUser().getUsername(), usernames);
        System.out.println("Trainers list updated successfully.");
    }

    private static void deleteTrainee() {
        System.out.println("Enter trainee username: ");
        String username = SCANNER.nextLine();
        traineeService.deleteTraineeProfileByUsername(username);
        System.out.println("Trainee deleted successfully.");
    }

    private static void createTrainer() {
        System.out.print("Enter first name: ");
        String firstName = SCANNER.nextLine();
        System.out.print("Enter last name: ");
        String lastName = SCANNER.nextLine();
        String specialization = readTrainingType();

        CreateTrainerProfileRequestDTO request = CreateTrainerProfileRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .trainingType(specialization)
                .build();

        Trainer trainer = trainerService.createTrainerProfile(request);
        System.out.println("Trainer created: " + trainer);
    }

    private static void createTrainee() {
        System.out.print("Enter first name: ");
        String firstName = SCANNER.nextLine();
        System.out.print("Enter last name: ");
        String lastName = SCANNER.nextLine();
        System.out.print("Enter address: ");
        String address = SCANNER.nextLine();

        Date dateOfBirth = readDate("Enter date of birth (yyyy-MM-dd): ");

        CreateTraineeProfileRequestDTO request = CreateTraineeProfileRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .dateOfBirth(dateOfBirth)
                .build();

        Trainee trainee = traineeService.createTraineeProfile(request);
        System.out.println("Trainee created: " + trainee);
    }
}