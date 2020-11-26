package br.pucrio.opus.smells.filechanges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.pucrio.opus.smells.resources.Type;

public class FileChangesManager {

	List<TypeChangesHolder> changes;

	public FileChangesManager(List<Type> allTypes) {
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

	public Map<Type, TypeChangesHolder> getMapOfFileChanges() {
		Map<Type, TypeChangesHolder> map = new HashMap<>();
		
		for (TypeChangesHolder typeChangesHolder : changes) {
			map.put(typeChangesHolder.getType(), typeChangesHolder);
		}
		
		return map;
	}
}
