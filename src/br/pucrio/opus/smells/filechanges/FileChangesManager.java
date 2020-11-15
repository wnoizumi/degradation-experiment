package br.pucrio.opus.smells.filechanges;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.pucrio.opus.smells.resources.Type;

public class FileChangesManager {

	List<TypeChangesHolder> changes;

	public FileChangesManager(List<Type> allTypes) throws IOException {
		changes = new ArrayList<>();
		for (Type type : allTypes) {
			changes.add(new TypeChangesHolder(type));
		}
	}

	public void incrementChangesOf(String filePath) {
		//TODO: make this implementation more efficient
		for (TypeChangesHolder t : changes) {
			if (t.getFilePath().contains(filePath)) {
				t.incrementNumberOfChanges();
				break;
			}
		}
	}

	public List<TypeChangesHolder> getSortedListOfFileChanges() {
		Collections.sort(changes, Collections.reverseOrder());
		return changes;
	}
}
