package agh.ics.project;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static java.lang.System.out;

public class GuiElementBox {
    private VBox vBox;
    private Label label;
    private ImageView imageView;

    GuiElementBox(MapCell element){
        try {
            create(element);
        }
        catch (FileNotFoundException ex){
            out.print("File not found: "+ex);
        }
    }

    public void create(MapCell element) throws FileNotFoundException{
        Image image = new Image(new FileInputStream(element.getImagePath()));
        imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);

        label=new Label("placeholder");

        vBox=new VBox();
        vBox.getChildren().addAll(imageView,label);
    }

    public VBox getvBox(){return vBox;}

}