package game.gui;

import gui.HomeController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HomeTest {

    private HomeController homeController;

    @BeforeAll
    static void initJFX() {
        // Initialize JavaFX toolkit for headless testing
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized
        }
    }

    @BeforeEach
    void setUp() {
        homeController = new HomeController();
    }

    @Test
    void testUIStructure() {
        // HomeController is a StackPane, its first child is an HBox
        assertFalse(homeController.getChildren().isEmpty(), "HomeController should have children");
        assertTrue(homeController.getChildren().get(0) instanceof HBox, "Root child should be an HBox");

        HBox mainBox = (HBox) homeController.getChildren().get(0);
        assertEquals(2, mainBox.getChildren().size(), "HBox should contain LeftBox and RightBox");
    }

    @Test
    void testButtonsExist() {
        HBox mainBox = (HBox) homeController.getChildren().get(0);
        VBox leftBox = (VBox) mainBox.getChildren().get(0);

        // Check for the 3 buttons: Start, Settings, Quit
        long buttonCount = leftBox.getChildren().stream()
                .filter(node -> node instanceof Button)
                .count();

        assertEquals(3, buttonCount, "There should be 3 buttons in the left VBox");

        Button startBtn = (Button) leftBox.getChildren().get(0);
        assertEquals("Start Game", startBtn.getText());
    }

    @Test
    void testTitleLabels() {
        HBox mainBox = (HBox) homeController.getChildren().get(0);
        VBox rightBox = (VBox) mainBox.getChildren().get(1);

        Label titleLabel = (Label) rightBox.getChildren().get(0);
        assertEquals("Dodoco Bombtastic", titleLabel.getText());

        Label subtitleLabel = (Label) rightBox.getChildren().get(1);
        assertEquals("Adventure Puzzles", subtitleLabel.getText());
    }

    @Test
    void testStartButtonAction() {
        // To test setRoot, we need a dummy Scene
        Scene scene = new Scene(homeController);

        HBox mainBox = (HBox) homeController.getChildren().get(0);
        VBox leftBox = (VBox) mainBox.getChildren().get(0);
        Button startButton = (Button) leftBox.getChildren().get(0);

        // Fire the event
        Platform.runLater(startButton::fire);

        // Note: In real JFX testing, we'd use a library like TestFX
        // to verify the scene root changed to StageSelectController.
        assertNotNull(startButton.getOnAction(), "Start button should have an action defined");
    }
}