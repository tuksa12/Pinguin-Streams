package eidi1.pinguinstreams;

import java.util.Arrays;

public class Penguin {
    String[] header = {"species", "island", "bill_length_mm", "bill_depth_mm", "flipper_length_mm", "body_mass_g", "sex", "year"};
    String[] params;

    public Penguin(String[] params) {
        this.params = params;
    }
    public String get(String field) {
        String str = params[Arrays.asList(header).indexOf(field)];
        return str;
    }
    public Double getDouble(String field) {
        return Double.parseDouble(get(field));
    }
}
