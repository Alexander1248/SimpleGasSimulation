package ru.alexander.gpu.simulation;

import ru.alexander.gpu.cuda.PointedVectorList;
import ru.alexander.vector.Vector2;

public class ParticleList {
    private final PointedVectorList position = new PointedVectorList();
    private final PointedVectorList speed = new PointedVectorList();

    public void add(Vector2 position, Vector2 speed) {
        this.position.add(position);
        this.speed.add(speed);
    }

    public void set(int index, Vector2 position, Vector2 speed) {
        this.position.set(index, position);
        this.speed.set(index, speed);
    }
    public void remove(int index) {
        position.remove(index);
        speed.remove(index);
    }


    public PointedVectorList getPosition() {
        return position;
    }

    public PointedVectorList getSpeed() {
        return speed;
    }
}
