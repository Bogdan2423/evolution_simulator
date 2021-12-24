package agh.ics.project;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class App extends Application {
    private AbstractMap leftMap;
    private AbstractMap rightMap;
    private Vector2d lowBoundary;
    private Vector2d upBoundary;
    private GridPane leftGrid;
    private GridPane rightGrid;
    private int columnWidth;
    private int rowHeight;
    private int sceneWidth=1600;
    private int sceneHeight=900;
    private SimulationEngine leftEng;
    private SimulationEngine rightEng;
    private Thread leftEngineThread;
    private Thread rightEngineThread;
    private MapChart leftChart;
    private MapChart rightChart;

    @Override
    public void start(Stage primaryStage) {
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
        CheckBox rightMagical=new CheckBox("Right map");
        HBox checkHBox=new HBox(leftMagical,rightMagical);
        Button startButton=new Button("Start");
        Button leftPauseButton=new Button("Pause/resume");
        Button rightPauseButton=new Button("Pause/resume");
        Button leftHighlightButton=new Button("Toggle top genome highlight");
        Button rightHighlightButton=new Button("Toggle top genome highlight");
        Button leftSaveButton=new Button("Save stats to file");
        Button rightSaveButton=new Button("Save stats to file");
        Label descriptionLabel=new Label("Each animal changes its direction or moves every day, based on their genotype\n" +
                "Animals lose energy from moving, but may regain it by eating plants\n" +
                "Each day one plant appears in the jungle and one outside of the jungle\n" +
                "If two animals with enough energy enter one map cell, they will reproduce, passing part of their genes to their child\n" +
                "On the left map, if the animal walks into the border, it will appear on the other side of the map\n" +
                "The right map is walled-animals cannot move outside the border\n" +
                "If \"Magical simulation\" is selected, when there is exactly 5 animals on the map at the beginning of a day,\n" +
                "5 new animals, which are copies of existing animals, will appear randomly\n" +
                "To start the simulation, enter the map parameters and press Start\n"+
                "Next to each map will be shown some stats and dominating genotypes\n" +
                "(all dominating genotypes are shown-if each animal has different genotype, all genotypes will be shown)\n"+
                "You can pause each map by clicking the button under it\n" +
                "When map is paused, you can:\n" +
                "-Click one animal to highlight it and track its stats\n" +
                " (tracking will start after unpausing)\n" +
                "-Toggle the highlighting of all animals with dominating genotypes\n" +
                "-Save map stats to CSV file");
        Label exceptionLabel=new Label();
        VBox parametersBox=new VBox(xLabel,xField,yLabel,yField,startEnergyLabel,startEnergyField,
                moveEnergyLabel,moveEnergyField,plantEnergyLabel,plantEnergyField,jungleRatioLabel,jungleRatioField,
                animalCountLabel,animalCountField,magicalLabel,checkHBox,startButton,descriptionLabel,exceptionLabel);



        HBox hBox= new HBox(parametersBox);

        Scene scene = new Scene(hBox,sceneWidth,sceneHeight);
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
                columnWidth = ((sceneWidth - 800) / (2 * upBoundary.x));
                rowHeight = ((sceneHeight - 400) / (upBoundary.y));
                rightEng = new SimulationEngine(this, true, upBoundary.x, upBoundary.y, startEnergy,
                        moveEnergy, plantEnergy, jungleRatio, animalCount,isRightMagical);
                leftEng = new SimulationEngine(this, false, upBoundary.x, upBoundary.y, startEnergy,
                        moveEnergy, plantEnergy, jungleRatio, animalCount,isLeftMagical);
                leftMap = leftEng.getMap();
                rightMap = rightEng.getMap();
                rightGrid = new GridPane();
                rightChart=new MapChart(rightMap);
                leftGrid = new GridPane();
                leftChart=new MapChart(leftMap);
                HBox leftButtonBox=new HBox(leftPauseButton,leftHighlightButton,leftSaveButton);
                VBox leftMapVBox=new VBox(leftGrid,leftButtonBox);
                HBox leftMapBox=new HBox(leftMapVBox,leftChart.getChart());
                HBox rightButtonBox=new HBox(rightPauseButton,rightHighlightButton,rightSaveButton);
                VBox rightMapVBox=new VBox(rightGrid,rightButtonBox);
                HBox rightMapBox=new HBox(rightMapVBox,rightChart.getChart());
                hBox.getChildren().addAll(leftMapBox,rightMapBox);
                leftEngineThread=new Thread(leftEng);
                rightEngineThread=new Thread(rightEng);
                leftEngineThread.start();
                rightEngineThread.start();
                startButton.setVisible(false);
            }
            catch (Exception ex)
            {
                exceptionLabel.setText("Enter all parameters correctly!\n"+ex);
            }
            leftPauseButton.setOnAction((event1 -> {
                leftEngineThread.interrupt();
            }));
            rightPauseButton.setOnAction((event2 -> {
                rightEngineThread.interrupt();
            }));
            leftHighlightButton.setOnAction((event3 -> {
                leftEng.highlightTopGenome();
            }));
            rightHighlightButton.setOnAction((event4 -> {
                rightEng.highlightTopGenome();
            }));
            leftSaveButton.setOnAction((event5 -> {
                leftEng.save();
            }));
            rightSaveButton.setOnAction((event6 -> {
                rightEng.save();
            }));
        }));

    }

    public void showMap(boolean walledMap){
        GridPane grid;
        if (walledMap) {
            grid = rightGrid;
            rightChart.update();
        }
        else {
            grid = leftGrid;
            leftChart.update();
        }

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
                    if (walledMap)
                        vBox = new GuiElementBox((MapCell) map.objectAt(actualPosition), columnWidth, rowHeight, rightEng).getvBox();
                    else
                        vBox = new GuiElementBox((MapCell) map.objectAt(actualPosition), columnWidth, rowHeight, leftEng).getvBox();
                }
                else
                    vBox=new VBox();

                if(map.isInJungle(actualPosition))
                    vBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("#2c8a12"),CornerRadii.EMPTY, Insets.EMPTY)));
                else
                    vBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("#3db36e"),CornerRadii.EMPTY, Insets.EMPTY)));

                GridPane.setHalignment(vBox, HPos.CENTER);
                grid.add(vBox, index, verticalIndex);
                index++;
            }
            verticalIndex++;
        }
    }

}
