package ru.alexander.gpu.simulation;

import ru.alexander.gpu.cuda.PointedList;
import ru.alexander.gpu.cuda.PointedVectorList;
import ru.alexander.vector.Vector2;

public class DetectorList {
    private final PointedVectorList position1 = new PointedVectorList();
    private final PointedVectorList position2 = new PointedVectorList();
    private final PointedList velocity = new PointedList();

    public void add(Vector2 pos1, Vector2 pos2) {
        position1.add(pos1);
        position2.add(pos2);
        velocity.add(0);
    }

    public void set(int index, Vector2 pos1, Vector2 pos2) {
        position1.set(index, pos1);
        position2.set(index, pos2);
    }
    public void remove(int index) {
        position1.remove(index);
        position2.remove(index);
        velocity.remove(0);
    }


    public PointedVectorList getPosition1() {
        return position1;
    }

    public PointedVectorList getPosition2() {
        return position2;
    }

    public PointedList getVelocity() {
        return velocity;
    }
}
