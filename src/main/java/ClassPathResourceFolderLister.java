import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * List entries of a subfolder of an entry in the class path, which may consist of file system folders and .jars.
 */
public class ClassPathResourceFolderLister {
  /**
   * For each entry in the classpath, verify that (a) "folder" exists, and (b) that it has child content, and if
   * so return the child entries (be they files, or folders).  If neither (a) nor (b) are true for a particular
   * class path entry, move on to the next entry and try again.
   *
   * @param folder the folder to match within the class path entry
   *
   * @return the subfolder items of the first matching class path entry, with a no duplicates guarantee
   */
  public static Collection<String> getFolderListing(final String folder) {
    final String classPath = System.getProperty("java.class.path", ".");
    final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
    List<String> classPathElementsList = new ArrayList<String> ( Arrays.asList(classPathElements));

    return getFolderListingForFirstMatchInClassPath(folder, classPathElementsList);
  }

  private static Collection<String>
  getFolderListingForFirstMatchInClassPath(String folder,
                                           List<String> classPathElementsList) {
    final Collection<String> retval = new HashSet<String>();
    System.out.println("folder:" + folder);
    if (folder.equals("/")) {
        folder = "";             // handle degenerate case, if not in this block than any string starting or ending with "/" has at least two characters
    } else {
      if (folder.endsWith("/")) {
        folder = folder.substring(0, folder.length()-1);
      }
      if (folder.startsWith("/")) {
        folder = folder.substring(1, folder.length());
      }
    }

    if (folder.startsWith("/") || folder.endsWith("/")) {
      throw new IllegalArgumentException("too many consecutive slashes in folder specification: " + folder);
    }

    classPathElementsList.add("/tmp/sample.jar");
    for (final String element : classPathElementsList) {
      System.out.println("class path element:" + element);
      retval.addAll(getFolderListing(element, folder));

      if (retval.size() > 0) {
        System.out.println("found matching folder in class path list. taking what we find in first matching folder");
        return retval;
      }
    }
    return new HashSet<String>();   // nothing found
  }

  private static Collection<String> getFolderListing(
      final String element,
      final String pattern) {
    final Collection<String> retval = new HashSet<>();
    final File file = new File(element);
    if (file.isDirectory()) {
      retval.addAll(getFolderContentsListingFromSubfolder(file, pattern));
    } else {
      retval.addAll(getResourcesFromJarFile(file, pattern));
    }
    return retval;
  }

  private static Collection<String> getResourcesFromJarFile(
      final File file,
       String pattern) {
    try {
      System.out.println("jar file:" + file.getCanonicalPath().toString());
    } catch(Exception e) {
      System.out.println("io erorr:" + e.getStackTrace());
    }
    final String leadingPathOfZipEntry = pattern.toString() + "/";
    final HashSet<String> retval = new HashSet<String>();
    ZipFile zf;
    try {
      zf = new ZipFile(file);
    } catch (final ZipException e) {
      throw new Error(e);
    } catch (final IOException e) {
      throw new Error(e);
    }
    final Enumeration e = zf.entries();
    while (e.hasMoreElements()) {
      final ZipEntry ze = (ZipEntry) e.nextElement();
      final String fileName = ze.getName();
      System.out.println("zip entry fileName:" + fileName);
      if (fileName.startsWith(leadingPathOfZipEntry)) {
        final String justLeafPartOfEntry = fileName.replaceFirst(leadingPathOfZipEntry,"");
        System.out.println("justLeafPartOfEntry:" + justLeafPartOfEntry);
        final String initSegmentOfPath = justLeafPartOfEntry.replaceFirst("/.*", "");
        System.out.println("initSegmentOfPath:" + initSegmentOfPath);
        if (initSegmentOfPath.length() > 0) {
          retval.add(initSegmentOfPath);
        }
      }
    }
    try {
      zf.close();
    } catch (final IOException e1) {
      throw new Error(e1);
    }
    return retval;
  }

  private static Collection<String> getFolderContentsListingFromSubfolder(
      final File directory,
       String pattern) {
    final String patAsString = pattern.toString();
    System.out.println("patAsString:" + patAsString);

    final HashSet<String> retval = new HashSet<String>();

    try {
      final String fullPath = directory.getCanonicalPath() + "/" + patAsString;
      final File subFolder = new File(fullPath);
      System.out.println("fullPath:" + fullPath);
      if (subFolder.isDirectory()) {
        final File[] fileList = subFolder.listFiles();
        for (final File file : fileList) {
          retval .add(file.getName());
        }
      }
    } catch (final IOException e) {
      throw new Error(e);
    }
    return retval;
  }
}
