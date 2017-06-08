package io.transwarp.midas;

import io.transwarp.midas.adaptor.operator.IIntervalSeries;
import org.jfree.data.xy.YIntervalSeries;

public class IntervalSeries extends YIntervalSeries implements IIntervalSeries{
    public IntervalSeries(String name){
        super(name);
    }
}
