package mazie.model;

public class Artifact {
	private int id;
	private String name;
	private ArtifactType type;
	private int value;

	public Artifact() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArtifactType getType() {
		return this.type;
	}

	public void setType(ArtifactType type) {
		this.type = type;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
