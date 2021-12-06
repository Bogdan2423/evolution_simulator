package agh.ics.project;

public class UnboundMap extends AbstractMap{
    UnboundMap(int x, int y) {
        super(x, y);
    }

    @Override
    public Vector2d getMoveVector(Vector2d oldPosition, Vector2d newPosition) {
        if (newPosition.follows(lowBoundary) && newPosition.precedes(upBoundary))
            return newPosition;

        int x= newPosition.x;
        int y= newPosition.y;
        if (x< lowBoundary.x)
            x= upBoundary.x;
        else if (x> upBoundary.x)
            x= lowBoundary.x;
        if (y< lowBoundary.y)
            y= upBoundary.y;
        else if (y> upBoundary.y)
            y= lowBoundary.y;

        return new Vector2d(x,y);
    }
}
