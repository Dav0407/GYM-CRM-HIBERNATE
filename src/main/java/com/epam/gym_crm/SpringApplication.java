package com.epam.gym_crm;

import com.epam.gym_crm.config.ApplicationConfig;
import com.epam.gym_crm.dto.AddTrainingRequestDTO;
import com.epam.gym_crm.dto.CreateTraineeProfileRequestDTO;
import com.epam.gym_crm.dto.CreateTrainerProfileRequestDTO;
import com.epam.gym_crm.dto.GetTraineeTrainingsRequestDTO;
import com.epam.gym_crm.dto.GetTrainerTrainingsRequestDTO;
import com.epam.gym_crm.dto.UpdateTraineeProfileRequestDTO;
import com.epam.gym_crm.dto.UpdateTrainerProfileRequestDTO;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.Training;
import com.epam.gym_crm.service.TraineeService;
import com.epam.gym_crm.service.TraineeTrainerService;
import com.epam.gym_crm.service.TrainerService;
import com.epam.gym_crm.service.TrainingService;
import com.epam.gym_crm.service.TrainingTypeService;
import com.epam.gym_crm.service.UserService;
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
import java.util.stream.Collectors;

public class SpringApplication {

    private static final Log LOG = LogFactory.getLog(SpringApplication.class);
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        LOG.info("Welcome to the GYM Management System!");

        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        UserService userService = context.getBean(UserService.class);
        TraineeService traineeService = context.getBean(TraineeService.class);
        TrainerService trainerService = context.getBean(TrainerService.class);
        TraineeTrainerService traineeTrainerService = context.getBean(TraineeTrainerService.class);
        TrainingService trainingService = context.getBean(TrainingService.class);
        TrainingTypeService trainingTypeService = context.getBean(TrainingTypeService.class);

