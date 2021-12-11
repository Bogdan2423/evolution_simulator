package agh.ics.project;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.System.out;

public class World {
    public static void main(String[] args) {
        int startEnergy=50;
        Vector2d position=new Vector2d(2,2);
        UnboundMap map=new UnboundMap(20,10, startEnergy,20,0.25,5);
        Animal animal=new Animal(map, position,startEnergy);
        map.placeAnimal(new Vector2d(2,2),animal);
        Animal animal2=new Animal(map, position,startEnergy);
        map.placeAnimal(new Vector2d(2,2),animal2);

        map.breedAtPos(position);
        MapCell cell= (MapCell) map.objectAt(position);

        Animal child= (Animal) cell.getAnimals().last();

        map.newDay();
        map.newDay();
        map.newDay();
        map.newDay();
        map.newDay();
        map.newDay();
        out.print("\n");
        out.print(animal.energy);
        out.print("\n");
        out.print(animal2.energy);
        out.print("\n");
        out.print(child.energy);
        out.print("\n");
        out.print(animal.getPosition());
        out.print(animal.getRotation());
        out.print("\n");
        out.print(animal2.getPosition());
        out.print(animal2.getRotation());
        out.print("\n");
        out.print(child.getPosition());
        out.print(child.getRotation());

    }
}
