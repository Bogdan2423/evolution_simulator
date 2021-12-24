package agh.ics.project;
import java.util.*;

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
    protected Animal trackedAnimal;
    protected int descendantCount;
    protected boolean topGenomeHighlighted=false;
    protected StringBuilder mapStats=new StringBuilder();
    protected double animalSum=0;
    protected double plantSum=0;
    protected double avgEnergySum=0;
    protected double avgLifetimeSum=0;
    protected double avgChildrenSum=0;


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
        mapStats.append("Day");
        mapStats.append(',');
        mapStats.append("Animal count");
        mapStats.append(',');
        mapStats.append("Plant count");
        mapStats.append(',');
        mapStats.append("Avg energy");
        mapStats.append(',');
        mapStats.append("Avg lifetime");
        mapStats.append(',');
        mapStats.append("Avg children count");
        mapStats.append("\n");
    }

    public void newDay(){
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
        updateMapStats();
        updateSums();
        dayCount++;
    }

    private void updateMapStats(){
        mapStats.append(dayCount);
        mapStats.append(',');
        mapStats.append(animals.size());
        mapStats.append(',');
        mapStats.append(getPlantCount());
        mapStats.append(',');
        mapStats.append(getAverageEnergy());
        mapStats.append(',');
        mapStats.append(getAverageDaysLived());
        mapStats.append(',');
        mapStats.append(getAverageChildrenCount());
        mapStats.append("\n");
    }

    private void updateSums(){
        animalSum+=animals.size();
        plantSum+=getPlantCount();
        avgEnergySum+=getAverageEnergy();
        avgLifetimeSum+=getAverageDaysLived();
        avgChildrenSum+=getAverageChildrenCount();
    }

    public StringBuilder getStats(){
        StringBuilder result=new StringBuilder(mapStats.toString());
        result.append("Average: ");
        result.append(',');
        result.append(animalSum/dayCount);
        result.append(',');
        result.append(plantSum/dayCount);
        result.append(',');
        result.append(avgEnergySum/dayCount);
        result.append(',');
        result.append(avgLifetimeSum/dayCount);
        result.append(',');
        result.append(avgChildrenSum/dayCount);

        return result;
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

    public ArrayList<int[]> getTopGenotypeArray(){
        int currMax=0;
        ArrayList result=new ArrayList();
        int[] topGenotype = new int[0];
        for(int[] genotype:genotypeCounter.keySet()){
            if (genotypeCounter.get(genotype)>currMax){
                result.clear();
                currMax=genotypeCounter.get(genotype);
            }
            if (genotypeCounter.get(genotype)==currMax){
                result.add(genotype);
            }
        }
        return result;
    }

    public String getTopGenotype(){
        String result="";
        for (int[] genotype:getTopGenotypeArray()){
            result+=Arrays.toString(genotype)+"\n";
        }
        return result;
    }

    public int getAnimalCount(){return animals.size();}

    public double getAverageDaysLived(){
        if (deadAnimalsCount>=1)
            return  (double)daysLivedSum/(double)deadAnimalsCount;
        return 0;
    }

    public double getAverageChildrenCount(){
        int sum=0;
        for(Animal animal:animals){
            sum+=animal.getChildrenCount();
        }
        return (double)sum/(double)animals.size();
    }


    public void setAnimal(Animal animal){
        trackedAnimal=animal;
        setDescendantCount();
    }
    private void setDescendantCount(){
        int counter=0;
        for(Animal animal:animals){
            if (animal.isDescendantOf(trackedAnimal)){
                counter++;
            }
        }
        descendantCount=counter;
    }
    public void addDescendant(){
        descendantCount++;
    }

    public boolean isTracked(Animal animal){return animal==trackedAnimal;}
    public boolean isAnimalTracked(){return trackedAnimal!=null;}
    public Object objectAt(Vector2d position){
        return mapCells.get(position);
    }

    public String getMagicalLabel(){
        if (isMagical)
            return "Magical evolutions left: "+magicalCount;
        return "";
    }

    public void highlightTopGenome(){topGenomeHighlighted=!topGenomeHighlighted;}
    public boolean isTopGenomeHighlighted(){return topGenomeHighlighted;}
    public int getDescendantCount(){return descendantCount;}
    public int getTrackedChildrenCount(){return trackedAnimal.getChildrenCount();}
    public int getTrackedDeathDay(){return trackedAnimal.getDeathDay();}
    public String getTrackedGenes(){return Arrays.toString(trackedAnimal.getGenes());}
    public Animal getTrackedAnimal(){return trackedAnimal;}
    public int getPlantCount(){return plantCount;}
    public int getDayCount(){return dayCount;}
    public abstract Vector2d getMoveVector(Vector2d oldPosition, Vector2d newPosition);
    public boolean isOccupied(Vector2d pos){return mapCells.get(pos)!=null;}
}
