package de.uniluebeck.itm.kma.xuggler.tools;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Media tool for audio bitrate resampling. This class can change
 * the sample rate of an audio stream and pass it to an IMediaWriter
 * afterwards. WriteAudioResamplerTool overrides onAddStream from
 * MediaToolAdapter to gain access to the IAddStreamEvent, which is
 * called during stream setup.
 *
 * @author seidel
 */
public class WriteAudioResamplerTool extends MediaToolAdapter
{
  /** Logger object for debug output */
  private static final Logger LOGGER = LoggerFactory.getLogger(WriteAudioResamplerTool.class);

  /** Audio sample rate */
  private int sampleRate;

  /** Amount of audio channels */
  private int channels;

  /**
   * Constructor sets audio sample rate and amount of channels.
   *
   * @param newSampleRate Audio sample rate
   * @param newChannels Amount of channels
   */
  public WriteAudioResamplerTool(int newSampleRate, int newChannels)
  {
    this.sampleRate = newSampleRate;
    this.channels = newChannels;

    LOGGER.info("WriteAudioResamplerTool created.");
    LOGGER.info("New bitrate is " + this.sampleRate + " Bit/s. New channel count is " + this.channels + ".");
  }

  /**
   * Event handler onAddStream is called by the MediaToolAdapter
   * before any audio samples are processed. We need to configure
   * the writer's IStreamCoder with the new bitrate, before we
   * can resample the raw audio data.
   *
   * @param event Add stream event
   */
  @Override
  public void onAddStream(IAddStreamEvent event)
  {
    LOGGER.info("IAddStreamEvent fired: " + event.getSource());

    // Get stream index from event
    int streamIndex = event.getStreamIndex();

    // Create container, get stream and build coder
    IContainer container = event.getSource().getContainer();
    IStream stream = container.getStream(streamIndex);
    IStreamCoder coder = stream.getStreamCoder();

    // If stream is audio stream...
    if (coder.getCodecType().equals(ICodec.Type.CODEC_TYPE_AUDIO))
    {
      // ... alter sample rate and amount of channels
      coder.setSampleRate(this.sampleRate);
      coder.setChannels(this.channels);
    }

    // Pass adjusted event to next tool
    super.onAddStream(event);
  }
}
