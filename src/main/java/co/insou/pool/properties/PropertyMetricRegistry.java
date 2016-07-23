package co.insou.pool.properties;

import com.zaxxer.hikari.HikariConfig;

public class PropertyMetricRegistry implements HikariProperty {

    private final Object value;

    public PropertyMetricRegistry(Object value) {
        this.value = value;
    }

    @Override
    public void applyTo(HikariConfig config) {
        config.setMetricRegistry(value);
    }

}
