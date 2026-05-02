package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class RuleController extends BorderPane {
    public RuleController(){
        Label title = new Label("Rule");
        title.setFont(Font.font(30));
        BorderPane.setMargin(title,new Insets(20,0,0,20));

        Label description = new Label("Description: [gsfsgssffseffsfs]\nsefsfsfsfsfesfs");
        description.setFont(Font.font(24));
        BorderPane.setMargin(description,new Insets(20,0,0,20));


        Button back = new Button("Back");
        back.setOnAction(e -> {
            StageSelectController stageSelectController = new StageSelectController();
            this.getScene().setRoot(stageSelectController);
        });

        HBox footer = new HBox();
        footer.setAlignment(Pos.BOTTOM_LEFT);
        footer.setPadding(new Insets(10));
        footer.getChildren().add(back);

        this.setTop(title);
        this.setLeft(description);
        this.setBottom(footer);
    }
}
