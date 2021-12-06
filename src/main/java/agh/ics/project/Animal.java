package agh.ics.project;

public class Animal {
    private Vector2d position;
    private AbstractMap map;
    private int energy;
    private Rotation rotation;
    private int[] genes= new int[9];

    Animal(AbstractMap map, Vector2d initialPosition, int initialEnergy){
        this.map=map;
        this.position=initialPosition;
        this.energy=initialEnergy;
    }

    public void move(int direction) {
        Vector2d oldPosition = this.position;
        Vector2d newPosition = this.position;
        if (direction==0)
            newPosition=oldPosition.add(this.rotation.toUnitVector());
        else if (direction==4)
            newPosition=oldPosition.add(this.rotation.toUnitVector().opposite());
        else
            rotation=rotation.rotateBy(direction);

        this.position=map.getMoveVector(oldPosition,newPosition);
        map.positionChanged(oldPosition,this.position,this);
    }

    public boolean isAt(Vector2d position) {return this.position.equals(position);}

    public Vector2d getPosition(){return this.position;}

    public int getEnergy(){return energy;}
}
