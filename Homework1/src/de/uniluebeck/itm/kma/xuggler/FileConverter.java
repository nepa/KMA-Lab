package de.uniluebeck.itm.kma.xuggler;

import javax.swing.JTextPane;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaTool;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import de.uniluebeck.itm.kma.LoggingHelper;

import de.uniluebeck.itm.kma.xuggler.tools.ReadAudioResamplerTool;
import de.uniluebeck.itm.kma.xuggler.tools.VolumeAdjustTool;
import de.uniluebeck.itm.kma.xuggler.tools.WriteAudioResamplerTool;

/**
 * Converter for multimedia files. Offers means for volume
 * adjustment, audio resampling and file type transcoding.
 *
 * @author seidel
 */
public class FileConverter
{
  /** JTextPane object for analyzer output */
  private JTextPane logPane;

  /**
   * Constructor sets logPane member.
   *
   * @param logPane JTextPane object
   */
  public FileConverter(JTextPane logPane)
  {
    this.logPane = logPane;
  }

  /**
   * Setter for logPane member.
   *
   * @param logPane JTextPane object
   */
  public void setLogPane(JTextPane logPane)
  {
    this.logPane = logPane;
  }

  /**
   * Getter for logPane member.
   *
   * @return JTextPane object
   */
  public JTextPane getLogPane()
  {
    return this.logPane;
  }

  /**
   * Helper method for logging, provided for convenience.
   *
   * @param textToAppend Text to append to log pane
   */
  private void log(String textToAppend)
  {
    LoggingHelper.appendAndScroll(this.logPane, textToAppend);
  }

  /**
   * This method is used for volume adjustment. Third argument
   * is used as a multiplier to increase or decrease audio
   * volume. Value should be between 0.0 and 2.0.
   *
   * @param intputFile Input file name
   * @param outputFile Output file name
   * @param adjustmentAmount Amount of adjustment
   *
   * @throws RuntimeException Error during media processing
   */
  public void adjustVolume(String intputFile, String outputFile, double adjustmentAmount) throws RuntimeException
  {
    this.log("============================================================");
    this.log("");
    this.log("Adjusting volume by " + adjustmentAmount + "...");

    // Create reader and writer object
    IMediaReader reader = ToolFactory.makeReader(intputFile);
    IMediaWriter writer = ToolFactory.makeWriter(outputFile, reader);

    // Instantiate VolumeAdjustTool
    IMediaTool adjustVolume = new VolumeAdjustTool(adjustmentAmount);

    // Create tool chain: reader -> VolumeAdjustTool -> writer
    reader.addListener(adjustVolume);
    adjustVolume.addListener(writer);

    // Read and decode packets
    while (reader.readPacket() == null)
    {
      // Do nothing intentionally!
    }

    // Safely close reader and writer
    this.closeContainerSafely(reader.getContainer());
    this.closeContainerSafely(writer.getContainer());

    // This will cause error here:
    //reader.close();
    //writer.close();

    this.log("Volume adjustment completed.");
    this.log("");
  }

  /**
   * This method is used for audio resampling. Just pass new
   * sampling rate and new channel count to alter input file.
   *
   * @param inputFile Input file name
   * @param outputFile Output file name
   * @param newSampleRate New sample rate
   * @param newChannels New amount of channels
   *
   * @throws RuntimeException Error during media processing
   */
  public void resampleAudio(String inputFile, String outputFile, int newSampleRate, int newChannels) throws RuntimeException
  {
    this.log("Resampling audio to " + newSampleRate + " Hz (" + newChannels + " channels)...");

    // Create reader and writer object
    IMediaReader reader = ToolFactory.makeReader(inputFile);
    IMediaWriter writer = ToolFactory.makeWriter(outputFile, reader);

    // Create resampler objects
    ReadAudioResamplerTool readerResampler = new ReadAudioResamplerTool(newSampleRate, newChannels);
    WriteAudioResamplerTool writerResampler = new WriteAudioResamplerTool(newSampleRate, newChannels);

    // Create tool chain: reader -> ReadAudioResamplerTool -> writer -> WriteAudioResamplerTool
    reader.addListener(readerResampler);
    readerResampler.addListener(writer);
    writer.addListener(writerResampler);

    // Read and decode packets
    while (reader.readPacket() == null)
    {
      // Do nothing intentionally!
    }

    // Safely close reader and writer
    this.closeContainerSafely(reader.getContainer());
    this.closeContainerSafely(writer.getContainer());

    // This will cause error here:
    //reader.close();
    //writer.close();

    this.log("Audio resampling completed.");
    this.log("");
  }

  /**
   * This method converts an input file to a different output format.
   * File type for transcoding is derived from the extension of the
   * output file (for example Output.mp3 will create a MP3 file).
   *
   * Transcoder uses default values of the specified file types.
   *
   * @param inputFile Input file name
   * @param outputFile Output file name
   *
   * @throws RuntimeException Error during media processing
   */
  public void transcode(String inputFile, String outputFile) throws RuntimeException
  {
    this.log("Transcoding input to new file format...");

    // Create reader and writer object
    IMediaReader reader = ToolFactory.makeReader(inputFile);
    IMediaWriter writer = ToolFactory.makeWriter(outputFile, reader);

    // Create tool chain: reader -> writer
    reader.addListener(writer);

    // Read and decode packets
    while (reader.readPacket() == null)
    {
      // Do nothing intentionally!
    }

    // Safely close reader and writer
    this.closeContainerSafely(reader.getContainer());
    this.closeContainerSafely(writer.getContainer());

    // This will cause error here:
    //reader.close();
    //writer.close();

    this.log("Transcoding finished.");
    this.log("");
  }

  /**
   * This method safely closes an IContainer object. Using
   * normal container.close() operation may cause errors
   * in some cases.
   *
   * @param container IContainer object
   */
  private void closeContainerSafely(IContainer container)
  {
    // Create local variables for Xuggler
    int i;
    int numStreams = container.getNumStreams();

    // Finalize streams for writable IContainer objects.
    //
    // Some video coders do read-ahead in a stream and keep extra data
    // for efficient compression. But they need some way to know, no
    // more data will come. The convention for that case is, to pass
    // "null" for the IMediaData once before closing the coder.
    if (container.getType() == IContainer.Type.WRITE)
    {
      for (i = 0; i < numStreams; i++)
      {
        IStreamCoder c = container.getStream(i).getStreamCoder();
        if (c != null)
        {
          IPacket oPacket = IPacket.make();
          if (c.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
          {
            c.encodeVideo(oPacket, null, 0);
          }
          if (oPacket.isComplete())
          {
            container.writePacket(oPacket, true);
          }
        }
      }
    }

    // Write trailer to avoid corrupted output file.
    //
    // Some container formats require a trailer at the end of
    // the file. So we may not close the container, before a
    // valid trailer is written.
    if (container.getType() == IContainer.Type.WRITE)
    {
      int retval = container.writeTrailer();
      if (retval < 0)
      {
        throw new RuntimeException("Could not write trailer to output file.");
      }
    }

    // Release IStreamCoder resources
    for (i = 0; i < numStreams; i++)
    {
      IStreamCoder c = container.getStream(i).getStreamCoder();
      if (c != null)
      {
        c.close();
      }
    }

    // Close container and free memory
    container.close();
    container = null;
  }
}
