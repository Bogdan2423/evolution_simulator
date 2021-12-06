package agh.ics.project;

import java.util.Comparator;

public class ComparatorByEnergy implements Comparator<Animal> {

    public int compare(Animal a1, Animal a2){
        if (a1.getEnergy()<a2.getEnergy())
            return -1;
        else if (a1.getEnergy()== a2.getEnergy())
            return 0;
        return 1;
    }
}
