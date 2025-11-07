package org.jedi_bachelor.math;

public class MathDistributionImpl implements MathDistribution {
    @Override
    public Float x(int n, int nAll, int a) {
        return 0f;
    }

    @Override
    public Float rating(float x) {
        if(x >= 0 && x <= 5)
            return (float) ((-5/2) * Math.cos(Math.PI * x / 5) + 5/2);
        else if(x < 0)
            return 0.0f;
        else
            return 5.0f;
    }
}
