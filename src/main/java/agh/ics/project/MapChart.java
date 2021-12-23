package agh.ics.project;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;


public class MapChart {
    private int size=150;
    NumberAxis xAxis1=new NumberAxis();
    NumberAxis yAxis1=new NumberAxis();
    NumberAxis xAxis2=new NumberAxis();
    NumberAxis yAxis2=new NumberAxis();
    NumberAxis xAxis3=new NumberAxis();
    NumberAxis yAxis3=new NumberAxis();
    NumberAxis xAxis4=new NumberAxis();
    NumberAxis yAxis4=new NumberAxis();
    NumberAxis xAxis5=new NumberAxis();
    NumberAxis yAxis5=new NumberAxis();
    LineChart energyChart=new LineChart(xAxis1,yAxis1);
    LineChart animalChart=new LineChart(xAxis2,yAxis2);
    LineChart plantChart=new LineChart(xAxis3,yAxis3);
    LineChart daysChart=new LineChart(xAxis4,yAxis4);
    LineChart childrenChart=new LineChart(xAxis5,yAxis5);
    XYChart.Series energy=new XYChart.Series();
    XYChart.Series animalCount=new XYChart.Series();
    XYChart.Series plantCount=new XYChart.Series();
    XYChart.Series daysLived=new XYChart.Series();
    XYChart.Series avgChildren=new XYChart.Series();
    Label genotypeLabel=new Label();
    VBox box;
    AbstractMap map;

    MapChart(AbstractMap map){
        this.map=map;
        Label energyLabel=new Label("Average animal energy");
        energyChart.getData().add(energy);
        Label animalLabel=new Label("Number of animals");
        animalChart.getData().add(animalCount);
        Label plantLabel=new Label("Number of plants");
        plantChart.getData().add(plantCount);
        Label daysLabel=new Label("Average days lived by dead animals");
        daysChart.getData().add(daysLived);
        Label childrenLabel=new Label("Average number of children for living animals");
        childrenChart.getData().add(avgChildren);
        energyChart.setMaxWidth(size);
        energyChart.setMaxHeight(size);
        daysChart.setMaxWidth(size);
        daysChart.setMaxHeight(size);
        plantChart.setMaxWidth(size);
        plantChart.setMaxHeight(size);
        animalChart.setMaxWidth(size);
        animalChart.setMaxHeight(size);
        childrenChart.setMaxWidth(size);
        childrenChart.setMaxHeight(size);
        VBox box1=new VBox(animalLabel,animalChart,plantLabel,plantChart);
        VBox box2=new VBox(energyLabel,energyChart,daysLabel,daysChart);
        HBox hBox=new HBox(box1,box2);
        box=new VBox(hBox,childrenLabel,childrenChart,genotypeLabel);
    }

    public void update(){
        energy.getData().add(new XYChart.Data(map.getDayCount(),map.getAverageEnergy()));
        animalCount.getData().add(new XYChart.Data(map.getDayCount(),map.getAnimalCount()));
        plantCount.getData().add(new XYChart.Data(map.getDayCount(),map.getPlantCount()));
        daysLived.getData().add(new XYChart.Data(map.getDayCount(),map.getAverageDaysLived()));
        avgChildren.getData().add(new XYChart.Data(map.getDayCount(),map.getAverageChildrenCount()));
        genotypeLabel.setText("Dominating genotype: \n"+ Arrays.toString(map.getTopGenotype()));
    }

    public VBox getChart(){
        return box;
    }

}
