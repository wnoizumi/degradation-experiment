package br.pucrio.opus.smells.filechanges;

import br.pucrio.opus.smells.resources.Type;

public class TypeChangesHolder implements Comparable<TypeChangesHolder> {

	private Type type;
	private int numberOfChanges;
	String filePath;

	public TypeChangesHolder(Type type) {
		this.type = type;
		this.numberOfChanges = 0;
		//Necessario para comparar com os caminhos retornados pelo git
		this.filePath = type.getAbsoluteFilePath().replaceAll("\\\\", "/");
	}

	public Type getType() {
		return this.type;
	}

	public int getNumberOfChanges() {
		return this.numberOfChanges;
	}

	public void incrementNumberOfChanges() {
		this.numberOfChanges += 1;
	}

	@Override
	public int compareTo(TypeChangesHolder o) {
		return Integer.compare(this.numberOfChanges, o.numberOfChanges);
	}

	public String getFilePath() {
		return this.filePath;
	}
}
