package ru.alexander.gpu.simulation;

import ru.alexander.gpu.cuda.PointedVectorList;
import ru.alexander.vector.Vector2;

public class AcceleratorList {
    private final PointedVectorList position1 = new PointedVectorList();
    private final PointedVectorList position2 = new PointedVectorList();
    private final PointedVectorList speed = new PointedVectorList();

    public void add(Vector2 pos1, Vector2 pos2, Vector2 speed) {
        position1.add(pos1);
        position2.add(pos2);
        this.speed.add(speed);
    }

    public void set(int index, Vector2 pos1, Vector2 pos2, Vector2 speed) {
        position1.set(index, pos1);
        position2.set(index, pos2);
        this.speed.set(index, speed);
    }
    public void remove(int index) {
        position1.remove(index);
        position2.remove(index);
        speed.remove(index);
    }


    public PointedVectorList getPosition1() {
        return position1;
    }

    public PointedVectorList getPosition2() {
        return position2;
    }

    public PointedVectorList getSpeed() {
        return speed;
    }
}
