package ru.alexander.vector;

import java.awt.*;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 clone() {
        return new Vector3(x, y, z);
    }

    public Color toColor() {
        float cx = (float) Math.max(0, Math.min(1, x));
        float cy = (float) Math.max(0, Math.min(1, y));
        float cz = (float) Math.max(0, Math.min(1, z));
        return new Color(cx, cy, cz);
    }
    public static Vector3 fromColor(Color color) {
        return new Vector3((double) color.getRed() / 255, (double) color.getGreen() / 255,(double)  color.getBlue() / 255);
    }

    public static final Vector3 zero = new Vector3(0,0,0);
    public static final Vector3 one = new Vector3(1,1,1);

    //==========================
    //          Math
    //==========================
    public double length() {
        return Math.sqrt(sqr());
    }
    public double sqr() {
        return x * x + y * y + z * z;
    }
    public void normalise() {
        double l = length();
        x /= l;
        y /= l;
        z /= l;
    }

    public Vector3 add(Vector3 vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;
        return this;
    }

    public Vector3 sub(Vector3 vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    public Vector3 mul(double val) {
        x *= val;
        y *= val;
        z *= val;
        return this;
    }

    public static Vector3 add(Vector3 a, Vector3 b) {
        return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    public static Vector3 sub(Vector3 a, Vector3 b) {
        return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    public static Vector3 mul(Vector3 vec, double val) {
        return new Vector3(vec.x * val, vec.y * val, vec.z * val);
    }

    public static double distance(Vector3 a, Vector3 b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
    }

    public static double dot(Vector3 a, Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }
    public static Vector3 cross(Vector3 a, Vector3 b) {
        return new Vector3(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x);
    }

    public static Vector3 reflect(Vector3 dir, Vector3 normal) {
        return Vector3.sub(dir, Vector3.mul(normal, 2 * Vector3.dot(dir, normal)));
    }
    public static Vector3 refract(Vector3 dir, Vector3 normal, double arc1, double arc2) {
        Vector3 arcdir = Vector3.mul(dir, arc1);
        double dot = Vector3.dot(arcdir, normal);
        double value = Math.sqrt((arc2 * arc2 - arc1 * arc1) / (dot * dot) + 1) - 1;
        value *= dot;
        return Vector3.mul(Vector3.add(arcdir, Vector3.mul(normal, value)), 1 / arc2);
    }
    public static double reflectionCoefficientInRefraction(Vector3 dir, Vector3 refraction, Vector3 normal, double arc1, double arc2) {
        double dot = Vector3.dot(normal, normal);
        double cosDir = Vector3.dot(dir, normal) / dot;
        double cosRef = Vector3.dot(refraction, normal) / dot;

        double rs = (arc1 * cosDir - arc2 * cosRef) / (arc1 * cosDir + arc2 * cosRef);
        double rp = (arc2 * cosDir - arc1 * cosRef) / (arc2 * cosDir + arc1 * cosRef);

        return Math.pow((rs + rp) / 2, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Vector3.distance(vector3, this) < 1e-5;
    }
}
