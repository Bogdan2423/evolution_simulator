package agh.ics.project;

import javafx.application.Platform;
import java.io.FileWriter;
import java.io.IOException;


public class SimulationEngine implements Runnable {
    private AbstractMap map;
    private App app;
    private boolean walledMap;
    private boolean isPaused;
    SimulationEngine(App app, boolean walledMap, int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int animalCount, boolean isMagical)
    {
        this.app=app;
        if (width<=0 || height<=0 || startEnergy<=0 || moveEnergy<=0 || plantEnergy<=0 || animalCount<=0)
            throw new IllegalArgumentException("Map parameters need to be positive!");
        if (jungleRatio<0||jungleRatio>1)
            throw new IllegalArgumentException("Jungle ratio must be between 0 and 1!");
        if (animalCount>width*height)
            throw new IllegalArgumentException("Can't place that many animals!");

        this.walledMap=walledMap;
        if (walledMap)
            map = new WalledMap(width, height, startEnergy, plantEnergy, jungleRatio, moveEnergy,isMagical);
        else
            map=new UnboundMap(width,height,startEnergy,plantEnergy,jungleRatio,moveEnergy,isMagical);

        map.putAnimalsRandomly(animalCount);
    }

    @Override
    public void run() {
        isPaused=false;
        while(true) {
            int moveDelay = 500;
            map.newDay();
            Platform.runLater(() -> app.showMap(walledMap));
            try {Thread.sleep(moveDelay);}
            catch (InterruptedException ex) {
                isPaused=true;
                while(true) {
                    try {Thread.sleep(moveDelay);}
                    catch (InterruptedException ex1) {
                        isPaused=false;
                        break;
                    }
                }
            }
            }
        }
    public void setAnimal(Animal animal){
        if (isPaused)
            map.setAnimal(animal);
    }
    public void highlightTopGenome(){
        if (isPaused)
            map.highlightTopGenome();
    }
    public void save(){
        if (isPaused){
            String stats=map.getStats().toString();
            String filename="";
            if (walledMap)
                filename+="rightMap";
            else
                filename+="leftMap";
            filename+="StatsDay"+(map.getDayCount()-1)+".csv";
            try{
                FileWriter file=new FileWriter(filename);
                file.write(stats);
                file.flush();
                file.close();
            }
            catch (IOException ex){}
        }
    }

    public AbstractMap getMap(){return map;}
}
