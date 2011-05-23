package kma.exercises;

import java.io.File;
import java.nio.ShortBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaTool;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.AudioSamplesEvent;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * OVERVIEW 
 * The DigitalAudio class performs simple audio processing tasks on input files. These
 * tasks include basic format transcoding, volume adjustment and re-sampling. The class includes a
 * test main and associated methods for each audio processing task. Several inner classes
 * perform the actual work. These inner classes, including VolumeAdjustTool, WriterAudioResampler,
 * ReaderAudioResampler are intended to be completed by the student. This class is based on
 * source-code written by Art Clarke from Xuggle Inc.
 * 
 * TODO
 * 1. Review the 'transcode' method
 * 2. Review the 'adjustVolume' method
 * 3. Review the 'resampleAudio' method
 * 4. Complete the 'VolumeAdjustTool' class
 * 5. Complete the 'WriterAudioResampler' class
 * 6. Complete the 'ReaderAudioResampler' class
 * 7. Complete and run the 'main' method
 * 
 * Hint:
 * - Read the sections marked: !!! LAB EXERCISE !!!
 * - Complete the sections marked: *** YOUR CODE HERE ***
 * - Have fun!
 * 
 * Note: This class is intentionally incomplete and should be finished by the student.
 * To help guide your work, this class includes a partial structure along with 
 * embedded comments, hints and links. An accompanying in-class presentation
 * will provide the background information and instructions necessary to 
 * complete this exercise.
 * 
 * REFERENCES 
 * The Xuggler source-code can be found at:
 * http://code.google.com/p/xuggle/source/browse/
 * 
 * The Xuggler Java documentation can be found at:
 * http://build.xuggle.com/view/Stable/job/xuggler_jdk5_stable/javadoc/java/api/index.html
 * 
 */
public class DigitalAudio {
	/*
	 * Create a Logger object specific to this class. For details on the Simple Logging Facade for
	 * Java (SLF4J), see: http://www.slf4j.org/
	 */
	private static final Logger log = LoggerFactory.getLogger(DigitalAudio.class);

	/**
	 * main method
	 */
	public static void main(String[] args) {
		log.info("Starting tests...");

		/*
		 * -----------------------------------------------------------------------------------
		 * !!! LAB EXERCISE !!!
		 * -----------------------------------------------------------------------------------
		 * 1. Before beginning this exercise, download 2 sample mp3 files from the course 
		 *    exercise page (for details, please see the accompanying slide presentation).
		 *    
		 * 2. Prepare an Eclipse "Run configuration" for the DigitalAudio class.
		 * 
		 * 3. Set the 'inputFilePath' variable to the complete file path of each of your 
		 *    downloaded sample media files and then run the 'main' method.
		 */
		log.info("Your JVM is running on " + System.getProperty("sun.arch.data.model") + " Bit.");
		
		// Check arguments passed to application
		if (args.length < 1)
		{
			log.error("Please pass input file when starting application.");
			System.exit(1);
		}
		
		String inputFilePath = args[0];
		System.out.println(args[0]);
		File f = new File(inputFilePath);
		if (!f.exists())
			throw new IllegalArgumentException("Input file required!");

		/*
		 * Prepare the outputFile paths. Note: We naively use the file extension to
		 * create paths using string replace. Clearly, this method is not robust and 
		 * assumes that the input file will be of the correct type.
		 * Don't use such tricks in production code :)
		 */
		String transcodePath = f.getPath().replace(".mp3", "_transcode.wav");
		String adjustVolumePath = f.getPath().replace(".mp3", "_volume.mp3");
		String resampleAudioPath = f.getPath().replace(".mp3", "_resampled.mp3");

		// Test the transcode method
		transcode(inputFilePath, transcodePath);

		// Test the adjustVolume method
		adjustVolume(inputFilePath, adjustVolumePath, 0.1);

		// Test the resampleAudio method
		resampleAudio(inputFilePath, resampleAudioPath, 8000, 2);

		log.info("Tests complete!");
	}

	/*
	 * Transcode an input file into an output file (and format) using the defaults of the specified
	 * file types. This method is only used to illustrate the fundamentals of the Xuggler MediaTool
	 * API and does not provide any conversion controls. A more detailed transcoding example will be
	 * explored in later labs.
	 */
	public static void transcode(String inputFile, String outputFile) {
		log.info("Starting to transcode...");

		// Create an IMediaReader using the ToolFactory
		IMediaReader reader = ToolFactory.makeReader(inputFile);

		/*
		 * Create an IMediaWriter and add it as a listener to the reader
		 * creating a simple tool chain: reader -> writer.
		 */
		IMediaWriter writer = ToolFactory.makeWriter(outputFile, reader);
		reader.addListener(writer);

		/*
		 * Read and decode packets from the reader, which triggers tool chain processing. Surround
		 * with a try/catch to log any errors.
		 */
		try {
			while (reader.readPacket() == null)
				;
		} catch (Exception e) {
			log.error(e.toString());
		}
		
		// Close the writer and reader
		writer.close();
		reader.close();

		log.info("Transcode finished!");
	}

