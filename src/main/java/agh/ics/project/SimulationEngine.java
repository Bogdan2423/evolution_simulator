package agh.ics.project;

import javafx.application.Platform;

import static java.lang.System.out;


public class SimulationEngine implements Runnable {
    private AbstractMap map;
    private App app;
    SimulationEngine(App app, boolean walledMap, int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int animalCount)
    {
        this.app=app;
        if (width<=0 || height<=0 || startEnergy<=0 || moveEnergy<=0 || plantEnergy<=0 || animalCount<=0)
            throw new IllegalArgumentException("Map parameters need to be positive!");
        if (jungleRatio<0||jungleRatio>1)
            throw new IllegalArgumentException("Jungle ratio must be between 0 and 1!");
        if (animalCount>width*height)
            throw new IllegalArgumentException("Can't place that many animals!");

        if (walledMap)
            map=new WalledMap(width,height,startEnergy,plantEnergy,jungleRatio,moveEnergy);
        else
            map=new UnboundMap(width,height,startEnergy,plantEnergy,jungleRatio,moveEnergy);

        map.putAnimalsRandomly(animalCount);
    }

    @Override
    public void run() {
        int i=1000;
        while(i>0) {
            int moveDelay = 10;
            map.newDay();
            /*Platform.runLater(() -> app.showMap());
            try {
                Thread.sleep(moveDelay);
            } catch (InterruptedException ex) {
                out.print("Simulation stopped");
            }*/
            i--;
        }
    }
}
