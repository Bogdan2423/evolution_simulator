package agh.ics.project;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.System.out;

public abstract class AbstractMap implements IPositionChangeObserver{
    protected Vector2d lowBoundary=new Vector2d(0,0);
    protected Vector2d upBoundary;
    protected Vector2d jungleLowBoundary;
    protected Vector2d jungleUpBoundary;
    protected Map<Vector2d, MapCell> mapCells=new LinkedHashMap<>();
    protected ArrayList<Animal> animals=new ArrayList<>();
    protected int startEnergy;
    protected int plantEnergy;
    protected int moveEnergy;
    protected Random rand=new Random();
    protected double jungleRatio;
    protected int jungleSize;
    protected ArrayList<Animal> markedForDeath=new ArrayList<>();
    protected int dayCount=0;


    AbstractMap(int x,int y, int startEnergy, int plantEnergy,double jungleRatio,int moveEnergy)
    {
        this.upBoundary=new Vector2d(x,y);
        this.startEnergy=startEnergy;
        this.plantEnergy=plantEnergy;
        this.jungleRatio=jungleRatio;
        this.moveEnergy=moveEnergy;
        double jungleSize=jungleRatio*x*y;
        this.jungleSize= (int) Math.round(jungleSize-1);
        int jungleSideLength= (int) Math.round(Math.sqrt(jungleSize));
        jungleLowBoundary=new Vector2d(x/2-jungleSideLength/2,y/2-jungleSideLength/2);
        jungleUpBoundary=new Vector2d(x/2+jungleSideLength/2,y/2+jungleSideLength/2);
    }

    public void newDay(){
        out.print("\nDay "+dayCount);
        out.print("\nAnimal count: "+animals.size()+"\n");

        addPlants();
        for (Animal animal:animals){
            animal.randomMove();
        }
        killAnimals();
        clearCells();
        for (MapCell cell:mapCells.values()) {
            cell.eatPlant();
            cell.breed();
        }
        dayCount++;
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal){
        mapCells.get(oldPosition).removeAnimal(animal);
        moveAnimal(newPosition, animal);
    }

    private void moveAnimal(Vector2d position, Animal animal){
        if(mapCells.get(position)==null)
            addCell(position);
        mapCells.get(position).addAnimal(animal);
    }

    public void placeAnimal(Vector2d position, Animal animal){
        moveAnimal(position,animal);
        animals.add(animal);
        out.print("\nNew animal at: "+position);
    }

    public void putAnimalsRandomly(int n){
        int x;
        int y;
        Vector2d pos;
        Animal newAnimal;
        while (n>0){
            x= rand.nextInt(upBoundary.x+1);
            y= rand.nextInt(upBoundary.y+1);
            pos=new Vector2d(x,y);
            if (mapCells.get(pos)==null)
            {
                newAnimal=new Animal(this,pos,startEnergy);
                placeAnimal(pos,newAnimal);
            }
            n--;
        }
    }

    public void animalDead(Animal animal){
        markedForDeath.add(animal);
    }

    private void clearCells(){
        ArrayList<Vector2d> toClear=new ArrayList<>();
        for (MapCell cell:mapCells.values()){
            if (cell.isEmpty())
                toClear.add(cell.getPosition());
        }
        for (Vector2d pos:toClear){
            mapCells.remove(pos);
        }
    }

    private void killAnimals(){
        for(Animal animal:markedForDeath){
            Vector2d pos=animal.getPosition();
            MapCell cell=mapCells.get(pos);
            cell.removeAnimal(animal);
            animals.remove(animal);
        }
        markedForDeath.clear();
    }


    private void addCell(Vector2d position){
        mapCells.put(position,new MapCell(plantEnergy,startEnergy,this,position));
    }

    private void addPlants(){
        boolean placedInJungle=false;
        boolean placedOutsideJungle=false;
        int x;
        int y;
        Vector2d pos;
        int i=0;
        while((!placedInJungle || !placedOutsideJungle)&&i<upBoundary.x* upBoundary.y*2){
            x= rand.nextInt(upBoundary.x+1);
            y= rand.nextInt(upBoundary.y+1);
            pos=new Vector2d(x,y);
            if (isInJungle(pos)&&!placedInJungle){
                if (mapCells.get(pos)==null){
                    addCell(pos);
                    mapCells.get(pos).addPlant();
                    placedInJungle=true;
                }
            }
            else if(!isInJungle(pos)&&!placedOutsideJungle){
                if (mapCells.get(pos)==null){
                    addCell(pos);
                    mapCells.get(pos).addPlant();
                    placedOutsideJungle=true;
                }
            }
            i++;
        }
    }


    public boolean isInJungle(Vector2d position){
        return position.follows(jungleLowBoundary)&& position.precedes(jungleUpBoundary);
    }

    public int getMoveEnergy(){return moveEnergy;}



    public Object objectAt(Vector2d position){
        return mapCells.get(position);
    }

    public void addPlantAtPos(Vector2d position){mapCells.get(position).addPlant();}

    public void breedAtPos(Vector2d position){mapCells.get(position).breed();}
    public void eatAtPos(Vector2d position){mapCells.get(position).eatPlant();}

    public abstract Vector2d getMoveVector(Vector2d oldPosition, Vector2d newPosition);

    public boolean isOccupied(Vector2d pos){return mapCells.get(pos)!=null;}
}
