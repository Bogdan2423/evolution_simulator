package agh.ics.project;

public enum Rotation {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    public Vector2d toUnitVector(){
        switch (this){
            case N:
                return new Vector2d(0,1);
            case NE:
                return new Vector2d(1,1);
            case E:
                return new Vector2d(1,0);
            case SE:
                return new Vector2d(1,-1);
            case S:
                return new Vector2d(0,-1);
            case SW:
                return new Vector2d(-1,-1);
            case W:
                return new Vector2d(-1,0);
            case NW:
                return new Vector2d(-1,1);
        }
        return new Vector2d(0,0);
    }

    public Rotation rotateBy(int x){
        int ord;
        switch (this){
            case N:
                ord=0;
                break;
            case NE:
                ord=1;
                break;
            case E:
                ord=2;
                break;
            case SE:
                ord=3;
                break;
            case S:
                ord=4;
                break;
            case SW:
                ord=5;
                break;
            case W:
                ord=6;
                break;
            case NW:
                ord=7;
                break;
            default:
                ord=0;
        }
        int i=(ord+x)%8;
        switch (i) {
            case 0:
                return Rotation.N;
            case 1:
                return Rotation.NE;
            case 2:
                return Rotation.E;
            case 3:
                return Rotation.SE;
            case 4:
                return Rotation.S;
            case 5:
                return Rotation.SW;
            case 6:
                return Rotation.W;
            case 7:
                return Rotation.NW;
        }
        return Rotation.N;
    }
}
