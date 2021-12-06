package agh.ics.project;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class MapCell {
    SortedSet<Animal> animals=new TreeSet<>(new ComparatorByEnergy());
    Plant plant;

    public void addAnimal(Animal animal){
        animals.add(animal);
    }

    public void removeAnimal(Animal animal){
        animals.remove(animal);
    }

    public boolean addPlant(Plant plant){
        if (this.plant!=null)
            return false;
        this.plant=plant;
        return true;
    }

    public boolean isEmpty(){
        return (animals.isEmpty() && plant==null);
    }
}
