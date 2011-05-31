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
   * Check for multimedia files. Very dirty implementation,
   * but should be sufficient for some testing.
   *
   * @param file File object from JFileChooser dialog
   *
   * @return Boolean flag for file acceptance
   */
  @Override
  public boolean accept(File file)
  {
    return file.isDirectory()
            || file.getAbsolutePath().endsWith(".mp3")
            || file.getAbsolutePath().endsWith(".wav")
            || file.getAbsolutePath().endsWith(".avi")
            || file.getAbsolutePath().endsWith(".mpeg")
            || file.getAbsolutePath().endsWith(".mov");
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
    return "Media files (*.mp3/*.wav/*.avi/*.mpeg/*.mov)";
  }
}
