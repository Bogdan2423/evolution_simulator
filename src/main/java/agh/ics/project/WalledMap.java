package agh.ics.project;

public class WalledMap extends AbstractMap{


    WalledMap(int x, int y, int startEnergy, int plantEnergy, double jungleRatio, int moveEnergy) {
        super(x, y, startEnergy, plantEnergy, jungleRatio, moveEnergy);
    }

    @Override
    public Vector2d getMoveVector(Vector2d oldPosition, Vector2d newPosition) {
        if (newPosition.follows(lowBoundary) && newPosition.precedes(upBoundary))
            return newPosition;
        return oldPosition;
    }
}
