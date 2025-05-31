package dev.paulina.multithreadimageprocessing;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class HelloController {

    @FXML private ImageView logoImageView;
    @FXML private ImageView originalImageView;
    @FXML private ImageView processedImageView;
    @FXML private ComboBox<String> operationComboBox;

    @FXML private Button btnExecute;
    @FXML private Button btnLoadImage;
    @FXML private Button btnSaveImage;
    @FXML private Button btnRotateLeft;
    @FXML private Button btnRotateRight;
    @FXML private Button btnScale;

    private BufferedImage currentImage;
    private BufferedImage processedImage;
    private boolean operationPerformed = false;

    private final MultithreadProcessing threadProcessor = new MultithreadProcessing();

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/pwr-logo.png")));
            logoImageView.setImage(logo);
        } catch (Exception e) {
            System.out.println("Logo not loaded: " + e.getMessage());
        }

        operationComboBox.getItems().addAll(
                "Negatyw",
                "Progowanie",
                "Konturowanie"
        );
        operationComboBox.setValue(null);

        btnExecute.setDisable(true);
        btnSaveImage.setDisable(true);
        operationComboBox.setDisable(true);
        btnRotateLeft.setDisable(true);
        btnRotateRight.setDisable(true);
        btnScale.setDisable(true);

        originalImageView.setImage(null);
        processedImageView.setImage(null);

        operationPerformed = false;
    }

    @FXML
    private void onLoadImage() {
        currentImage = null;
        processedImage = null;
        originalImageView.setImage(null);
        processedImageView.setImage(null);
        operationPerformed = false;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wczytaj obraz JPG");
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki JPG", "*.jpg"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String name = file.getName().toLowerCase();
            if (!name.endsWith(".jpg")) {
                showWarning("Niedozwolony format pliku!");
                return;
            }

            try {
                BufferedImage img = ImageIO.read(file);
                if (img == null) {
                    showWarning("Nie udało się wczytać obrazu!");
                    return;
                }
                currentImage = img;
                processedImage = null;
                Image fxImage = javafx.embed.swing.SwingFXUtils.toFXImage(img, null);
                originalImageView.setImage(fxImage);
                processedImageView.setImage(null);

                btnExecute.setDisable(false);
                btnSaveImage.setDisable(false);
                operationComboBox.setDisable(false);
                btnRotateLeft.setDisable(false);
                btnRotateRight.setDisable(false);
                btnScale.setDisable(false);

                showToast("Pomyślnie załadowano plik");
            } catch (IOException e) {
                showWarning("Błąd podczas wczytywania obrazu!");
            }
        }
    }

    @FXML
    private void onExecute() {
        String selected = operationComboBox.getValue();
        if (selected == null) {
            showWarning("Nie wybrano operacji do wykonania");
            return;
        }

        if (currentImage == null) {
            showWarning("Nie wczytano obrazu!");
            return;
        }

        try {
            switch (selected) {
                case "Negatyw":
                    processedImage = threadProcessor.processNegative(currentImage);
                    break;
                case "Progowanie":
                    Integer threshold = showThresholdDialog();
                    if (threshold == null) {
                        return;
                    }
                    processedImage = threadProcessor.processThreshold(currentImage, threshold);
                    break;
                case "Konturowanie":
                    processedImage = threadProcessor.processEdgeDetection(currentImage);
                    break;
                default:
                    showWarning("Nieznana operacja");
                    return;
            }
            Image fxProcessed = javafx.embed.swing.SwingFXUtils.toFXImage(processedImage, null);
            processedImageView.setImage(fxProcessed);
            operationPerformed = true;
            showToast(selected + " zostało przeprowadzone pomyślnie!");
        } catch (ExecutionException | InterruptedException e) {
            showWarning("Nie udało się wykonać " + selected.toLowerCase() + ".");
            AppLogger.logError("Błąd operacji " + selected, e);
        }
    }

    @FXML
    private void onSaveImage() {
        if (currentImage == null) {
            showWarning("Nie wczytano żadnego obrazu do zapisu!");
            return;
        }

        if (!operationPerformed) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ostrzeżenie");
            alert.setHeaderText(null);
            alert.setContentText("Na pliku nie zostały wykonane żadne operacje!");
            alert.showAndWait();
            AppLogger.logWarning("Na pliku nie zostały wykonane żadne operacje!");

            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Zapisz obraz");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        TextField filenameField = new TextField();
        filenameField.setPromptText("Nazwa pliku (3-100 znaków)");
        filenameField.setPrefWidth(300);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        VBox content = new VBox(10, filenameField, errorLabel);
        dialog.getDialogPane().setContent(content);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        filenameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().length() >= 3 && newVal.trim().length() <= 100) {
                saveButton.setDisable(false);
                errorLabel.setText("");
            } else {
                saveButton.setDisable(true);
                if (newVal.trim().length() > 0) {
                    errorLabel.setText("Wpisz co najmniej 3 znaki i maksymalnie 100 znaków");
                } else {
                    errorLabel.setText("");
                }
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return filenameField.getText().trim();
            }
            return null;
        });

        boolean validNameEntered = false;

        while (!validNameEntered) {
            var result = dialog.showAndWait();

            if (result.isEmpty()) {
                break;
            }

            String filename = result.get();
            if (filename == null) break;

            if (!filename.toLowerCase().endsWith(".jpg")) {
                filename += ".jpg";
            }

            try {
                Path picturesDir = Path.of(System.getProperty("user.home"), "Pictures");
                File file = picturesDir.resolve(filename).toFile();

                if (file.exists()) {
                    errorLabel.setText("Plik " + filename + " już istnieje. Podaj inną nazwę.");
                    continue;
                }

                Files.createDirectories(picturesDir);
                ImageIO.write(processedImage, "jpg", file);
                showToast("Zapisano obraz w pliku " + filename);
                validNameEntered = true;
            } catch (Exception e) {
                showWarning("Nie udało się zapisać pliku " + filename);
                AppLogger.logError("Błąd zapisu pliku " + filename, e);
                break;
            }
        }
    }

    @FXML
    private void onRotateLeft() {
        if (currentImage == null) {
            showWarning("Nie wczytano obrazu!");
            return;
        }
        processedImage = ImageProcessor.rotate90DegreesLeft(processedImage != null ? processedImage : currentImage);
        Image fxProcessed = javafx.embed.swing.SwingFXUtils.toFXImage(processedImage, null);
        processedImageView.setImage(fxProcessed);
        operationPerformed = true;
        showToast("Obrót o 90° w lewo wykonany");
    }

    @FXML
    private void onRotateRight() {
        if (currentImage == null) {
            showWarning("Nie wczytano obrazu!");
            return;
        }
        processedImage = ImageProcessor.rotate90DegreesRight(processedImage != null ? processedImage : currentImage);
        Image fxProcessed = javafx.embed.swing.SwingFXUtils.toFXImage(processedImage, null);
        processedImageView.setImage(fxProcessed);
        operationPerformed = true;
        showToast("Obrót o 90° w prawo wykonany");
    }

    @FXML
    private void onScale() {
        if (currentImage == null) {
            showWarning("Nie wczytano obrazu!");
            return;
        }
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Skalowanie obrazu");
        dialog.setHeaderText(null);

        ButtonType scaleButtonType = new ButtonType("Zmień rozmiar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(scaleButtonType, cancelButtonType);

        TextField widthField = new TextField();
        widthField.setPromptText("Szerokość (0-3000)");
        TextField heightField = new TextField();
        heightField.setPromptText("Wysokość (0-3000)");

        Button restoreButton = new Button("Przywróć oryginalne wymiary");

        Label widthError = new Label();
        widthError.setStyle("-fx-text-fill: red;");
        Label heightError = new Label();
        heightError.setStyle("-fx-text-fill: red;");

        restoreButton.setOnAction(e -> {
            widthField.setText(String.valueOf(currentImage.getWidth()));
            heightField.setText(String.valueOf(currentImage.getHeight()));
            widthError.setText("");
            heightError.setText("");
        });

        VBox content = new VBox(10, widthField, widthError, heightField, heightError, restoreButton);
        dialog.getDialogPane().setContent(content);

        Node scaleButton = dialog.getDialogPane().lookupButton(scaleButtonType);
        scaleButton.setDisable(true);

        widthField.textProperty().addListener((obs, oldVal, newVal) -> validateScaleFields(widthField, heightField, widthError, heightError, scaleButton));
        heightField.textProperty().addListener((obs, oldVal, newVal) -> validateScaleFields(widthField, heightField, widthError, heightError, scaleButton));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == scaleButtonType) {
                try {
                    int w = Integer.parseInt(widthField.getText().trim());
                    int h = Integer.parseInt(heightField.getText().trim());
                    return new int[] {w, h};
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        var result = dialog.showAndWait();
        if (result.isPresent()) {
            int[] size = result.get();
            if (size[0] > 0 && size[0] <= 3000 && size[1] > 0 && size[1] <= 3000) {
                processedImage = ImageProcessor.scale(processedImage != null ? processedImage : currentImage, size[0], size[1]);
                Image fxProcessed = javafx.embed.swing.SwingFXUtils.toFXImage(processedImage, null);
                processedImageView.setImage(fxProcessed);
                operationPerformed = true;
                showToast("Obraz przeskalowany do " + size[0] + "x" + size[1]);
            } else {
                showWarning("Wymiary muszą być w zakresie 1-3000");
            }
        }
    }

    private void validateScaleFields(TextField widthField, TextField heightField, Label widthError, Label heightError, Node scaleButton) {
        boolean valid = true;

        String wText = widthField.getText().trim();
        if (wText.isEmpty()) {
            widthError.setText("Pole jest wymagane");
            valid = false;
        } else {
            try {
                int w = Integer.parseInt(wText);
                if (w <= 0 || w > 3000) {
                    widthError.setText("Wartość musi być z zakresu 1-3000");
                    valid = false;
                } else {
                    widthError.setText("");
                }
            } catch (NumberFormatException e) {
                widthError.setText("Pole musi zawierać liczbę");
                valid = false;
            }
        }

        String hText = heightField.getText().trim();
        if (hText.isEmpty()) {
            heightError.setText("Pole jest wymagane");
            valid = false;
        } else {
            try {
                int h = Integer.parseInt(hText);
                if (h <= 0 || h > 3000) {
                    heightError.setText("Wartość musi być z zakresu 1-3000");
                    valid = false;
                } else {
                    heightError.setText("");
                }
            } catch (NumberFormatException e) {
                heightError.setText("Pole musi zawierać liczbę");
                valid = false;
            }
        }

        scaleButton.setDisable(!valid);
    }

    private Integer showThresholdDialog() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Progowanie obrazu");
        dialog.setHeaderText(null);

        ButtonType applyButtonType = new ButtonType("Wykonaj progowanie", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        TextField thresholdField = new TextField();
        thresholdField.setPromptText("Wartość progu (0-255)");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        VBox content = new VBox(10, thresholdField, errorLabel);
        dialog.getDialogPane().setContent(content);

        Node applyButton = dialog.getDialogPane().lookupButton(applyButtonType);
        applyButton.setDisable(true);

        thresholdField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean valid = false;
            if (newVal.trim().isEmpty()) {
                errorLabel.setText("Pole jest wymagane");
            } else {
                try {
                    int val = Integer.parseInt(newVal.trim());
                    if (val < 0 || val > 255) {
                        errorLabel.setText("Wartość musi być z zakresu 0-255");
                    } else {
                        errorLabel.setText("");
                        valid = true;
                    }
                } catch (NumberFormatException e) {
                    errorLabel.setText("Pole musi zawierać liczbę");
                }
            }
            applyButton.setDisable(!valid);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButtonType) {
                try {
                    return Integer.parseInt(thresholdField.getText().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        var result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showWarning(String msg) {
        AppLogger.logWarning(msg);
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setTitle("Ostrzeżenie");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showToast(String msg) {
        AppLogger.logInfo(msg);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setHeaderText(null);
        alert.initOwner(originalImageView.getScene().getWindow());
        alert.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> alert.close());
        delay.play();
    }

}
