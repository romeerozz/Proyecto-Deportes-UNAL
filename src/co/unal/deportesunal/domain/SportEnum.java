package co.unal.deportesunal.domain;

public enum SportEnum {
    FUTBOL("Fútbol"),
    FUTBOL_SALA("Fútbol sala"),
    BALONCESTO("Baloncesto"),
    VOLEIBOL("Voleibol"),
    RUGBY("Rugby"),
    TAEKWONDO("Taekwondo"),
    NATACION("Natación"),
    ATLETISMO("Atletismo"),
    CICLISMO("Ciclismo"),
    TENIS("Tenis"),
    TENIS_MESA("Tenis de mesa"),
    BADMINTON("Badminton"),
    AJEDREZ("Ajedrez"),
    JUDO("Judo"),
    KARATE("Karate"),
    BOXEO("Boxeo"),
    GIMNASIO("Gimnasio"),
    CALISTENIA("Calistenia"),
    ESCALADA("Escalada"),
    ULTIMATE("Ultimate");

    private final String DISPLAY_NAME;

    SportEnum(String displayName){
        this.DISPLAY_NAME = displayName;
    }

    public String displayName(){
        return DISPLAY_NAME;
    }
}


