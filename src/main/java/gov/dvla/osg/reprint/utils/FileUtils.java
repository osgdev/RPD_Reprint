package gov.dvla.osg.reprint.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

	/**
	 * Files that are not sucessfully sent to RPD remain in the working directory.
	 * Files that are not manually moved or deleted by Dev team are automatically
	 * deleted after the given number of days.
	 * @param workingDir directory files are saved to before sending to RPD
	 * @param daysBack
	 */
	public static void deleteFilesOlderThanNdays(String workingDir, int daysBack) {

		int dayInMilis = 24 * 60 * 60 * 1000;

		long purgeTime = System.currentTimeMillis() - (daysBack * dayInMilis);

		try {
			Files.list(Paths.get(workingDir))
			.filter(p -> p.toFile().lastModified() < purgeTime)
			.forEach(p -> {
				if (p.toString().endsWith(".DAT") || p.toString().endsWith(".EOT")) {
					try {
						Files.deleteIfExists(p);
					} catch (IOException e) {
						System.err.println("Unable to delte file:- " + p.getFileName());
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Suppress default constructor for noninstantiability
	private FileUtils() {
		throw new AssertionError();
	}
}
