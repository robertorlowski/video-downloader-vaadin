package pl.luxtech.movie.common;

public enum VideoViewEnum {
    
    YOUTOBE("youtobe"),
    SETTINGS("settings"),
    HISTORY("history");
    
    private String name;
    
    private VideoViewEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
