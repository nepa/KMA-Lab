package de.uniluebeck.itm.kma.xuggler.tools;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import java.nio.ShortBuffer;

/**
 * Media tool for volume adjustment. This class can increase or
 * decrease the volume of an audio file by a constant multiplier.
 * VolumeAdjustTool overrides onAudioSamples from MediaToolAdapter
 * to gain access to IAudioSampleEvents during media processing.
 *
 * @author seidel
 */
public class VolumeAdjustTool extends MediaToolAdapter
{
  /** Multiplier for volume adjustment */
  private double multiplier;

  /**
   * Constructor checks and sets volume multiplier. Argument
   * must be value between 0.0 and 2.0 (where 1.0 denotes no
   * change in volume at all).
   *
   * @param volumeMultiplier Volume multiplier
   */
  public VolumeAdjustTool(double volumeMultiplier)
  {
    // Check argument
    if (volumeMultiplier < 0.0 || volumeMultiplier > 2.0)
    {
      throw new IllegalArgumentException("Volume multiplier must be between 0 and 2.");
    }

    this.multiplier = volumeMultiplier;
  }

  /**
   * Event handler onAudioSamples gives access to the underlying
   * IAudioSamples during media processing. This way audio samples
   * may be altered locally and then passed to the superclass,
   * which gives them to other IMediaListeners.
   *
   * @param event Audio samples event
   */
  @Override
  public void onAudioSamples(IAudioSamplesEvent event)
  {
    // Create buffer for incoming audio samples
    ShortBuffer buffer = event.getAudioSamples().getByteBuffer().asShortBuffer();

    // Modify elements in buffer (here: multiply with constant
    // value to increase or decrease audio volume)
    for (int i = 0; i < buffer.limit(); ++i)
    {
      buffer.put(i, (short)(buffer.get(i) * this.multiplier));
    }

    // Pass adjusted event to next tool
    super.onAudioSamples(event);
  }
}
