package br.pucrio.opus.smells.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import br.pucrio.opus.smells.filechanges.TypeChangesHolder;
import br.pucrio.opus.smells.patterns.model.PatternModel;
import br.pucrio.opus.smells.resources.Type;

public class ExperimentalData {

	private HashSet<Type> selectedTypes = new HashSet<>();
	private ArrayList<Case> selectedCases = new ArrayList<>();

	public ExperimentalData(List<Type> allTypes, List<PatternModel> singleSmellPatterns,
			List<PatternModel> multipleSmellsPatterns, Map<Type, TypeChangesHolder> changeData) {
		Collections.sort(multipleSmellsPatterns, comparePatterns(changeData));
		Collections.sort(singleSmellPatterns, comparePatterns(changeData));
		Collections.sort(allTypes, compareTypes(changeData));
		int caseNumber = 1;
		for (PatternModel pattern : multipleSmellsPatterns) {
			if (!selectedTypes.contains(pattern.getRootType())) {
				int numberOfDetectedChanges = 0;
				TypeChangesHolder changesHolder = changeData.get(pattern.getRootType());
				if (changesHolder != null) {
					numberOfDetectedChanges = changesHolder.getNumberOfChanges();
				}
				
				selectedTypes.add(pattern.getRootType());
				getSelectedCases().add(new MultipleSmellsPatternCase(pattern, caseNumber, numberOfDetectedChanges));
				caseNumber++;
				if (caseNumber > 2) {
					break;
				}
			}
		}

		for (PatternModel pattern : singleSmellPatterns) {
			if (!selectedTypes.contains(pattern.getRootType())) {
				int numberOfDetectedChanges = 0;
				TypeChangesHolder changesHolder = changeData.get(pattern.getRootType());
				if (changesHolder != null) {
					numberOfDetectedChanges = changesHolder.getNumberOfChanges();
				}
				
				selectedTypes.add(pattern.getRootType());
				getSelectedCases().add(new SingleSmellsPatternCase(pattern, caseNumber, numberOfDetectedChanges));
				caseNumber++;
			}

			if (caseNumber > 4) {
				break;
			}
		}

		HashSet<Type> typesInPatterns = new HashSet<Type>();
		for (PatternModel p : multipleSmellsPatterns) {
			typesInPatterns.add(p.getRootType());
		}
		for (PatternModel p : singleSmellPatterns) {
			typesInPatterns.add(p.getRootType());
		}

		for (Type type : allTypes) {
			if (!typesInPatterns.contains(type)) {
				int numberOfDetectedChanges = 0;
				TypeChangesHolder changesHolder = changeData.get(type);
				if (changesHolder != null) {
					numberOfDetectedChanges = changesHolder.getNumberOfChanges();
				}
				selectedTypes.add(type);
				getSelectedCases().add(new Case(type, caseNumber, numberOfDetectedChanges));
				caseNumber++;
			}

			if (caseNumber > 6) {
				break;
			}
		}
	}

	private Comparator<? super Type> compareTypes(Map<Type, TypeChangesHolder> changeData) {
		return (o1, o2) -> {
			int result = 0;
			int firstNumSmells = o1.getAllSmells().size();
			int secondNumSmells = o2.getAllSmells().size();
			result = Integer.compare(secondNumSmells, firstNumSmells);

			if (result == 0) {
				TypeChangesHolder o1ChangeData = changeData.get(o1);
				TypeChangesHolder o2ChangeData = changeData.get(o2);
				result = o2ChangeData.compareTo(o1ChangeData);
			}

			return result;
		};
	}

	private Comparator<? super PatternModel> comparePatterns(Map<Type, TypeChangesHolder> changeData) {
		return (o1, o2) -> {
			int result = o2.compareTo(o1);

			if (result == 0) {
				int firstNumSmells = o1.getAllSmells().size();
				int secondNumSmells = o2.getAllSmells().size();
				result = Integer.compare(secondNumSmells, firstNumSmells);
			}
			
			if (result == 0) {
				TypeChangesHolder o1ChangeData = changeData.get(o1.getRootType());
				TypeChangesHolder o2ChangeData = changeData.get(o2.getRootType());
				result = o2ChangeData.compareTo(o1ChangeData);
			}

			return result;
		};
	}

	public ArrayList<Case> getSelectedCases() {
		return selectedCases;
	}
}
