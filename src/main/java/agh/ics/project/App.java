package agh.ics.project;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class App extends Application {
    private AbstractMap leftMap;
    private AbstractMap rightMap;
    private Vector2d lowBoundary;
    private Vector2d upBoundary;
    private GridPane leftGrid;
    private GridPane rightGrid;
    private int columnWidth=40;
    private int rowHeight=40;


    @Override
    public void start(Stage primaryStage) {
        rightGrid = new GridPane();
        leftGrid = new GridPane();
        HBox hBox= new HBox(leftGrid,rightGrid);
        Scene scene = new Scene(hBox,1000,600);
        lowBoundary=new Vector2d(0,0);
        upBoundary=new Vector2d(20,20);
        primaryStage.setScene(scene);
        primaryStage.show();
        SimulationEngine rightEng=new SimulationEngine(this,true, upBoundary.x, upBoundary.y, 1000,5,500,0.4,10);
        rightMap=rightEng.getMap();
        SimulationEngine leftEng=new SimulationEngine(this,false, upBoundary.x, upBoundary.y, 1000,5,500,0.4,10);
        leftMap=leftEng.getMap();
        Thread leftEngineThread=new Thread(leftEng);
        Thread rightEngineThread=new Thread(rightEng);
        leftEngineThread.start();
        rightEngineThread.start();
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
                    vBox = new GuiElementBox((MapCell) map.objectAt(actualPosition)).getvBox();
                    GridPane.setHalignment(vBox, HPos.CENTER);
                    grid.add(vBox, index, verticalIndex);
                }
                index++;
            }
            verticalIndex++;
        }
    }

}
