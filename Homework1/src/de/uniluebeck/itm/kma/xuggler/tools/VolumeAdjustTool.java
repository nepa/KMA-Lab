package de.uniluebeck.itm.kma.xuggler.tools;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import java.nio.ShortBuffer;

/**
 * The VolumeAdjustTool class adjusts the volume of audio by a constant multiplier (a percentage
 * value between 0 and 1). This class extends MediaToolAdapter and @Override(s) onAudioSamples
 * to gain access to IAudioSamplesEvent(s) during media processing.
 *
 * @author seidel
 */
public class VolumeAdjustTool extends MediaToolAdapter
{
  // Private data
  private double multiplier;

  // VolumeAdjustTool constructor
  public VolumeAdjustTool(double volumeMultiplier)
  {
    if (volumeMultiplier < 0 || volumeMultiplier > 2)
    {
      throw new IllegalArgumentException("must be between 0 and 2");
    }
    multiplier = volumeMultiplier;
  }

  /*
   * Override onAudioSamples to gain access to underlying IAudioSamples during media
   * processing. Audio samples may be altered locally in this method and then passed to the
   * superclass, which passes the altered data to additional IMediaListeners.
   */
  @Override
  public void onAudioSamples(IAudioSamplesEvent event)
  {

    /*
     * -----------------------------------------------------------------------------------
     * !!! LAB EXERCISE !!!
     * -----------------------------------------------------------------------------------
     * 1. Create a 'ShortBuffer' and point it to the incoming audio samples.
     *    Hint: Use 'event.getAudioSamples().getByteBuffer().asShortBuffer()'
     *
     * 2. Loop over the buffer values from 0 to 'buffer.limit()' and access each buffer
     *    value using 'buffer.get'
     *    Hint: 'buffer.get' gets the sample values as a 'short'
     *
     * 3. Multiply each buffer value by the 'multiplier' (which is a double) then place the
     *    resultant value back into the buffer using 'buffer.put'
     *    Hint: You may need to cast the data to a 'short' value
     */

    // ===================================================================================
    // *** YOUR CODE HERE ***
    ShortBuffer buffer = event.getAudioSamples().getByteBuffer().asShortBuffer();

    for (int i = 0; i < buffer.limit(); ++i)
    {
      buffer.put(i, (short)(buffer.get(i) * this.multiplier));
    }
    // ===================================================================================

    // Finally, pass the adjusted event to the next tool
    super.onAudioSamples(event);
  }
}
