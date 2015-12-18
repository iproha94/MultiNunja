package com.wordpress.ilyaps.multiNunjaGamemech;

import com.wordpress.ilyaps.resourceSystem.Resource;

/**
 * Created by ilya on 18.12.15.
 */
public class MultiNunjaResource implements Resource {
    private int maxFruit;
    private int periodGenerate;

    public int getMaxFruit() {
        return maxFruit;
    }

    public void setMaxFruit(int maxFruit) {
        this.maxFruit = maxFruit;
    }

    public int getPeriodGenerate() {
        return periodGenerate;
    }

    public void setPeriodGenerate(int periodGenerate) {
        this.periodGenerate = periodGenerate;
    }
}
