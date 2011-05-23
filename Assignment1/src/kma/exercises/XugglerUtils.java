package kma.exercises;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * OVERVIEW
 * The XugglerUtils contains static utility methods designed to encapsulate 
 * common Xuggler tasks. This class can be used in future projects as a way of speeding
 * development. This class is based on source-code written by Art Clarke from Xuggle Inc.
 * 
 * TODO
 * 1. Complete the 'logMediaMetadata' method
 * 2. Complete and run the 'main' method
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
public class XugglerUtils {
	/*
	 * Create a Logger object specific to this class. For details on the 
	 * Simple Logging Facade for Java (SLF4J), see: http://www.slf4j.org/
	 */
	private static final Logger log = LoggerFactory.getLogger(XugglerUtils.class);
	
	/**
	 * Simple main method
	 */
	public static void main(String[] args) {
		log.info("Starting tests...");

		/*
		 * -----------------------------------------------------------------------------------
		 * !!! LAB EXERCISE !!!
		 * -----------------------------------------------------------------------------------
		 * 1. Before beginning this exercise, download 2 sample video files from the course 
		 *    exercise page (for details, please see the accompanying slide presentation).
		 *    
		 * 2. Prepare an Eclipse "Run configuration" for the XugglerUtils class.
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
		File f = new File(inputFilePath);
		if (!f.exists())
			throw new IllegalArgumentException("Valid input file path required!");

		// Test the logMediaMetadata method with an example file
		logMediaMetadata(inputFilePath);

		log.info("Tests complete!");
	}

	/*
	 * Creates an IContainer from the incoming filename and logs a variety of media meta-data for each
	 * IStream contained within the container. Both audio and video meta-data are supported.
	 * 
	 * @param filename
	 *            A string that represents a filename (including the full path)
	 */
	public static void logMediaMetadata(String filename) {
		
		log.info("Logging meta-data from: {}", filename);
		
		// Setup local variables
		int numStreams = 0;
		
		// Create a Xuggler IContainer object.
		IContainer container = IContainer.make();

		// Open up the container using READ access
		if (container.open(filename, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("Could not open: " + filename);

		/* -----------------------------------------------------------------------------------
		 * !!! LAB EXERCISE !!!
		 * -----------------------------------------------------------------------------------
		 * Log the following meta-data related to the IContainer using the Logger (defined above)
		 * 1. Assign 'numStreams' to the number of streams in the 'container' and log the result.
		 *    Hint: Use 'container.getNumStreams()'
		 * 2. Duration (in milliseconds)
		 *    
		 * 3. File size (in bytes)
		 *    
		 * 4. Bit-rate
		 *    
		 * 
		 * Logging examples:
		 * Logging 1: log.info("Info related to the IContainer"); // No parameters
		 * Logging 2: log.info("The new entry is {}. It replaces {}.", entry, oldEntry); // 1 or 2 parameters
		 * Logging 3: log.info("Value {} was inserted between {} and {}.", new Object[] {newVal, below, above}); // 3 or more parameters
		 */			
		
		// ===================================================================================
		// *** YOUR CODE HERE ***
		numStreams = container.getNumStreams();
		log.info("Number of streams: {}", numStreams);
		
		log.info("Duration: {} ms", container.getDuration());
		
		log.info("File size: {} bytes", container.getFileSize());
		
		log.info("Bit-rate: {}", container.getBitRate());
		// ===================================================================================
		
		/*
		 * Iterate through the IStream entities within the IContainer, 
		 * logging meta-data for each IStream.
		 */
		for (int i = 0; i < numStreams; i++) {
			
			// Access the associated IStream object within the IContainer
			IStream stream = container.getStream(i);
			
			// Get the pre-configured IStreamCoder from the stream object
			IStreamCoder coder = stream.getStreamCoder();
			
			/* -----------------------------------------------------------------------------------
			 * !!! LAB EXERCISE !!!
			 * -----------------------------------------------------------------------------------
			 * Log the following meta-data for each IStream within the IContainer
			 * 1. The stream's codec type
			 *    Hint: 'coder.getCodecType()'	
			 *    		
			 * 2. The stream's codec ID
			 *    
			 * 3. The stream's duration
			 *    Hint: The duration of IStream objects will be returned in the time units of the 
			 *    underlying media format. This can vary widely and may not relate to milliseconds directly.
			 * 4. The stream's start time
			 *    Hint: Streams will have an unknown start time if their IContainer has an unknown 
			 *    start time. You can check the IContainer using: 'container.getStartTime() == Global.NO_PTS'
			 *    where 'Global.NO_PTS' means no time stamp is set for a given object.
			 * 5. The stream's language (not all streams support this)
			 *    
			 * 6. The stream's time base
			 *    Hint: get both the Numerator and the Denominator
			 *    
			 * 7. The stream coder's time base
			 *    Hint: get both the Numerator and the Denominator
			 *    
			 */				
						
			// ===================================================================================
			// *** YOUR CODE HERE ***
			log.info("Stream's codec type: {}", coder.getCodecType());
			
			log.info("Stream's codec ID: {}", coder.getCodecID());
			
			log.info("Stream's duration: {} time units", stream.getDuration());
			
			log.info("Stream's start time: {}", stream.getStartTime());
			
			log.info("Stream's language: {}", stream.getLanguage());
			
			log.info("Stream's time base: {} (Numerator), {} (Denominator)", stream.getTimeBase().getNumerator(), stream.getTimeBase().getDenominator());
			
			log.info("Stream coder's time base: {} (Numerator), {} (Denominator)", coder.getTimeBase().getNumerator(), coder.getTimeBase().getDenominator());
			// ===================================================================================
			
			/* -----------------------------------------------------------------------------------
			 * !!! LAB EXERCISE !!!
			 * -----------------------------------------------------------------------------------
			 * Next, log media-specific meta-data depending on the type of the stream (audio or video).
			 * Note that you can check the stream type by comparing 'coder.getCodecType()' with 
			 * either 'ICodec.Type.CODEC_TYPE_AUDIO' or 'ICodec.Type.CODEC_TYPE_VIDEO'
			 * 
			 * If the stream is an audio type, log the following information:
			 * 1. Sample rate
			 * 2. Number of channels
			 * 3. Sample format
			 * 			
			 * If the stream is a video type, log the following information:
			 * 1. Frame height and width
			 * 2. Pixel type
			 * 3. Frame rate
			 */
			
			// ===================================================================================
			// *** YOUR CODE HERE ***
			if (coder.getCodecType().equals(ICodec.Type.CODEC_TYPE_AUDIO))
			{
				log.info("Audio stream sample rate: {}", coder.getSampleRate());
				
				log.info("Audio stream number of channels: {}", coder.getChannels());
				
				log.info("Audio stream sample format: {}", coder.getSampleFormat());				
			}
			else if (coder.getCodecType().equals(ICodec.Type.CODEC_TYPE_VIDEO))
			{
				log.info("Video frame measurements: {} (height), {} (width)", coder.getHeight(), coder.getWidth());
				
				log.info("Video pixel type: {}", coder.getPixelType());
				
				log.info("Video frame rate: {} fps", coder.getFrameRate());				
			}
			// ===================================================================================
		}
		
		// Finally, close the container to release resources
		container.close();
	}
	
	/*
	 * Close and safely release all resources related to the incoming IContainer
	 */
	public static void closeContainer(IContainer container) {
		log.info("Closing {}", container);
		
		// Local variables
		int i;
		int numStreams = container.getNumStreams();
		
		/*
		 * Finalize the streams for writable IContainers
		 * 
		 * Some video coders (e.g. MP3) will often "read-ahead" in a stream and
		 * keep extra data around to get efficient compression. But they need
		 * some way to know they're never going to get more data. The convention
		 * for that case is to pass null for the IMediaData (e.g. IAudioSamples
		 * or IVideoPicture) in encodeAudio(...) or encodeVideo(...) once before
		 * closing the coder.
		 * 
		 * In that case, the IStreamCoder will flush all data.
		 */
		if(container.getType() == IContainer.Type.WRITE) {
			for (i = 0; i < numStreams; i++) {
				IStreamCoder c = container.getStream(i).getStreamCoder();
				if (c != null) {
					IPacket oPacket = IPacket.make();
					if (c.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
						c.encodeVideo(oPacket, null, 0);
					if (oPacket.isComplete())
						container.writePacket(oPacket, true);				
				}
			}
		}
		
		/*
		 * Some container formats require a trailer to be written to avoid a
		 * corrupt files.
		 * 
		 * Others, such as the FLV container muxer, will take a writeTrailer()
		 * call to tell it to seek() back to the start of the output file and
		 * write the (now known) duration into the Meta Data.
		 * 
		 * So trailers are required. In general if a format is a streaming
		 * format, then the writeTrailer() will never seek backwards.
		 * 
		 * Make sure you don't close your codecs before you write your trailer,
		 * or we'll complain loudly and not actually write a trailer.
		 */
		if(container.getType() == IContainer.Type.WRITE) {
			int retval = container.writeTrailer();
			if (retval < 0)
				throw new RuntimeException("Could not write trailer to output file");
		}
			
		// Release all IStreamCoder resources
		for (i = 0; i < numStreams; i++) {
			IStreamCoder c = container.getStream(i).getStreamCoder();
			if (c != null) {
				c.close();				
			}
		}
		
		/*
		 * Tell Xuggler it can close the output file, write all data, and free
		 * all relevant memory.
		 */
		container.close();
		container = null;

		log.info("Finished closing container!");
	}
}
