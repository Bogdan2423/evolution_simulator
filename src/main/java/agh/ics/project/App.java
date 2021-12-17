package agh.ics.project;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Label;

import static java.lang.System.out;

public class App extends Application {
    private AbstractMap leftMap;
    private AbstractMap rightMap;
    private Vector2d lowBoundary;
    private Vector2d upBoundary;
    private GridPane leftGrid;
    private GridPane rightGrid;
    private int columnWidth;
    private int rowHeight;
    private int sceneWidth=1500;
    private int sceneHeight=800;
    private Thread leftEngineThread;
    private Thread rightEngineThread;

    @Override
    public void start(Stage primaryStage) {
        rightGrid = new GridPane();
        leftGrid = new GridPane();

        Label xLabel=new Label("Map width:");
        TextField xField=new TextField();
        Label yLabel=new Label("Map height:");
        TextField yField=new TextField();
        Label startEnergyLabel=new Label("Animal starting energy:");
        TextField startEnergyField=new TextField();
        Label moveEnergyLabel=new Label("Animal energy move cost:");
        TextField moveEnergyField=new TextField();
        Label plantEnergyLabel=new Label("Energy gained from eating plants:");
        TextField plantEnergyField=new TextField();
        Label jungleRatioLabel=new Label("Ratio of jungle to map size");
        TextField jungleRatioField=new TextField();
        Label animalCountLabel=new Label("Number of animals:");
        TextField animalCountField=new TextField();
        Label magicalLabel=new Label("Magical simulation:");
        CheckBox leftMagical=new CheckBox("Left map");
        CheckBox rightMagical=new CheckBox("Left map");
        HBox checkHBox=new HBox(leftMagical,rightMagical);
        Button startButton=new Button("Start");
        Button pauseButton=new Button("Pause");
        Button resumeButton=new Button("Resume");
        HBox buttonsBox=new HBox(startButton,pauseButton,resumeButton);
        Label exceptionLabel=new Label();
        VBox parametersBox=new VBox(xLabel,xField,yLabel,yField,startEnergyLabel,startEnergyField,
                moveEnergyLabel,moveEnergyField,plantEnergyLabel,plantEnergyField,jungleRatioLabel,jungleRatioField,
                animalCountLabel,animalCountField,magicalLabel,checkHBox,buttonsBox,exceptionLabel);

        HBox hBox= new HBox(parametersBox,leftGrid,rightGrid);

        Scene scene = new Scene(hBox,1500,800);
        primaryStage.setScene(scene);
        primaryStage.show();

        startButton.setOnAction((event -> {
            exceptionLabel.setText("");
            int startEnergy;
            int moveEnergy;
            int plantEnergy;
            double jungleRatio;
            int animalCount;
            boolean isLeftMagical;
            boolean isRightMagical;
            try {
                lowBoundary = new Vector2d(0, 0);
                upBoundary = new Vector2d(Integer.parseInt(xField.getText()), Integer.parseInt(yField.getText()));
                startEnergy = Integer.parseInt(startEnergyField.getText());
                moveEnergy = Integer.parseInt(moveEnergyField.getText());
                plantEnergy = Integer.parseInt(plantEnergyField.getText());
                jungleRatio = Double.parseDouble(jungleRatioField.getText());
                animalCount = Integer.parseInt(animalCountField.getText());
                isLeftMagical=leftMagical.isSelected();
                isRightMagical=rightMagical.isSelected();
                columnWidth = ((sceneWidth - 200) / (2 * upBoundary.x));
                rowHeight = ((sceneHeight - 200) / (upBoundary.y));
                SimulationEngine rightEng = new SimulationEngine(this, true, upBoundary.x, upBoundary.y, startEnergy,
                        moveEnergy, plantEnergy, jungleRatio, animalCount,isRightMagical);
                SimulationEngine leftEng = new SimulationEngine(this, false, upBoundary.x, upBoundary.y, startEnergy,
                        moveEnergy, plantEnergy, jungleRatio, animalCount,isLeftMagical);
                leftMap = leftEng.getMap();
                rightMap = rightEng.getMap();
                leftEngineThread=new Thread(leftEng);
                rightEngineThread=new Thread(rightEng);
                leftEngineThread.start();
                rightEngineThread.start();
            }
            catch (Exception ex)
            {
                exceptionLabel.setText("Enter all parameters correctly!\n"+ex);
            }
            pauseButton.setOnAction((event1 -> {
                leftEngineThread.interrupt();
                rightEngineThread.interrupt();
                exceptionLabel.setText("Simulation paused");
            }));
            resumeButton.setOnAction((event1 -> {
                leftEngineThread.interrupt();
                rightEngineThread.interrupt();
                exceptionLabel.setText("");
            }));
        }));

    }

    public void showMap(boolean walledMap){
        GridPane grid;
        if (walledMap)
            grid=rightGrid;
        else
            grid=leftGrid;

        grid.setGridLinesVisible(false);
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();
        grid.setGridLinesVisible(true);
        makeGrid(grid,lowBoundary,upBoundary);
        addElements(grid,lowBoundary,upBoundary,walledMap);
    }

    private void makeGrid(GridPane grid, Vector2d lowBoundary, Vector2d upBoundary){

        Label label;
        grid.getColumnConstraints().add(new ColumnConstraints(columnWidth));
        label=new Label("y\\x");
        grid.addColumn(0,label);
        GridPane.setHalignment(label, HPos.CENTER);

        int index=1;
        int range=upBoundary.x-lowBoundary.x+2;
        while (index<range){
            label=new Label(String.valueOf(lowBoundary.x+index-1));
            grid.getColumnConstraints().add(new ColumnConstraints(columnWidth));
            grid.addColumn(index,label);
            GridPane.setHalignment(label, HPos.CENTER);
            index++;
        }

        int verticalRange=upBoundary.y-lowBoundary.y+2;
        int verticalIndex=1;
        while (verticalIndex<verticalRange){
            grid.addRow(verticalIndex);
            grid.getRowConstraints().add(new RowConstraints(rowHeight));
            label=new Label(String.valueOf(upBoundary.y-verticalIndex+1));
            grid.add(label,0,verticalIndex);
            GridPane.setHalignment(label, HPos.CENTER);
            verticalIndex++;
        }
        grid.getRowConstraints().add(new RowConstraints(rowHeight));
    }

    private void addElements(GridPane grid, Vector2d lowBoundary, Vector2d upBoundary, boolean walledMap){
        AbstractMap map;
        if (walledMap)
            map=rightMap;
        else
            map=leftMap;

        int range=upBoundary.x-lowBoundary.x+2;
        int verticalRange=upBoundary.y-lowBoundary.y+2;
        int verticalIndex=1;
        while (verticalIndex<verticalRange) {
            int index = 1;
            VBox vBox;
            while (index < range) {
                Vector2d actualPosition = new Vector2d(lowBoundary.x + index - 1, upBoundary.y - verticalIndex + 1);
                if (map.isOccupied(actualPosition)) {
                    vBox = new GuiElementBox((MapCell) map.objectAt(actualPosition),columnWidth,rowHeight).getvBox();
                    GridPane.setHalignment(vBox, HPos.CENTER);
                    grid.add(vBox, index, verticalIndex);
                }
                index++;
            }
            verticalIndex++;
        }
    }

}
