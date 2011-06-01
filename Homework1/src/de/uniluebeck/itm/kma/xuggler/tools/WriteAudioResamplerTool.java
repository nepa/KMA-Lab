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
 * The WriterAudioResampler is used to configure the underlying IStreamCoder for an IMediaWriter
 * to a new sample rate. This class extends MediaToolAdapter and @Override(s) onAddStream to
 * gain access to the IAddStreamEvent, which is called during stream setup.
 *
 * @author seidel
 */
public class WriteAudioResamplerTool extends MediaToolAdapter
{
  /** Logger object for debug output */
  private static final Logger LOGGER = LoggerFactory.getLogger(WriteAudioResamplerTool.class);

  // Private data
  private int sampleRate;

  private int channels;

  // AudioResampler constructor
  public WriteAudioResamplerTool(int newSampleRate, int newChannels)
  {
    this.sampleRate = newSampleRate;
    this.channels = newChannels;
    LOGGER.info("Created an WriterAudioResampler: {}/{}", newSampleRate, newChannels);
  }

  /*
   * @Override onAddStream
   *
   * Before we can re-sample raw audio data, we need to configure the writer's IStreamCoder
   * with the new sample rate. The onAddStream method is called by the MediaToolAdapter
   * before any audio samples are processed.
   */
  @Override
  public void onAddStream(IAddStreamEvent event)
  {
    LOGGER.info("IAddStreamEvent: {}", event.getSource());

    /*
     * -----------------------------------------------------------------------------------
     * !!! LAB EXERCISE !!!
     * -----------------------------------------------------------------------------------
     *
     * 1. Get the streamIndex for the newly added stream
     *    Hint: use 'event.getStreamIndex()'
     *
     * 2. Get the IStreamCoder for the stream
     *    Hint: First, get the source container with 'event.getSource().getContainer()'
     *    Hint: Using the container, get stream with 'getStream(streamIndex)'
     *    Hint: Using the stream, get the coder with 'getStreamCoder()'
     *
     * 3. Set the sample rate and channels on the obtained streamCoder
     *    Hint: First, check if 'streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO'
     *    Hint: Set sample rate using 'streamCoder.setSampleRate' and 'this.sampleRate'
     *    Hint: Set channels using 'streamCoder.setChannels' and 'this.channels'
     */

    // ===================================================================================
    // *** YOUR CODE HERE ***
    int streamIndex = event.getStreamIndex();

    IContainer container = event.getSource().getContainer();
    IStream stream = container.getStream(streamIndex);
    IStreamCoder coder = stream.getStreamCoder();

    if (coder.getCodecType().equals(ICodec.Type.CODEC_TYPE_AUDIO))
    {
      coder.setSampleRate(this.sampleRate);

      coder.setChannels(this.channels);
    }
    // ===================================================================================

    // Finally, pass the adjusted event to the next tool
    super.onAddStream(event);
  }
}
