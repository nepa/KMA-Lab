package de.uniluebeck.itm.kma.xuggler.tools;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.AudioSamplesEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Media tool for audio resampling. This class uses an
 * IAudioResampler to change the sample rate of an audio
 * stream. ReadAudioResamplterTool overrides onAudioSamples
 * from MediaToolAdapter to gain access to the IAudioSamplesEvent,
 * which arrives as the IMediaReader reads a stream.
 *
 * @author seidel
 */
public class ReadAudioResamplerTool extends MediaToolAdapter
{
  /** Logger object for debug output */
  private static final Logger LOGGER = LoggerFactory.getLogger(ReadAudioResamplerTool.class);

  /** Audio resampler object */
  private IAudioResampler resampler;

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
  public ReadAudioResamplerTool(int newSampleRate, int newChannels)
  {
    this.sampleRate = newSampleRate;
    this.channels = newChannels;

    LOGGER.info("ReadAudioResamplerTool created.");
    LOGGER.info("New sample rate is " + this.sampleRate + " Hz. New channel count is " + this.channels + ".");
  }

  /**
   * Event handler onAudioSamples is called as IAudioSamples are
   * read from the IMediaReader in the processing chain. The method
   * argument contains the IAudioSamples from the underlying stream.
   * Thus we can resample the data and pass it to the next
   * IMediaListener in the tool chain.
   *
   * @param event Audio samples event
   */
  @Override
  public void onAudioSamples(IAudioSamplesEvent event)
  {
    // Get audio samples
    IAudioSamples samples = event.getAudioSamples();

    // Create resampler only once (for performance reasons)
    if (this.resampler == null)
    {
      // Set old and new channel count/sample rate
      this.resampler = IAudioResampler.make(this.channels, samples.getChannels(), this.sampleRate, samples.getSampleRate());
    }

    if (this.resampler != null && event.getAudioSamples().getNumSamples() > 0)
    {
      // Create buffer for resampled data
      IAudioSamples outputSamples = IAudioSamples.make(samples.getNumSamples(), samples.getChannels());

      // Resample incoming data
      resampler.resample(outputSamples, samples, samples.getNumSamples());

      // Create new AudioSamplesEvent
      AudioSamplesEvent samplesEvent = new AudioSamplesEvent(event.getSource(), outputSamples, event.getStreamIndex());

      // Pass new event to next tool in chain
      super.onAudioSamples(samplesEvent);

      // Release temporary buffer
      outputSamples.delete();
    }
  }
}
