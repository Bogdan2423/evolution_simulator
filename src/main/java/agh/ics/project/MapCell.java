package agh.ics.project;

import java.util.*;

import static java.lang.System.out;

public class MapCell {
    private SortedSet<Animal> animals=new TreeSet<>(new ComparatorByEnergy());
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
            Iterator<Animal> maxAnimals=animals.iterator();
            Animal currAnimal=maxAnimals.next();
            int counter=1;
            int maxEnergy=currAnimal.energy;
            while (maxAnimals.hasNext()){
                if (maxAnimals.next().energy==maxEnergy)
                    counter++;
                else
                    break;
            }
            int currPlantEnergy=Math.round(plantEnergy/counter);
            Iterator<Animal> maxAnimals1=animals.iterator();
            while(counter>0){
                maxAnimals1.next().energy+=currPlantEnergy;
                counter--;
            }
            hasPlant=false;
        }
    }

    public void breed(){
        if (animals.size()>=2){
            Iterator<Animal> iterator=animals.iterator();
            Animal parent1= iterator.next();
            Animal parent2= iterator.next();
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

                Animal child=new Animal(map,position,childEnergy,childGenes);
                addAnimal(child);
                map.animalBorn(child);
            }
        }
    }

    public void addAnimal(Animal animal){
        animals.add(animal);
    }

    public void removeAnimal(Animal animal){
        animals.remove(animal);
    }

    public void addPlant(){
        hasPlant=true;
    }

    public boolean isEmpty(){
        return (animals.isEmpty() && !hasPlant);
    }

    public boolean hasPlant(){return hasPlant;}

    public Vector2d getPosition(){return position;}

    public SortedSet getAnimals(){return animals;}
}
