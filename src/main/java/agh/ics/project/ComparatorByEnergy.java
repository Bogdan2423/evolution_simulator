package agh.ics.project;

import java.util.Comparator;

public class ComparatorByEnergy implements Comparator<Animal> {

    public int compare(Animal a1, Animal a2){
        if (a1.energy<a2.energy)
            return 1;
        else if (a1.energy== a2.energy)
            return -1;
        return -1;
    }
}
