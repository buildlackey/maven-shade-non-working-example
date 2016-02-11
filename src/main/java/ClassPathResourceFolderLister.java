import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * http://www.uofr.net/~greg/java/get-resource-listing.html
 */

/**
 * List entries of a subfolder of an entry in the class path, which may consist of file system folders and .jars.
 */
public class ClassPathResourceFolderLister {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathResourceFolderLister.class);

  /**
   * For each entry in the classpath, verify that (a) "folder" exists, and (b) "folder" has child content, and if
   * these conditions hold,  return the child entries (be they files, or folders).  If neither (a) nor (b) are true for
   * a particular class path entry, move on to the next entry and try again.
   *
   * @param folder the folder to match within the class path entry
   * @return the subfolder items of the first matching class path entry, with a no duplicates guarantee
   */
  public static Collection<String> getFolderListing(final String folder) {
    final String classPath = System.getProperty("java.class.path", ".");
    final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
    List<String> classPathElementsList = new ArrayList<String>(Arrays.asList(classPathElements));

    return getFolderListingForFirstMatchInClassPath(folder, classPathElementsList);
  }

  // not private so we can unit test
  static Collection<String> getFolderListingForFirstMatchInClassPath(final String folder,
                                                                     List<String> classPathElementsList) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "getFolderListing for " + folder + " with classpath elements " + classPathElementsList);
    }

    Collection<String> retval = new HashSet<String>();
    String cleanedFolder = stripTrailingAndLeadingSlashes(folder);
    for (final String element : classPathElementsList) {
      System.out.println("class path element:" + element);
      retval = getFolderListingFromJarOrDirectory(element, cleanedFolder);

      if (retval.size() > 0) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("found matching folder in class path list. returning: " + retval);
        }
        return retval;
      }
    }
    return retval;
  }

  private static String stripTrailingAndLeadingSlashes(final String folder) {
    String stripped = folder;

    if (stripped.equals("/")) {  // handle degenerate case:
      return "";
    } else { // handle cases for strings starting or ending with "/", confident that we have at least two characters
      if (stripped.endsWith("/")) {
        stripped = stripped.substring(0, stripped.length() - 1);
      }
      if (stripped.startsWith("/")) {
        stripped = stripped.substring(1, stripped.length());
      }
      if (stripped.startsWith("/") || stripped.endsWith("/")) {
        throw new IllegalArgumentException("too many consecutive slashes in folder specification: " + stripped);
      }
    }

    return stripped;
  }

  private static Collection<String> getFolderListingFromJarOrDirectory(final String element, final String folderName) {
    final File file = new File(element);
    if (file.isDirectory()) {
      return getFolderContentsListingFromSubfolder(file, folderName);
    } else {
      return getResourcesFromJarFile(file, folderName);
    }
  }

  private static Collection<String> getResourcesFromJarFile(final File file, final String folderName) {
    final String leadingPathOfZipEntry = folderName + "/";
    final HashSet<String> retval = new HashSet<String>();
    ZipFile zf = null;
    try {
      zf = new ZipFile(file);
      final Enumeration e = zf.entries();
      while (e.hasMoreElements()) {
        final ZipEntry ze = (ZipEntry) e.nextElement();
        final String fileName = ze.getName();
        if (fileName.startsWith(leadingPathOfZipEntry)) {
          final String justLeafPartOfEntry = fileName.replaceFirst(leadingPathOfZipEntry, "");
          final String initSegmentOfPath = justLeafPartOfEntry.replaceFirst("/.*", "");
          if (initSegmentOfPath.length() > 0) {
            if (LOGGER.isTraceEnabled()) {
              LOGGER.trace("adding:" + initSegmentOfPath);
            }
            retval.add(initSegmentOfPath);
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("getResourcesFromJarFile failed. file=" + file + " folder=" + folderName, e);
    } finally {
      if (zf != null) {
        try {
          zf.close();
        } catch (IOException e) {
          LOGGER.error("getResourcesFromJarFile close failed. file=" + file + " folder=" + folderName, e);
        }
      }
    }
    return retval;
  }

  private static Collection<String> getFolderContentsListingFromSubfolder(final File directory, String folderName) {
    final HashSet<String> retval = new HashSet<String>();
    try {
      final String fullPath = directory.getCanonicalPath() + "/" + folderName;
      final File subFolder = new File(fullPath);
      System.out.println("fullPath:" + fullPath);
      if (subFolder.isDirectory()) {
        final File[] fileList = subFolder.listFiles();
        for (final File file : fileList) {
          retval.add(file.getName());
        }
      }
    } catch (final IOException e) {
      throw new Error(e);
    }
    return retval;
  }
}
