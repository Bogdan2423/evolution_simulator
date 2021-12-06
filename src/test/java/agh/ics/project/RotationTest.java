package agh.ics.project;

import org.junit.jupiter.api.Test;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RotationTest {
    @Test
    public void rotationTest()
    {
        Rotation testRot=Rotation.N;
        assertEquals(testRot.rotateBy(0),Rotation.N);
        assertEquals(testRot.rotateBy(1),Rotation.NE);
        assertEquals(testRot.rotateBy(2),Rotation.E);
        assertEquals(testRot.rotateBy(3),Rotation.SE);
        assertEquals(testRot.rotateBy(4),Rotation.S);
        assertEquals(testRot.rotateBy(5),Rotation.SW);
        assertEquals(testRot.rotateBy(6),Rotation.W);
        assertEquals(testRot.rotateBy(7),Rotation.NW);
        assertEquals(testRot.rotateBy(8),Rotation.N);
        assertEquals(testRot.rotateBy(11),Rotation.SE);

        Rotation testRot1=Rotation.SE;
        assertEquals(testRot1.rotateBy(2),Rotation.SW);
        assertEquals(testRot1.rotateBy(-2),Rotation.NE);
    }
}
