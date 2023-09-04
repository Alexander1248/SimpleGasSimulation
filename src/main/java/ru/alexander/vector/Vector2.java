package ru.alexander.vector;

public class Vector2 {
    public double x;
    public double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public Vector2 clone() {
        return new Vector2(x, y);
    }

    public static final Vector2 zero = new Vector2(0, 0);
    public static final Vector2 one = new Vector2(1, 1);

    //==========================
    //          Math
    //==========================
    public double length() {
        return Math.sqrt(sqr());
    }
    public double sqr() {
        return x * x + y * y;
    }
    public Vector2 normalise() {
        double l = length();
        x /= l;
        y /= l;
        return this;
    }

    public Vector2 add(Vector2 vec) {
        x += vec.x;
        y += vec.y;
        return this;
    }

    public Vector2 sub(Vector2 vec) {
        x -= vec.x;
        y -= vec.y;
        return this;
    }

    public Vector2 mul(double val) {
        x *= val;
        y *= val;
        return this;
    }

    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }
    public static Vector2 sub(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }
    public static Vector2 mul(Vector2 vec, double val) {
        return new Vector2(vec.x * val, vec.y * val);
    }

    public static double distance(Vector2 a, Vector2 b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    public static double dot(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }
    public static double cross(Vector2 a, Vector2 b) {
        return a.x * b.y - a.y * b.x;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return Vector2.distance(vector2, this) < 1e-5;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y +")";
    }
}
