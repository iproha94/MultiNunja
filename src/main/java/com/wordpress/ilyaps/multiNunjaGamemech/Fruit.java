package com.wordpress.ilyaps.multiNunjaGamemech;

import java.util.Random;

/**
 * Created by ilya on 18.12.15.
 */
public class Fruit {
    private int a;
    private int b;
    private int c;
    private int id;
    private static Random rand = new Random();


    public void generateFruit(int id) {
        a = rand.nextInt(200) - 100;
        b = rand.nextInt(200) - 100;
        c = rand.nextInt(200) - 100;
        this.id = id;
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