        boolean running = true;
        while (running) {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Create Trainee");
            System.out.println("2. Create Trainer");
            System.out.println("3. Log in as Trainee");
            System.out.println("4. Log in as Trainer");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1 -> createTrainee(traineeService);
                case 2 -> createTrainer(trainerService);
                case 3 -> handleTraineeLogin(userService, traineeService, trainingService, traineeTrainerService);
                case 4 -> handleTrainerLogin(userService, trainerService, trainingService);
                case 10 -> {
                    running = false;
                    System.out.println("Exiting the GYM Management System. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(SCANNER.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

    private static void handleTrainerLogin(UserService userService, TrainerService trainerService, TrainingService trainingService) {
        System.out.println("Enter your username: ");
        String username = SCANNER.nextLine();

        Trainer trainer = trainerService.getTrainerByUsername(username);
        if (trainer != null) {
            System.out.println("Enter your password: ");
            String password = SCANNER.nextLine();
            if (userService.isPasswordValid(username, password)) {
                trainerMenu(userService, trainerService, trainer, trainingService);
            } else {
                System.out.println("Invalid username or password.");
            }
        } else {
            System.out.println("Trainer not found.");
        }
    }

    private static void handleTraineeLogin(UserService userService, TraineeService traineeService, TrainingService trainingService, TraineeTrainerService traineeTrainerService) {
        System.out.println("Enter your username: ");
        String username = SCANNER.nextLine();

        Trainee trainee = traineeService.getTraineeByUsername(username);
        if (trainee != null) {
            System.out.println("Enter your password: ");
            String password = SCANNER.nextLine();
            if (userService.isPasswordValid(username, password)) {
                traineeMenu(userService, traineeService, trainee, trainingService, traineeTrainerService);
            } else {
                System.out.println("Invalid username or password.");
            }
        } else {
            System.out.println("Trainee not found.");
        }
    }

    private static void trainerMenu(UserService userService, TrainerService trainerService, Trainer trainer, TrainingService trainingService) {
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
                case 1 -> getTrainerByUsername(trainerService);
                case 2 -> changeTrainerPassword(trainer, userService);
                case 3 -> updateTrainerProfile(trainer, trainerService);
                case 4 -> switchTrainerStatus(trainer, trainerService);
                case 5 -> getUnassignedTrainers(trainer, trainerService);
                case 6 -> addTraining(trainer, trainingService);
                case 7 -> getTrainerTrainingList(trainer, trainingService);
                case 8 -> loggedIn = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void getTrainerTrainingList(Trainer trainer, TrainingService trainingService) {

        System.out.println("Enter trainee username: ");
        String username = SCANNER.nextLine();

        Date from = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        while (from == null) {
            System.out.print("Enter date from (yyyy-MM-dd): ");
            String dateInput = SCANNER.nextLine();
            try {
                from = dateFormat.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        Date to = null;

        while (to == null) {
            System.out.print("Enter date to (yyyy-MM-dd): ");
            String dateInput = SCANNER.nextLine();
            try {
                to = dateFormat.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        GetTrainerTrainingsRequestDTO request = GetTrainerTrainingsRequestDTO.builder()
                .TrainerUsername(trainer.getUser().getUsername())
                .TraineeUsername(username)
                .from(from)
                .to(to)
                .build();
        List<Training> trainerTrainings = trainingService.getTrainerTrainings(request);

        for (int i = 0; i < trainerTrainings.size(); i++) {
            System.out.println("Training " + i + ": " + trainerTrainings.get(i));
        }
    }

    private static void addTraining(Trainer trainer, TrainingService trainingService) {

        System.out.println("Enter your training name: ");
        String trainingName = SCANNER.nextLine();

        System.out.println("Enter trainee username: ");
        String traineeUsername = SCANNER.nextLine();

        System.out.println("Enter the training type:");
        System.out.println("[Cardio/Strength Training/Yoga/Pilates/CrossFit/HIIT/Cycling/Zumba/Boxing/Swimming]");
        String trainingType = SCANNER.nextLine();

        System.out.println("Enter training date in yyyy-MM-dd:");

        Date trainingDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        while (trainingDate == null) {
            System.out.print("Enter date of birth (yyyy-MM-dd): ");
            String dateInput = SCANNER.nextLine();
            try {
                trainingDate = dateFormat.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        System.out.println("Enter training duration (minutes):");
        String input = SCANNER.nextLine();
        int trainingDuration = Integer.parseInt(input);

        AddTrainingRequestDTO request = AddTrainingRequestDTO.builder()
                .trainingName(trainingName)
                .trainerUsername(trainer.getUser().getUsername())
                .traineeUsername(traineeUsername)
                .trainingTypeName(trainingType)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .build();

        trainingService.addTraining(request);
    }

    private static void getUnassignedTrainers(Trainer trainer, TrainerService trainerService) {
        List<Trainer> list = trainerService.getNotAssignedTrainersByTraineeUsername(trainer.getUser().getUsername());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Trainer " + i + ": " + list.get(i));
        }
        System.out.println();
    }

    private static void switchTrainerStatus(Trainer trainer, TrainerService trainerService) {
        trainerService.updateStatus(trainer.getUser().getUsername());
    }

    private static void updateTrainerProfile(Trainer trainer, TrainerService trainerService) {

        System.out.println("Enter new first name: ");
        String firstName = SCANNER.nextLine();

        System.out.println("Enter new last name: ");
        String lastName = SCANNER.nextLine();

        System.out.println("Enter new username: ");
        String username = SCANNER.nextLine();

        System.out.print("Enter new specialization ");
        System.out.println("[Cardio/Strength Training/Yoga/Pilates/CrossFit/HIIT/Cycling/Zumba/Boxing/Swimming]: ");
        String specialization = SCANNER.nextLine();

        System.out.println();

        UpdateTrainerProfileRequestDTO request = UpdateTrainerProfileRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .trainingTypeName(specialization)
                .build();
        Trainer updateTrainerProfile = trainerService.updateTrainerProfile(trainer.getId(), request);

        if (updateTrainerProfile != null) {
            LOG.info("Trainer updated successfully: " + updateTrainerProfile);
        } else {
            LOG.error("Trainer update failed");
        }
    }

    private static void changeTrainerPassword(Trainer trainer, UserService userService) {
        System.out.println("Enter your old password: ");
        String oldPassword = SCANNER.nextLine();
        if (userService.isPasswordValid(trainer.getUser().getUsername(), oldPassword)) {
            System.out.println("Enter your new password: ");
            String newPassword = SCANNER.nextLine();
            userService.changePassword(trainer.getUser().getUsername(), oldPassword, newPassword);
        }
    }

    private static void getTrainerByUsername(TrainerService trainerService) {
        System.out.println("Username:");
        String username = SCANNER.nextLine();
        Trainer trainer = trainerService.getTrainerByUsername(username);
        if (trainer != null) {
            LOG.info(trainer.toString());
        } else LOG.error("Trainer not found.");
    }

    private static void traineeMenu(UserService userService, TraineeService traineeService, Trainee trainee, TrainingService trainingService, TraineeTrainerService traineeTrainerService) {
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
                case 1 -> getTraineeByUsername(traineeService);
                case 2 -> changeTraineePassword(trainee, userService);
                case 3 -> updateTraineeProfile(trainee, traineeService);
                case 4 -> switchTraineeStatus(trainee, traineeService);
                case 5 -> deleteTrainee(traineeService);
                case 6 -> getTraineeTrainingList(trainee, trainingService);
                case 7 -> updateTrainersList(trainee, traineeTrainerService);
                case 8 -> loggedIn = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void updateTrainersList(Trainee trainee, TraineeTrainerService traineeTrainerService) {
        System.out.println("Enter your trainer usernames list as this format {username1, username2, username3, username4}: ");
        String input = SCANNER.nextLine();

        // Extract usernames from input
        List<String> usernames = Arrays.stream(input.replaceAll("[{}]", "").split(","))
                .map(String::trim)
                .filter(username -> !username.isEmpty())
                .toList();

        traineeTrainerService.updateTraineeTrainers(trainee.getUser().getUsername(), usernames);
    }


    private static void getTraineeTrainingList(Trainee trainee, TrainingService trainingService) {

        System.out.println("Enter trainer username: ");
        String trainerUsername = SCANNER.nextLine();

        Date from = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        while (from == null) {
            System.out.print("Enter date from (yyyy-MM-dd): ");
            String dateInput = SCANNER.nextLine();
            try {
                from = dateFormat.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        Date to = null;

        while (to == null) {
            System.out.print("Enter date to (yyyy-MM-dd): ");
            String dateInput = SCANNER.nextLine();
            try {
                to = dateFormat.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        System.out.print("Enter training type: ");
        System.out.println("[Cardio/Strength Training/Yoga/Pilates/CrossFit/HIIT/Cycling/Zumba/Boxing/Swimming]: ");
        String type = SCANNER.nextLine();

        GetTraineeTrainingsRequestDTO request = GetTraineeTrainingsRequestDTO.builder()
                .traineeUsername(trainee.getUser().getUsername())
                .trainerUsername(trainerUsername)
                .from(from)
                .to(to)
                .trainingType(type)
                .build();
        List<Training> traineeTrainings = trainingService.getTraineeTrainings(request);

        for (int i = 0; i < traineeTrainings.size(); i++) {
            System.out.println("Training " + i + ": " + traineeTrainings.get(i));
        }
    }

    private static void deleteTrainee(TraineeService traineeService) {
        System.out.println("Enter trainee username: ");
        String username = SCANNER.nextLine();

        traineeService.deleteTraineeProfileByUsername(username);
    }

    private static void switchTraineeStatus(Trainee trainee, TraineeService traineeService) {
        traineeService.updateStatus(trainee.getUser().getUsername());
    }

    private static void updateTraineeProfile(Trainee trainee, TraineeService traineeService) {

        System.out.println("Enter your new first name: ");
        String firstName = SCANNER.nextLine();

        System.out.println("Enter your new last name: ");
        String lastName = SCANNER.nextLine();

        System.out.println("Enter your new username: ");
        String username = SCANNER.nextLine();

        Date dateOfBirth = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        while (dateOfBirth == null) {
            System.out.print("Enter date of birth (yyyy-MM-dd): ");
            String dateInput = SCANNER.nextLine();
            try {
                dateOfBirth = dateFormat.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        System.out.println("Enter your new address: ");
        String address = SCANNER.nextLine();

        UpdateTraineeProfileRequestDTO request = UpdateTraineeProfileRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .dateOfBirth(dateOfBirth)
                .address(address)
                .build();

        traineeService.updateTraineeProfile(trainee.getId(), request);
    }

    private static void changeTraineePassword(Trainee trainee, UserService userService) {
        System.out.println("Enter your old password: ");
        String oldPassword = SCANNER.nextLine();
        if (userService.isPasswordValid(trainee.getUser().getUsername(), oldPassword)) {
            System.out.println("Enter your new password: ");
            String newPassword = SCANNER.nextLine();
            userService.changePassword(trainee.getUser().getUsername(), oldPassword, newPassword);
        }
    }

    private static void getTraineeByUsername(TraineeService traineeService) {
        System.out.println("Username:");
        String username = SCANNER.nextLine();
        Trainee trainee1 = traineeService.getTraineeByUsername(username);
        if (trainee1 != null) {
            LOG.info(trainee1.toString());
        } else LOG.error("Trainer not found.");
    }

    private static void createTrainer(TrainerService service) {
        System.out.print("Enter first name: ");
        String firstName = SCANNER.nextLine();
        System.out.print("Enter last name: ");
        String lastName = SCANNER.nextLine();
        System.out.print("Enter specialization ");
        System.out.println("[Cardio/Strength Training/Yoga/Pilates/CrossFit/HIIT/Cycling/Zumba/Boxing/Swimming]: ");
        String specialization = SCANNER.nextLine();

        CreateTrainerProfileRequestDTO request = CreateTrainerProfileRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .trainingType(specialization)
                .build();

        Trainer trainer = service.createTrainerProfile(request);
        System.out.println("Trainer created: " + trainer);
    }

    private static void createTrainee(TraineeService service) {
        System.out.print("Enter first name: ");
        String firstName = SCANNER.nextLine();
        System.out.print("Enter last name: ");
        String lastName = SCANNER.nextLine();
        System.out.print("Enter address: ");
        String address = SCANNER.nextLine();

        Date dateOfBirth = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        while (dateOfBirth == null) {
            System.out.print("Enter date of birth (yyyy-MM-dd): ");
            String dateInput = SCANNER.nextLine();
            try {
                dateOfBirth = dateFormat.parse(dateInput);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        CreateTraineeProfileRequestDTO request = CreateTraineeProfileRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .dateOfBirth(dateOfBirth)
                .build();

        Trainee trainee = service.createTraineeProfile(request);
        System.out.println("Trainee created: " + trainee);
    }
}
