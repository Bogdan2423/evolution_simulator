package agh.ics.project;
import java.util.*;

public class MapCell {
    private ArrayList<Animal> animals=new ArrayList<>();
    private boolean hasPlant;
    private int plantEnergy;
    private int startEnergy;
    private Random rand=new Random();
    private AbstractMap map;
    private Vector2d position;

    MapCell(int plantEnergy,int startEnergy, AbstractMap map, Vector2d position){
        this.plantEnergy=plantEnergy;
        this.startEnergy=startEnergy;
        this.map=map;
        this.position=position;
    }


    public void eatPlant(){
        if (hasPlant && !animals.isEmpty())
        {
            Animal currAnimal=animals.get(0);
            int counter=1;
            int maxEnergy=currAnimal.energy;
            int i=1;
            while (i<animals.size() && animals.get(i).energy==maxEnergy){
                counter++;
                i++;
            }
            int currPlantEnergy=Math.round(plantEnergy/counter);
            i=0;
            while(counter>0){
                animals.get(i).energy+=currPlantEnergy;
                counter--;
                i++;
            }
            map.plantEaten();
            hasPlant=false;
        }
    }

    public void breed(){
        if (animals.size()>=2){
            Animal parent1= animals.get(0);
            Animal parent2= animals.get(1);

            if (parent1.energy>=Math.round(startEnergy/2) && parent2.energy>=Math.round(startEnergy/2)){
                double energyRatio=(double)parent1.energy/((double)parent1.energy+(double)parent2.energy);
                int divisionPoint= (int) Math.round(32*energyRatio);

                int side= rand.nextInt(2);
                int[] slice1;
                int[] slice2;
                if (side==0)
                {
                    slice1=Arrays.copyOfRange(parent1.getGenes(),0,divisionPoint);
                    slice2=Arrays.copyOfRange(parent2.getGenes(),divisionPoint,32);
                }
                else
                {
                    slice1=Arrays.copyOfRange(parent2.getGenes(),0,32-divisionPoint);
                    slice2=Arrays.copyOfRange(parent1.getGenes(),32-divisionPoint,32);
                }
                int[] childGenes= new int[32];
                int i=0;
                for(int el:slice1){
                    childGenes[i]=el;
                    i++;
                }
                for(int el:slice2){
                    childGenes[i]=el;
                    i++;
                }
                Arrays.sort(childGenes);
                int childEnergy= (int) Math.round(parent1.energy*0.25+parent2.energy*0.25);
                parent1.energy= (int) Math.round(parent1.energy*0.75);
                parent2.energy= (int) Math.round(parent2.energy*0.75);
                parent1.newChild();
                parent2.newChild();
                if (parent1.isDescendantOf(map.getTrackedAnimal())|| parent2.isDescendantOf(map.getTrackedAnimal()))
                    map.addDescendant();

                Animal child=new Animal(map,position,childEnergy,childGenes,parent1,parent2);
                map.placeAnimal(position,child);
            }
        }
    }

    public void addAnimal(Animal animal){
        animals.add(animal);
        animals.sort(new ComparatorByEnergy());
    }

    public void removeAnimal(Animal animal){
        animals.remove(animal);
        animals.sort(new ComparatorByEnergy());
    }

    public void addPlant(){
        hasPlant=true;
    }

    public boolean isEmpty(){
        return (animals.isEmpty() && !hasPlant);
    }

    public Vector2d getPosition(){return position;}

    public String getImagePath() {
        if (hasPlant && animals.isEmpty())
            return ("src/main/resources/grass.png");
        else if (map.isTracked(getTopAnimal()))
            return ("src/main/resources/trackedAnimal.png");
        else if (map.isTopGenomeHighlighted()) {
            for (int[] genotype:map.getTopGenotypeArray()) {
                if (Arrays.equals(getTopAnimal().getGenes(),genotype))
                    return ("src/main/resources/highlightedAnimal.png");
            }
        }
        return ("src/main/resources/animal.png");
    }
    public String getLabel() {
        if (hasPlant && animals.isEmpty())
            return null;
        else if (!animals.isEmpty())
            return (""+animals.get(0).energy);
        return("");
    }

    public Animal getTopAnimal(){
        return this.animals.get(0);
    }
}
