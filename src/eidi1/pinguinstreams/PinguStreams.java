package eidi1.pinguinstreams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PinguStreams {

    public static String path = "penguins.csv";

    public static Stream<String> lines() {
        try {
            return Files.lines(Path.of(path));
        } catch (IOException e) {
            return Stream.empty();
        }
    }

    public static Stream<Penguin> penguins() {
        List<Penguin> result = new ArrayList<>();
        lines().skip(1).map(x -> {
            if (x.equals(";")){
                x = " ";
            }
            return x;
        }).forEach(x -> {
            result.add(new Penguin(x.split(" ")));
        });
        return result.stream();
    }

    public static Stream<Penguin> filter(Stream<Penguin> stream, String field, String value) {
        return stream.filter(x -> x.get(field).equals(value));

    }

    public static Stream<String> getFeature(Stream<Penguin> stream, String field) {
        return stream.map(p -> p.get(field));
    }

    public static double getAverage(Stream<Penguin> stream, String field) {
        return stream.filter(x -> !x.get(field).equals("NA"))
                .mapToDouble(x -> x.getDouble(field))
                .average()
                .orElse(0);

    }

    public static double getAverageOfSpecies(String species, String feature) {
        Stream<Penguin> penguins = filter(penguins(), "species", species);
        return getAverage(penguins, feature);
    }

    public static double distance(Penguin penguin, String species) {
        double avgBillLen = getAverageOfSpecies(species, "bill_depth_mm");
        double avgFlipperLen = getAverageOfSpecies(species, "body_mass_g");
        double billLen = penguin.getDouble("bill_depth_mm");
        double flipperLen = penguin.getDouble("body_mass_g");
        return (Math.abs(avgBillLen - billLen) + Math.abs(avgFlipperLen - flipperLen))/2;
    }

    public static String classifyPenguin(Penguin penguin) {
        double min = Double.MAX_VALUE;
        String classifiedSpecies = "";
        String[] ar = {"Adelie", "Gentoo", "Chinstrap"};
        for (String species: ar) {
            double d = distance(penguin, species);
            if (min > d) {
                min = d;
                classifiedSpecies = species;
            }
        }
        return classifiedSpecies;
    }

    public static void main(String[] args) {
        String[] pingu = "Chinstrap,Dream,50.9,17.9,196,3675,female,2009".split(",");
        System.out.println(classifyPenguin(new Penguin(pingu)));
        System.out.println(accuracy());
    }

    public static double accuracy() {
        return penguins()
                .filter(p -> !p.get("bill_depth_mm").equals("NA") && !p.get("body_mass_g").equals("NA"))
                .map(p -> p.get("species").equals(classifyPenguin(p)))
                .mapToDouble(p -> p?1:0)
                .average()
                .orElse(0);
    }

}
