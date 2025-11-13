package org.jedi_bachelor.math;

public class MathDistributionImpl implements MathDistribution {
    @Override
    public Float x(int a, int c, int oldRating) {
        double template = (double) c /125;
        return (float) ((1.2f*a + 0.3f*template)*Math.log(oldRating));
    }

    @Override
    public Integer newRating(float x) {
        return (int) ((int) 2.3*x);
    }
}
