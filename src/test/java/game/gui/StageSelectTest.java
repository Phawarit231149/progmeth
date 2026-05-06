package game.gui;

import gui.StageSelectController;
import gui.GameController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.GameProgress;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StageSelectTest {

    private StageSelectController controller;
    private Scene scene;

    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    @BeforeEach
    void setUp() {
        // Reset progress for clean testing
        // (Assuming GameProgress has a reset method or you clear it)
        controller = new StageSelectController();
        scene = new Scene(controller);
    }

    @Test
    void testInitialState() {
        // Instead of lookup, use direct child access
        HBox bottomBox = (HBox) controller.getBottom();
        Button startBtn = (Button) bottomBox.getChildren().get(1); // Start is the second button added
    }

    @Test
    void testCharacterSelectionToggle() {
        // Find the "Start" button
        Button startBtn = (Button) controller.getBottom().lookup(".button:last-child");

        // Find a character button (SpongeBob)
        Button spongeBtn = findCharacterButton("SpongeBob");
        assertNotNull(spongeBtn);

        // Action: Select SpongeBob
        spongeBtn.fire();
        assertFalse(startBtn.isDisable(), "Start button should enable after picking a character");

        // Action: Click SpongeBob again (Deselect)
        spongeBtn.fire();
        assertTrue(startBtn.isDisable(), "Start button should disable after deselecting character");
    }

    @Test
    void testStageUnlockingLogic() {
        // Initially, Stage 0 (I) is unlocked, Stage 1 (II) is locked
        assertTrue(GameProgress.isUnlocked(0));
        assertFalse(GameProgress.isUnlocked(1));

        // Click Stage II button
        Button stageIIBtn = findStageButton(1); // Index 1 for Stage II
        stageIIBtn.fire();

        // Label should NOT change to Stage II because it's locked
        Label title = (Label) controller.getTop().lookup(".label");
        assertTrue(title.getText().contains("Stage I"), "Should stay on Stage I if Stage II is locked");
    }

    @Test
    void testGameStartTransition() {
        // 1. Setup
        StageSelectController controller = new StageSelectController();
        Scene testScene = new Scene(controller); // MUST attach to a scene

        // 2. Select Character (to enable start button)
        Button patrickBtn = findCharacterButton("Patrick");
        patrickBtn.fire(); // Use .fire() instead of .handle(null) to simulate a real click

        // 3. Click Start
        HBox bottomBox = (HBox) controller.getBottom();
        Button startBtn = (Button) bottomBox.getChildren().get(1);

        startBtn.fire();

        assertTrue(testScene.getRoot() instanceof GameController);
    }

    // --- Helpers to navigate the deep JavaFX tree ---

    private Button findCharacterButton(String name) {
        // Search through the right VBox -> Character HBox
        VBox right = (VBox) controller.getRight();
        return right.getChildren().stream()
                .filter(node -> node instanceof javafx.scene.layout.HBox)
                .flatMap(node -> ((javafx.scene.layout.HBox) node).getChildren().stream())
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .filter(btn -> {
                    Object data = btn.getUserData();
                    return data instanceof Object[] && ((Object[])data)[0].equals(name);
                })
                .findFirst()
                .orElse(null);
    }

    private Button findStageButton(int index) {
        VBox left = (VBox) controller.getLeft();
        return (Button) left.getChildren().get(index);
    }
}