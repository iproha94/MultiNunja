package com.wordpress.ilyaps.multiNunjaGamemech;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Created by ilya on 18.12.15.
 */
public class Fruit {
    private int a;
    private int b;
    private int c;
    private int id;
    @NotNull
    private static final Random RAND = new Random();

    public void generateFruit(int id) {
        this.id = id;
        a = RAND.nextInt(200) - 100;
        b = RAND.nextInt(200) - 100;
        c = RAND.nextInt(100);

        if (a > 0) {
            c *= -1;
        }
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
