package rs2.cache;

/**
 * A class holding the file containers.
 * @author Dragonkk
 * 
 */
public final class FilesContainer extends ContainerSegment {
	
	/**
	 * The file indexes.
	 */
	private int[] filesIndexes;
	
	/**
	 * The files.
	 */
	private ContainerSegment[] files;
	
	/**
	 * Construct a new files container.
	 */
	public FilesContainer() {
	
	}

	/**
	 * Set the files.
	 * @param containers The files.
	 */
	public void setFiles(ContainerSegment[] containers) {
		this.files = containers;
	}

	/**
	 * Get the files.
	 * @return The files.
	 */
	public ContainerSegment[] getFiles() {
		return files;
	}

	/**
	 * Set the file indexes.
	 * @param containersIndexes The file indexes.
	 */
	public void setFilesIndexes(int[] containersIndexes) {
		this.filesIndexes = containersIndexes;
	}

	/**
	 * Get the file indexes.
	 * @return The file indexes.
	 */
	public int[] getFilesIndexes() {
		return filesIndexes;
	}
}
