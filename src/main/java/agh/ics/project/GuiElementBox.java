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
    private SimulationEngine engine;

    GuiElementBox(MapCell element,int x, int y,SimulationEngine engine){
        try {
            create(element,x,y);
        }
        catch (FileNotFoundException ex){
            out.print("File not found: "+ex);
        }
        this.engine=engine;
    }

    public void create(MapCell element,int x,int y) throws FileNotFoundException{
        Image image = new Image(new FileInputStream(element.getImagePath()));
        imageView = new ImageView(image);
        imageView.setFitWidth(x*0.9);
        imageView.setFitHeight(y*0.6);
        label=new Label(element.getLabel());
        vBox=new VBox();
        vBox.setMaxWidth(x);
        vBox.setMaxHeight(y);
        vBox.getChildren().addAll(imageView,label);
        vBox.setOnMouseClicked(event -> {engine.setAnimal(element.getTopAnimal());});
    }

    public VBox getvBox(){return vBox;}

}