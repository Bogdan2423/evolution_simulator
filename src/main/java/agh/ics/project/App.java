package agh.ics.project;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SimulationEngine eng=new SimulationEngine(this,true,10,10,1000,5,500,0.4,10);
        eng.run();
    }
}
