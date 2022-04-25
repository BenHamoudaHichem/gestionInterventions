package com.app.gestionInterventions.services.statistics;

public class PairCustom {
    private  String key;
    private Long value;

    public PairCustom(String key, long value) {
        this.key = key;
        this.value = value;
    }
    public PairCustom(String key, Integer value) {
        this.key = key;
        this.value = value.longValue();
    }
    public PairCustom(String key, Float value) {
        this.key = key;
        this.value = value.longValue();
    }
    public PairCustom(String key, Double value) {
        this.key = key;
        this.value = value.longValue();
    }
    public void increment()
    {
        this.value= this.value+1;
    }

    public String getKey() {
        return key;
    }

    public long getValue() {
        return value;
    }
}
