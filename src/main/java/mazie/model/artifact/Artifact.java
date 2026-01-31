package mazie.model.artifact;

import mazie.model.Quality;
import mazie.model.util.QualityDefiner;

public abstract class Artifact {
    protected String name;
    protected ArtifactType type;
    protected String description;
    protected int value;
    protected Quality quality;

    protected Artifact(String name, String description, int value) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.quality = QualityDefiner.defineQuality();
    }

    public int getValue() {
        return value;
    }
}
