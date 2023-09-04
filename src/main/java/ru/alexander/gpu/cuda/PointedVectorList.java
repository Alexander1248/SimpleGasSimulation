package ru.alexander.gpu.cuda;


import ru.alexander.vector.Vector2;

public class PointedVectorList {
    private final PointedList x;
    private final PointedList y;

    public PointedVectorList() {
        x = new PointedList();
        y = new PointedList();
    }

    public void add(Vector2 vector) {
        x.add(vector.x);
        y.add(vector.y);
    }
    public void remove(int index) {
        x.remove(index);
        y.remove(index);
    }

    public Vector2 get(int index) {
        return new Vector2(x.get(index), y.get(index));
    }
    public void set(int index, Vector2 vector) {
        x.set(index, vector.x);
        y.set(index, vector.y);
    }

    public PointedList getX() {
        return x;
    }

    public PointedList getY() {
        return y;
    }
}
