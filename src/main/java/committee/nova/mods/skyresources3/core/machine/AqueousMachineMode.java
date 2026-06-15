package committee.nova.mods.skyresources3.core.machine;

public enum AqueousMachineMode {
    CONCENTRATOR("aqueous_concentrator", true),
    DECONCENTRATOR("aqueous_deconcentrator", false);

    private final String id;
    private final boolean concentrator;

    AqueousMachineMode(final String id, final boolean concentrator) {
        this.id = id;
        this.concentrator = concentrator;
    }

    public boolean isConcentrator() {
        return this.concentrator;
    }

    public String containerTranslationKey() {
        return "container.skyresources." + this.id;
    }

    public String modeTranslationKey() {
        return "screen.skyresources." + this.id + ".mode";
    }
}
