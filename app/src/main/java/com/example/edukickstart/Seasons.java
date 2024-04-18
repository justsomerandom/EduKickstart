package com.example.edukickstart;

public enum Seasons {
    SUMMER("Summer", new String[]{"very hot", "sunny", "beach"}, 3, true),
    SPRING("Spring", new String[]{"blossoms", "flowers", "colourful"}, 2, false),
    AUTUMN("Autumn", new String[]{"fall", "leaves", "pumpkin"}, 1, false),
    WINTER("Winter", new String[]{"very cold", "snow", "icicle"}, 0, true);

    private final String name;
    private final String[] keywords;
    private final int temperature;
    private final boolean adjacent;

    Seasons(String name, String[] keywords, int temperature, boolean adjacent) {
        this.name = name;
        this.keywords = keywords;
        this.temperature = temperature;
        this.adjacent = adjacent;
    }

    public String getName() {
        return name;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean isAdjacent() {
        return adjacent;
    }
}
