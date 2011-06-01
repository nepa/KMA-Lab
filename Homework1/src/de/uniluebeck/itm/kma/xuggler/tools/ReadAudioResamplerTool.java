package de.uniluebeck.itm.kma.xuggler.tools;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.AudioSamplesEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ReaderAudioResampler is used to re-sample the incoming IAudioSamples. This class uses an
 * IAudioResampler to perform the actual re-sampling. This class extends MediaToolAdapter and
 * @Override(s) onAudioSamples to gain access to the IAudioSamplesEvent(s), which arrive as the
 * IMediaReader reads a stream.
 * @author seidel
 */
public class ReadAudioResamplerTool extends MediaToolAdapter
{
  /** Logger object for debug output */
  private static final Logger LOGGER = LoggerFactory.getLogger(ReadAudioResamplerTool.class);

  // Private data
  private IAudioResampler resampler;

  private int sampleRate;

  private int channels;

  // AudioResampler constructor
  public ReadAudioResamplerTool(int newSampleRate, int newChannels)
  {
    this.sampleRate = newSampleRate;
    this.channels = newChannels;
    LOGGER.info("Created an ReaderAudioResampler: {}/{}", newSampleRate, newChannels);
  }

  /*
   * @Override onAudioSamples
   *
   * As IAudioSamples are read from the IMediaReader in our processing chain, this method
   * will be called with an IAudioSamplesEvent that contains the actual IAudioSamples from the
   * underlying stream. This method re-samples the data contained within the event and passes
   * the re-sampled data to the next IMediaListener in the chain.
   */
  @Override
  public void onAudioSamples(IAudioSamplesEvent event)
  {

    /*
     * -----------------------------------------------------------------------------------
     * !!! LAB EXERCISE !!!
     * -----------------------------------------------------------------------------------
     * 1. Grab the audio IAudioSamples from the incoming event
     *    Hint: Use 'event.getAudioSamples()'
     *
     * 2. Create an IAudioResampler to handle re-sampling
     *    Hint: Use 'IAudioResampler.make'
     *    Hint: For performance reasons, only create the IAudioResampler once. Store your
     *          instantiated IAudioSampler in 'this.resampler'.
     *
     * 3. Check if the event has more than zero samples
     *    Hint: Use 'event.getAudioSamples().getNumSamples()'
     *
     * 4. Create a temporary IAudioSamples buffer to hold the re-sampled data
     *    Hint: Use 'IAudioSamples.make' to create the buffer
     *    Hint: Use the original IAudioSamples values for setting the
     *          number of buffer's samples ('samples.getNumSamples()') and the number
     *          of channels ('samples.getChannels()').
     *
     * 5. Use the IAudioResampler to re-sample the incoming samples, placing using the
     *    temporary IAudioSamples buffer created above
     *    Hint: Use 'resampler.resample'
     *
     * 6. Create a new AudioSamplesEvent using the IAudioSamples buffer as the
     *    sample data.
     *    Hint: For the event source, use 'event.getSource()'
     *    Hint: For the stream index, use 'event.getStreamIndex()'
     *
     * 7. Pass this new AudioSamplesEvent to the superclass
     *    Hint: Use 'super.onAudioSamples'
     *
     * 8. Delete the temporary IAudioSamples to reclaim resources
     *    Hint: Use the IAudioSamples' 'delete()' method
     */

    // ===================================================================================
    // *** YOUR CODE HERE ***
    IAudioSamples samples = event.getAudioSamples();

    if (this.resampler == null)
    {
      this.resampler = IAudioResampler.make(this.channels, samples.getChannels(), this.sampleRate, samples.getSampleRate());
    }

    if (samples.getNumSamples() > 0)
    {
      IAudioSamples temporarySamples = IAudioSamples.make(samples.getNumSamples(), samples.getChannels());

      resampler.resample(temporarySamples, samples, samples.getNumSamples());

      AudioSamplesEvent samplesEvent = new AudioSamplesEvent(event.getSource(), temporarySamples, event.getStreamIndex());

      super.onAudioSamples(samplesEvent);

      temporarySamples.delete();
    }
    // ===================================================================================
  }
}
