package game.gui;
import gui.GameOverController;
import gui.GameController;
import gui.StageSelectController;
import gui.Status;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.StageData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameOverTest {

    private StageData stage1;
    private Scene scene;
    private final String charName = "SpongeBob";

    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    @BeforeEach
    void setUp() {
        stage1 = StageData.ALL_STAGES[0]; // Stage I (Level 1)
    }

    @Test
    void testWinStateButtons() {
        GameOverController controller = new GameOverController(Status.WIN, stage1, charName);
        VBox layout = (VBox) controller.getCenter();

        // WIN should show: Title, Next Stage, Retry, Back
        assertEquals(4, layout.getChildren().size(), "WIN state should show 4 children in VBox");

        Label title = (Label) layout.getChildren().get(0);
        assertEquals("Stage Clear!", title.getText());

        Button nextBtn = (Button) layout.getChildren().get(1);
        assertEquals("Next Stage", nextBtn.getText());
    }

    @Test
    void testLoseStateButtons() {
        GameOverController controller = new GameOverController(Status.LOSE, stage1, charName);
        VBox layout = (VBox) controller.getCenter();

        // LOSE should show: Title, Retry, Back (No Next Stage)
        assertEquals(3, layout.getChildren().size(), "LOSE state should show 3 children (no Next Stage)");

        Label title = (Label) layout.getChildren().get(0);
        assertEquals("Mission Fail!", title.getText());
    }

    @Test
    void testRetryLogic() {
        GameOverController controller = new GameOverController(Status.LOSE, stage1, charName);
        scene = new Scene(controller);

        VBox layout = (VBox) controller.getCenter();
        Button retryBtn = (Button) layout.getChildren().get(1); // Index 1 is Retry in Lose state

        // Trigger Retry
        retryBtn.getOnAction().handle(null);

        // Verify it returns to a GameController with the SAME stage config
        assertTrue(scene.getRoot() instanceof GameController, "Should return to GameController");
    }

    @Test
    void testNextStageLogic() {
        // Current stage is Stage I (Index 0, Level 1)
        GameOverController controller = new GameOverController(Status.WIN, stage1, charName);
        scene = new Scene(controller);

        VBox layout = (VBox) controller.getCenter();
        Button nextBtn = (Button) layout.getChildren().get(1); // Index 1 is Next Stage in Win state

        // Trigger Next Stage
        nextBtn.getOnAction().handle(null);

        // Logic check: Next Stage should load Level 2 (Index 1)
        assertTrue(scene.getRoot() instanceof GameController);
        // Note: You could further verify by checking the config inside GameController if accessible
    }

    @Test
    void testBackToStageSelectLogic() {
        GameOverController controller = new GameOverController(Status.WIN, stage1, charName);
        scene = new Scene(controller);

        VBox layout = (VBox) controller.getCenter();
        // In WIN state, Back button is the 4th child (index 3)
        Button backBtn = (Button) layout.getChildren().get(3);

        backBtn.getOnAction().handle(null);

        assertTrue(scene.getRoot() instanceof StageSelectController, "Should return to Stage Select");
    }
}
