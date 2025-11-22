package ajav.model.artifact;

public abstract class Artifact {
    private String name;
    private ArtifactType type;
    private String description;
    private int value;


    public String getName() {
        return name;
    }

    public ArtifactType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setType(ArtifactType type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
