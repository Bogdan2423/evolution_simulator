package agh.ics.project;

public class WalledMap extends AbstractMap{
    WalledMap(int x, int y) {
        super(x, y);
    }

    @Override
    public Vector2d getMoveVector(Vector2d oldPosition, Vector2d newPosition) {
        if (newPosition.follows(lowBoundary) && newPosition.precedes(upBoundary))
            return newPosition;
        return oldPosition;
    }
}
