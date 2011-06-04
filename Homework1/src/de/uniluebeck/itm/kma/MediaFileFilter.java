package de.uniluebeck.itm.kma;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Custom file filter for JFileChooser.
 *
 * @author seidel
 */
class MediaFileFilter extends FileFilter
{
  /**
   * Check for multimedia files by file extension. Very dirty
   * implementation, but should be sufficient for testing.
   *
   * @param file File object from JFileChooser dialog
   *
   * @return Boolean flag for file acceptance
   */
  @Override
  public boolean accept(File file)
  {
    boolean result = false;

    // Supported file types
    String[] fileExtensions =
    {
      ".wav", ".mp3", ".mpeg", ".avi", ".mov", ".wma", ".ogg", ".mp2", ".mp4", ".flac", ".aac", ".au", ".ra"
    };

    // Check all file extensions...
    for (String fileExtension: fileExtensions)
    {
      result = result || file.getAbsolutePath().endsWith(fileExtension);
    }

    // ... and also check for directory
    return file.isDirectory() || result;
  }

  /**
   * Set description for filter in JFileChooser dialog.
   * Again, just some hard-wired code.
   *
   * @return String with description
   */
  @Override
  public String getDescription()
  {
    // No localization supported here :)
    return "Media files (extension is ignored)";
  }
}
