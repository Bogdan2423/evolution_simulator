package agh.ics.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Animal {
    private Vector2d position;
    private AbstractMap map;
    int energy;
    private Rotation rotation;
    private int[] genes= new int[32];
    private ArrayList<IPositionChangeObserver> observers=new ArrayList<>();
    private Random rand=new Random();
    private int daysLived=0;
    private int childrenCount=0;
    private Animal parent1;
    private Animal parent2;
    private int deathDay;

    Animal(AbstractMap map, Vector2d initialPosition, int initialEnergy){
        this.map=map;
        this.position=initialPosition;
        this.energy=initialEnergy;
        int rotation=rand.nextInt(8);
        switch (rotation){
            case 0:
                this.rotation=Rotation.N;
                break;
            case 1:
                this.rotation=Rotation.NE;
                break;
            case 2:
                this.rotation=Rotation.E;
                break;
            case 3:
                this.rotation=Rotation.SE;
                break;
            case 4:
                this.rotation=Rotation.S;
                break;
            case 5:
                this.rotation=Rotation.SW;
                break;
            case 6:
                this.rotation=Rotation.W;
                break;
            case 7:
                this.rotation=Rotation.NW;
                break;
        }
        int i=0;
        while (i<32){
            genes[i]=rand.nextInt(8);
            i++;
        }
        Arrays.sort(genes);
        addObserver(map);
    }

    Animal(AbstractMap map, Vector2d initialPosition, int initialEnergy, int[] genes){
        this.map=map;
        this.position=initialPosition;
        this.energy=initialEnergy;
        int rotation=rand.nextInt(8);
        switch (rotation){
            case 0:
                this.rotation=Rotation.N;
                break;
            case 1:
                this.rotation=Rotation.NE;
                break;
            case 2:
                this.rotation=Rotation.E;
                break;
            case 3:
                this.rotation=Rotation.SE;
                break;
            case 4:
                this.rotation=Rotation.S;
                break;
            case 5:
                this.rotation=Rotation.SW;
                break;
            case 6:
                this.rotation=Rotation.W;
                break;
            case 7:
                this.rotation=Rotation.NW;
                break;
        }
        this.genes=genes;
        addObserver(map);
    }

    Animal(AbstractMap map, Vector2d initialPosition, int initialEnergy, int[] genes, Animal parent1, Animal parent2){
        this.map=map;
        this.position=initialPosition;
        this.energy=initialEnergy;
        int rotation=rand.nextInt(8);
        switch (rotation){
            case 0:
                this.rotation=Rotation.N;
                break;
            case 1:
                this.rotation=Rotation.NE;
                break;
            case 2:
                this.rotation=Rotation.E;
                break;
            case 3:
                this.rotation=Rotation.SE;
                break;
            case 4:
                this.rotation=Rotation.S;
                break;
            case 5:
                this.rotation=Rotation.SW;
                break;
            case 6:
                this.rotation=Rotation.W;
                break;
            case 7:
                this.rotation=Rotation.NW;
                break;
        }
        this.genes=genes;
        this.parent1=parent1;
        this.parent2=parent2;
        addObserver(map);
    }

    public void randomMove(){move(randomizeDirection());}

    private void move(int direction) {
        daysLived++;
        energy-= map.getMoveEnergy();
        if (energy<=0){
            map.animalDead(this);
            deathDay= map.getDayCount();
            return;
        }
        Vector2d oldPosition = this.position;
        Vector2d newPosition = this.position;
        if (direction==0)
            newPosition=oldPosition.add(this.rotation.toUnitVector());
        else if (direction==4)
            newPosition=oldPosition.add(this.rotation.toUnitVector().opposite());
        else
            rotation=rotation.rotateBy(direction);

        this.position=map.getMoveVector(oldPosition,newPosition);
        positionChanged(oldPosition,this.position,this);
    }

    private int randomizeDirection(){
        int i=rand.nextInt(32);
        return genes[i];
    }

    public boolean isAt(Vector2d position) {return this.position.equals(position);}

    public Vector2d getPosition(){return this.position;}

    public Rotation getRotation() {
        return rotation;
    }

    public int getDaysLived(){return daysLived;}

    public int[] getGenes(){return genes;}

    public void newChild(){childrenCount++;}

    public int getChildrenCount(){return childrenCount;}

    public boolean isDescendantOf(Animal animal){
        if (parent1==null && parent2==null)
            return false;
        return parent1==animal||parent2==animal||parent1.isDescendantOf(animal)|| parent2.isDescendantOf(animal);
    }

    public int getDeathDay(){return deathDay;}

    void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal){
        observers.forEach(observer -> observer.positionChanged(oldPosition, newPosition, animal));
    }

    void addObserver(IPositionChangeObserver observer){observers.add(observer);}
    void removeObserver(IPositionChangeObserver observer){observers.remove(observer);}

}