	/*
	 * Utility method for testing the VolumeAdjustTool class
	 */
	public static void adjustVolume(String intputFile, String outputFile, double adjustmentAmount) {
		log.info("Adjusting volume by {}", adjustmentAmount);

		// Create an IMediaReader using ToolFactory.makeWriter
		IMediaReader reader = ToolFactory.makeReader(intputFile);

		/*
		 * Create an I that receives data from the above IMediaReader using
		 * ToolFactory.makeWriter
		 */
		IMediaWriter writer = ToolFactory.makeWriter(outputFile, reader);

		/*
		 * Create an VolumeAdjustTool that reduces audio volume by the adjustmentAmount
		 */
		IMediaTool reduceVolume = new VolumeAdjustTool(adjustmentAmount);

		// Create a tool chain: reader -> reduceVolume -> writer
		reader.addListener(reduceVolume);
		reduceVolume.addListener(writer);

		/*
		 * Read and decode packets from the reader, which triggers tool chain processing. Surround
		 * with a try/catch to log any errors.
		 */
		try {
			while (reader.readPacket() == null)
				;
		} catch (Exception e) {
			log.error(e.toString());
		}
		
		// Close the writer and reader
		writer.close();		
		reader.close();
		
		log.info("Volume adjustment complete!");
	}

	/*
	 * Utility method for testing the ReaderAudioResampler and WriterAudioResampler classes
	 */
	public static void resampleAudio(String intputFile, String outputFile, int newSampleRate,
			int newChannels) {
		log.info("Resampling audio to {}/{}", newSampleRate, newChannels);

		// Create an IMediaReader using ToolFactory.makeReader
		IMediaReader reader = ToolFactory.makeReader(intputFile);

		/*
		 * Create an IMediaWriter that receives data from the 'reader' using
		 * ToolFactory.makeWriter
		 */
		IMediaWriter writer = ToolFactory.makeWriter(outputFile, reader);

		// Create a ReaderAudioResampler (class to be completed by the student)
		ReaderAudioResampler readerResampler = new ReaderAudioResampler(newSampleRate, newChannels);

		// Create a WriterAudioResampler (class to be completed by the student)
		WriterAudioResampler writerResampler = new WriterAudioResampler(newSampleRate, newChannels);

		// Create a tool chain: reader -> readerResampler -> writer -> writerResampler
		reader.addListener(readerResampler);
		readerResampler.addListener(writer);
		writer.addListener(writerResampler);

		/*
		 * Read and decode packets from the reader, which triggers tool chain processing. Surround
		 * with a try/catch to log any errors.
		 */
		try {
			while (reader.readPacket() == null)
				;
		} catch (Exception e) {
			log.error(e.toString());
		}
		
		// Close the writer and reader
		writer.close();
		reader.close();
		
		log.info("Resampling audio complete");
	}

	/*
	 * The VolumeAdjustTool class adjusts the volume of audio by a constant multiplier (a percentage
	 * value between 0 and 1). This class extends MediaToolAdapter and @Override(s) onAudioSamples
	 * to gain access to IAudioSamplesEvent(s) during media processing.
	 */
	static class VolumeAdjustTool extends MediaToolAdapter {

		// Private data
		private double multiplier;

		// VolumeAdjustTool constructor
		public VolumeAdjustTool(double volumeMultiplier) {
			if (volumeMultiplier < 0 || volumeMultiplier > 1)
				throw new IllegalArgumentException("must be between 0 and 1");
			multiplier = volumeMultiplier;
		}

		/*
		 * Override onAudioSamples to gain access to underlying IAudioSamples during media
		 * processing. Audio samples may be altered locally in this method and then passed to the
		 * superclass, which passes the altered data to additional IMediaListeners.
		 */
		@Override
		public void onAudioSamples(IAudioSamplesEvent event) {

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
			double multiplier = 0.5;
			
			for (int i = 0; i < buffer.limit(); ++i)
			{
				buffer.put((short)(buffer.get(i) * this.multiplier));
			}
			// ===================================================================================

			// Finally, pass the adjusted event to the next tool
			super.onAudioSamples(event);
		}
	}

	/*
	 * The WriterAudioResampler is used to configure the underlying IStreamCoder for an IMediaWriter
	 * to a new sample rate. This class extends MediaToolAdapter and @Override(s) onAddStream to
	 * gain access to the IAddStreamEvent, which is called during stream setup.
	 */
	static class WriterAudioResampler extends MediaToolAdapter {

		// Private data
		private int sampleRate;
		private int channels;

		// AudioResampler constructor
		public WriterAudioResampler(int newSampleRate, int newChannels) {
			this.sampleRate = newSampleRate;
			this.channels = newChannels;
			log.info("Created an WriterAudioResampler: {}/{}", newSampleRate, newChannels);
		}

		/*
		 * @Override onAddStream
		 * 
		 * Before we can re-sample raw audio data, we need to configure the writer's IStreamCoder
		 * with the new sample rate. The onAddStream method is called by the MediaToolAdapter 
		 * before any audio samples are processed.
		 */
		@Override
		public void onAddStream(IAddStreamEvent event) {
			log.info("IAddStreamEvent: {}", event.getSource());

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

	/*
	 * The ReaderAudioResampler is used to re-sample the incoming IAudioSamples. This class uses an
	 * IAudioResampler to perform the actual re-sampling. This class extends MediaToolAdapter and
	 * @Override(s) onAudioSamples to gain access to the IAudioSamplesEvent(s), which arrive as the
	 * IMediaReader reads a stream.
	 */
	static class ReaderAudioResampler extends MediaToolAdapter {

		// Private data
		private IAudioResampler resampler;
		private int sampleRate;
		private int channels;

		// AudioResampler constructor
		public ReaderAudioResampler(int newSampleRate, int newChannels) {
			this.sampleRate = newSampleRate;
			this.channels = newChannels;
			log.info("Created an ReaderAudioResampler: {}/{}", newSampleRate, newChannels);
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
		public void onAudioSamples(IAudioSamplesEvent event) {

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
}