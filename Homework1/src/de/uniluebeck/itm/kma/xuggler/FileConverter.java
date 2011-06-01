package de.uniluebeck.itm.kma.xuggler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaTool;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import de.uniluebeck.itm.kma.xuggler.tools.ReadAudioResamplerTool;
import de.uniluebeck.itm.kma.xuggler.tools.VolumeAdjustTool;
import de.uniluebeck.itm.kma.xuggler.tools.WriteAudioResamplerTool;

/**
 * Converter for multimedia files. Offers means for volume
 * adjustment, audio bitrate resampling and file type
 * transcoding.
 *
 * @author seidel
 */
public class FileConverter
{
  /** Logger object for debug output */
  private static final Logger LOGGER = LoggerFactory.getLogger(FileConverter.class);

  /**
   * This method is used for volume adjustment. Third argument
   * is used as a multiplier to increase or decrease audio
   * volume. Value should be between 0.0 and 2.0.
   *
   * @param intputFile Input file name
   * @param outputFile Output file name
   * @param adjustmentAmount Amount of adjustment
   */
  public static void adjustVolume(String intputFile, String outputFile, double adjustmentAmount)
  {
    LOGGER.info("Adjusting volume by " + adjustmentAmount + "...");

    // Create reader and writer object
    IMediaReader reader = ToolFactory.makeReader(intputFile);
    IMediaWriter writer = ToolFactory.makeWriter(outputFile, reader);

    // Instantiate VolumeAdjustTool
    IMediaTool adjustVolume = new VolumeAdjustTool(adjustmentAmount);

    // Create tool chain: reader -> VolumeAdjustTool -> writer
    reader.addListener(adjustVolume);
    adjustVolume.addListener(writer);

    // Read and decode packets
    try
    {
      while (reader.readPacket() == null)
      {
        // Do nothing intentionally!
      }
    }
    catch (Exception e)
    {
      LOGGER.error(e.toString());
    }

    // Safely close reader and writer
    FileConverter.closeContainerSafely(reader.getContainer());
    FileConverter.closeContainerSafely(writer.getContainer());

    // This will cause error here:
    //reader.close();
    //writer.close();

    LOGGER.info("Volume adjustment completed.");
  }

  /**
   * This method is used for audio bitrate resampling. Just pass
   * new sampling rate and new channel count to alter input file.
   *
   * @param intputFile Input file name
   * @param outputFile Output file name
   * @param newSampleRate New sample rate
   * @param newChannels New amount of channels
   */
  public static void resampleAudio(String intputFile, String outputFile, int newSampleRate, int newChannels)
  {
    LOGGER.info("Resampling audio bitrate to " + newSampleRate + " Hz (" + newChannels + " channels)...");

    // Create reader and writer object
    IMediaReader reader = ToolFactory.makeReader(intputFile);
    IMediaWriter writer = ToolFactory.makeWriter(outputFile, reader);

    // Create resampler objects
    ReadAudioResamplerTool readerResampler = new ReadAudioResamplerTool(newSampleRate, newChannels);
    WriteAudioResamplerTool writerResampler = new WriteAudioResamplerTool(newSampleRate, newChannels);

    // Create tool chain: reader -> ReadAudioResamplerTool -> writer -> WriteAudioResamplerTool
    reader.addListener(readerResampler);
    readerResampler.addListener(writer);
    writer.addListener(writerResampler);

    // Read and decode packets
    try
    {
      while (reader.readPacket() == null)
      {
        // Do nothing intentionally!
      }
    }
    catch (Exception e)
    {
      LOGGER.error(e.toString());
    }

    // Safely close reader and writer
    FileConverter.closeContainerSafely(reader.getContainer());
    FileConverter.closeContainerSafely(writer.getContainer());

    // This will cause error here:
    //reader.close();
    //writer.close();

    LOGGER.info("Audio resampling completed.");
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
   */
  public static void transcode(String inputFile, String outputFile)
  {
    LOGGER.info("Transcoding input to new file format...");

    // Create reader and writer object
    IMediaReader reader = ToolFactory.makeReader(inputFile);
    IMediaWriter writer = ToolFactory.makeWriter(outputFile, reader);

    // Create tool chain: reader -> writer
    reader.addListener(writer);

    // Read and decode packets
    try
    {
      while (reader.readPacket() == null)
      {
        // Do nothing intentionally!
      }
    }
    catch (Exception e)
    {
      LOGGER.error(e.toString());
    }

    // Safely close reader and writer
    FileConverter.closeContainerSafely(reader.getContainer());
    FileConverter.closeContainerSafely(writer.getContainer());

    // This will cause error here:
    //reader.close();
    //writer.close();

    LOGGER.info("Transcoding finished.");
  }

  /**
   * This method safely closes an IContainer object. Using
   * normal container.close() operation may cause errors
   * in some cases.
   *
   * @param container IContainer object
   */
  private static void closeContainerSafely(IContainer container)
  {
    LOGGER.info("Closing container...");

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

    LOGGER.info("Finished closing container.");
  }
}
