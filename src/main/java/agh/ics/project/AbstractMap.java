package agh.ics.project;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractMap {
    protected Vector2d lowBoundary=new Vector2d(0,0);
    protected Vector2d upBoundary;
    protected Map<Vector2d, MapCell> mapCells=new LinkedHashMap<>();

    AbstractMap(int x,int y){
        this.upBoundary=new Vector2d(x,y);
    }
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal){
        mapCells.get(oldPosition).removeAnimal(animal);
        if(mapCells.get(oldPosition).isEmpty())
            mapCells.remove(oldPosition);
        if(mapCells.get(newPosition)==null)
            mapCells.put(newPosition,new MapCell());
        mapCells.get(newPosition).addAnimal(animal);
    }

    public abstract Vector2d getMoveVector(Vector2d oldPosition, Vector2d newPosition);
}
