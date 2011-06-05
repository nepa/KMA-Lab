package de.uniluebeck.itm.kma;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for easy output of text on a JTextPane.
 *
 * @author seidel
 */
public class LoggingHelper
{
  /** Logger object for debug output */
  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingHelper.class);

  /**
   * This method appends text to the given JTextPane, respecting
   * platform-specific line delimiters. The document model of the
   * JTextPane component is utilized for higher performance.
   * Furthermore the caret will always scroll to the end of the
   * pane, so that the user won't have to.
   *
   * @param textPane JTextPane for output
   * @param text Text to append
   */
  public static void appendAndScroll(JTextPane textPane, String text)
  {
    try
    {
      textPane.getDocument().insertString(textPane.getDocument().getLength(),
              text + System.getProperty("line.separator"), null);
      textPane.setCaretPosition(textPane.getDocument().getLength()); // Auto-scrolling
    }
    catch (BadLocationException e)
    {
      LOGGER.info(e.getMessage());
    }
  }
}
