package br.pucrio.opus.smells.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class JavaFilesFinder {

	private static final String[] EXTENSIONS = { "java" };

	private List<String> directories;
	private Set<String> foldersToSkip = new HashSet<>();

	public JavaFilesFinder() {
		this.directories = new ArrayList<>();
		loadFoldersToSkip();
	}

	public JavaFilesFinder(String sourcePath) {
		this.directories = Arrays.asList(sourcePath);
		loadFoldersToSkip();
	}

	private void loadFoldersToSkip() {
		try (BufferedReader reader = new BufferedReader(new FileReader("folders-filter.config"))) {

			String line;
			while ((line = reader.readLine()) != null) {
				foldersToSkip.add(line);
			}

		} catch (IOException e) {
			System.out.println("Unable to load folders to skip.");
		}
	}

	public JavaFilesFinder(List<String> sourcePaths) {
		this.directories = sourcePaths;
		loadFoldersToSkip();
	}

	public void addDir(String directory) {
		this.directories.add(directory);
	}

	public String[] getSourcePaths() {
		String[] sourcePathsArray = new String[this.directories.size()];
		this.directories.toArray(sourcePathsArray);
		return sourcePathsArray;
	}

	public List<File> findAll() {
		List<File> files = new ArrayList<>();
		for (String dir : this.directories) {
			Collection<File> tempFiles = FileUtils.listFiles(new File(dir), EXTENSIONS, true);
			files.addAll(filterFolders(tempFiles));
		}
		return files;
	}

	private Collection<? extends File> filterFolders(Collection<File> tempFiles) {
		HashSet<File> selectedFiles = new HashSet<>();
		
		for (File file : tempFiles) {
			boolean shouldSkip = false;
			for (String fs : foldersToSkip) {
				if (file.getAbsolutePath().contains(fs)) {
					shouldSkip = true;
					break;
				}
			}
			if (!shouldSkip) {
				selectedFiles.add(file);
			}
		}
		
		return selectedFiles;
	}

}
