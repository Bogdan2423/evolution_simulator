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
    protected int magicalCount=3;
    protected boolean isMagical;
    protected int plantCount=0;
    protected int deadAnimalsCount=0;
    protected int daysLivedSum=0;
    protected Map<int[], Integer> genotypeCounter=new LinkedHashMap<>();


    AbstractMap(int x,int y, int startEnergy, int plantEnergy,double jungleRatio,int moveEnergy,boolean isMagical)
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
        this.isMagical=isMagical;

    }

    public void newDay(){
        out.print("\nDay "+dayCount);
        out.print("\nAnimal count: "+animals.size()+"\n");
        if(isMagical){
            if(animals.size()==5 && magicalCount>0)
                magicSpawn();
        }

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

    private void magicSpawn(){
        magicalCount--;
        int i=5;
        int[] genesCopy;
        Vector2d initPos;
        while (i>0){
            i--;
            genesCopy=animals.get(i).getGenes().clone();
            initPos=getRandomEmptyPosition();
            if (initPos==null)
                return;
            placeAnimal(initPos,new Animal(this,initPos,startEnergy,genesCopy));
        }
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
        if (genotypeCounter.get(animal.getGenes())==null)
            genotypeCounter.put(animal.getGenes(), 1);
        else {
            Integer curr=genotypeCounter.get(animal.getGenes());
            genotypeCounter.put(animal.getGenes(), curr+1);
        }
        out.print("\nNew animal at: "+position);
    }

    public void putAnimalsRandomly(int n){
        Vector2d pos;
        Animal newAnimal;
        while (n>0){
            pos=getRandomEmptyPosition();
            if (pos==null)
                return;
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


    private void killAnimals(){
        Integer curr;
        for(Animal animal:markedForDeath){
            Vector2d pos=animal.getPosition();
            MapCell cell=mapCells.get(pos);
            deadAnimalsCount++;
            daysLivedSum+=animal.getDaysLived();
            if(genotypeCounter.get(animal.getGenes())==1)
                genotypeCounter.remove(animal.getGenes());
            else{
                curr=genotypeCounter.get(animal.getGenes());
                genotypeCounter.put(animal.getGenes(), curr-1);
            }
            cell.removeAnimal(animal);
            animals.remove(animal);
        }
        markedForDeath.clear();
    }


    private void addCell(Vector2d position){
        mapCells.put(position,new MapCell(plantEnergy,startEnergy,this,position));
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

    private void addPlants(){
        boolean placedInJungle=false;
        boolean placedOutsideJungle=false;
        int i=0;
        Vector2d pos;
        while((!placedInJungle || !placedOutsideJungle)&&i<upBoundary.x*upBoundary.y*2){
            pos=getRandomEmptyPosition();
            if (pos==null) {
                placedInJungle=true;
                placedOutsideJungle=true;
            }
            else if (isInJungle(pos)&&!placedInJungle){
                addCell(pos);
                mapCells.get(pos).addPlant();
                plantCount++;
                placedInJungle=true;
            }
            else if(!isInJungle(pos)&&!placedOutsideJungle){
                addCell(pos);
                mapCells.get(pos).addPlant();
                plantCount++;
                placedOutsideJungle=true;
            }
            i++;
        }
    }

    private Vector2d getRandomEmptyPosition(){
        int i=0;
        int x;
        int y;
        Vector2d pos;
        while(i<upBoundary.x* upBoundary.y*2) {
            x = rand.nextInt(upBoundary.x + 1);
            y = rand.nextInt(upBoundary.y + 1);
            pos = new Vector2d(x, y);
            if (!isOccupied(pos))
                return pos;
            i++;
        }
        return null;
    }

    public boolean isInJungle(Vector2d position){
        return position.follows(jungleLowBoundary)&& position.precedes(jungleUpBoundary);
    }

    public void plantEaten(){plantCount--;}

    public int getMoveEnergy(){return moveEnergy;}

    public double getAverageEnergy(){
        int sum=0;
        for(Animal animal:animals){
            sum+=animal.energy;
        }
        return (double)sum/(double)animals.size();
    }

    public int[] getTopGenotype(){
        int currMax=0;
        int[] topGenotype = new int[0];
        for(int[] genotype:genotypeCounter.keySet()){
            if (genotypeCounter.get(genotype)>currMax){
                currMax=genotypeCounter.get(genotype);
                topGenotype=genotype;
            }
        }
        return topGenotype;
    }

    public int getAnimalCount(){return animals.size();}

    public double getAverageDaysLived(){
        return (double)daysLivedSum/(double)deadAnimalsCount;
    }

    public double getAverageChildrenCount(){
        int sum=0;
        for(Animal animal:animals){
            sum+=animal.getChildrenCount();
        }
        return (double)sum/(double)animals.size();
    }

    public Object objectAt(Vector2d position){
        return mapCells.get(position);
    }
    public int getPlantCount(){return plantCount;}

    public void addPlantAtPos(Vector2d position){mapCells.get(position).addPlant();}

    public void breedAtPos(Vector2d position){mapCells.get(position).breed();}
    public void eatAtPos(Vector2d position){mapCells.get(position).eatPlant();}

    public abstract Vector2d getMoveVector(Vector2d oldPosition, Vector2d newPosition);

    public boolean isOccupied(Vector2d pos){return mapCells.get(pos)!=null;}
}
