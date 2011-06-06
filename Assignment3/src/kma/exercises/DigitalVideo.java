package kma.exercises;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;

/**
 * OVERVIEW
 * The DigitalVideo class transcodes media files using techniques from the
 * Xuggler Advanced API. While the advanced API is considerably more
 * complex than the MediaTool API, it provides low-level access to IContainer, IStream,
 * and IStreamCoder objects and settings. To help clarify Xuggler's low-level media handling
 * concepts, this class only processes the first discover video stream, leaving 
 * audio and combined audio/video encoding as an exercise for the interested student. 
 * This class is based on source-code written by Art Clarke from Xuggle Inc.
 * 
 * TODO
 * 1. Review the 'setupStreams' method
 * 2. Complete the 'encodeVideo' method
 * 3. Review the 'XugglerUtils.closeContainer()' method (optional)
 * 3. Complete and run the 'main' method 
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
 * A full encoder example, including both audio and video, can be found here:
 * http://code.google.com/p/xuggle/source/browse/trunk/java/xuggle-xuggler/src/com/xuggle/xuggler/Converter.java
 * 
 */
public class DigitalVideo {
	/*
	 * Create a Logger object specific to this class. For details on the 
	 * Simple Logging Facade for Java (SLF4J), see: http://www.slf4j.org/
	 */
	private static final Logger log = LoggerFactory.getLogger(DigitalVideo.class);

	// Setup local variables for the input IContainer
	private static IContainer inContainer = null;
	private static IStream inStream = null;
	private static IStreamCoder inCoder = null;
	private static IVideoPicture inVideoPicture = null;

	// Setup local variables for the output IContainer
	private static IContainer outContainer = null;
	private static IStream outStream = null;
	private static IStreamCoder outCoder = null;
	private static IVideoPicture outVideoPicture = null;
	private static IVideoResampler outVSampler = null;
	private static int videoStreamIndex = 0;

	/*
	 * VideoSettings is a simple 'struct' like inner class that is used to hold 
	 * encoding settings. The defaults force the encoder to match source settings.
	 * 
	 * Note: This type of class should only be used internally for convenience since
	 * it breaks encapsulation by exposing internal variables.
	 */
	private static class VideoSettings {
		ICodec.ID vcodec;
		int vbitrate = 0;
		int vbitratetolerance = 0;
		int vquality = 0; // 0 is full quality
		double vscaleFactor = 1.0;
	}

	/**
	 * main method
	 */
	public static void main(String[] args) {
		log.info("Starting tests...");

		/*
		 * -----------------------------------------------------------------------------------
		 * !!! LAB EXERCISE !!!
		 * -----------------------------------------------------------------------------------
		 * 1. Before beginning this exercise, download 1 sample video file from the course 
		 *    exercise page (for details, please see the accompanying slide presentation).
		 *    
		 * 2. Prepare an Eclipse "Run configuration" for the DigitalVideo class.
		 * 
		 * 3. Set the 'inputFilePath' variable to the complete file path of your 
		 *    downloaded sample media file and then run the 'main' method.
		 *    
		 * 4. First, run 'main' using 'Video Settings for CODEC_ID_FLV1'.
		 * 
		 * 5. Second, run 'main' using 'Video Settings for CODEC_ID_RAWVIDEO'.
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
		
		/*
		 * Prepare the outputFile path. Note: We naively replace the source file's extension 
		 * with "flv" using string replace. Clearly, this method is not robust and 
		 * assumes that the input file will be of the correct type.
		 * Don't use such tricks in production code :)
		 */
		String ext = "";
		int i = f.getPath().lastIndexOf('.');
		if (i > 0 && i < f.length() - 1) {
			ext = f.getPath().substring(i + 1).toLowerCase();
		}
		String transcodePath = f.getPath().replace(ext, "flv");
	
		VideoSettings vs = new VideoSettings();
		
		// Video Settings for CODEC_ID_FLV1
		// ------------------------------------
		// String containerFormat = "flv";		
		// vs.vcodec = ICodec.ID.CODEC_ID_FLV1;		
		// vs.vscaleFactor = 0.5;	
		// vs.vbitrate = 100000;
		
		// Video Settings for CODEC_ID_RAWVIDEO
		// ------------------------------------
		String containerFormat = "avi";
		vs.vcodec = ICodec.ID.CODEC_ID_RAWVIDEO;
		
		// Transcode the video file
		transcodeVideo(inputFilePath, transcodePath, containerFormat, vs);
		log.info("Tests complete!");
	}
	
	/*
	 * -----------------------------------------------------------------------------------
	 * !!! LAB EXERCISE !!!
	 * -----------------------------------------------------------------------------------
	 * Review the 'setupStreams' method in detail to gain an understanding of how to setup
	 * streams using Xuggler's advanced API. While the advanced API is considerably more
	 * complex than the MediaTool API, it provides low-level access to IContainer, IStream,
	 * and IStreamCoder settings.
	 * 
	 * As you study this method, note the following:
	 * 1. The output IContainer format can vary from the underlying codec's default 
	 *    container format (e.g. including MP3 audio within a Flash FLV container). 
	 *    
	 * 2. The encoding codec can be specified directly, including its bit-rate.
	 * 
	 * 3. We have precise control over the output video's size through an IVideoResampler.
	 * 
	 * 4. Direct access to IVideoPicture buffers is supported (providing direct access to
	 *    individual video frames).
	 */
	private static void setupStreams(String inputURL, String outputURL, String containerFormat, VideoSettings vs) {
		log.info("Setting up streams...");

		// Create a local return value (for error checking)
		int retval = 0;

		// Create the input and output IContainer(s)
		inContainer = IContainer.make();
		outContainer = IContainer.make();

		// Open the input IContainer for reading
		retval = inContainer.open(inputURL, IContainer.Type.READ, null);
		if (retval < 0)
			throw new RuntimeException("Could not open: " + inputURL);

		/*
		 * If there was a specified containerFormat (i.e. containerFormat != null) then
		 * create a specific IContainerFormat for use in creating the output IContainer
		 * 
		 * Note: This is useful if you need to use a particular container format with a
		 * given media type (e.g. including MP3 audio within a Flash FLV container)
		 */
		IContainerFormat oFmt = null;
		if (containerFormat != null) {
			oFmt = IContainerFormat.make();
			retval = oFmt.setOutputFormat(containerFormat, outputURL, null);
			if (retval < 0)
				throw new RuntimeException("Could not find output container format: " + containerFormat);
		}

		/*
		 * Open the output IContainer for writing. If oFmt is null, we are
		 * telling Xuggler to guess the output container format based on the
		 * outputURL. Otherwise, we use the IContainerFormat created above.
		 */
		retval = outContainer.open(outputURL, IContainer.Type.WRITE, oFmt);
		if (retval < 0)
			throw new RuntimeException("could not open output url: " + outputURL);

		/*
		 * Now let's search through the streams to find the first video stream
		 */
		for (int i = 0; i < inContainer.getNumStreams(); i++) {

			// Get the IStream for this input stream.
			inStream = inContainer.getStream(i);

			/*
			 * Next, get the input stream coder. Xuggler will set up all sorts of
			 * defaults on this StreamCoder for you (such as the audio sample
			 * rate) when you open it.
			 * 
			 * You can create IStreamCoders yourself using
			 * IStreamCoder#make(IStreamCoder.Direction), but then you have to
			 * set all parameters yourself.
			 */
			inCoder = inStream.getStreamCoder();

			// Find out what Codec Xuggler guessed the input stream was encoded with.
			ICodec.Type cType = inCoder.getCodecType();

			// Check for a video stream, ignore otherwise
			if (cType == ICodec.Type.CODEC_TYPE_VIDEO) {
				log.info("Found a video stream!");
				videoStreamIndex = i;

				// Since we found a video stream, add a new stream to our output IContainer
				outStream = outContainer.addNewStream(i);

				// Grab the output stream coder from the 'outStream' and set its codec and quality
				outCoder = outStream.getStreamCoder();
				if (vs.vcodec != null) {
					outCoder.setCodec(vs.vcodec);
					outCoder.setGlobalQuality(0);
				} else {
					/*
					 * Looks like the user didn't specify an output coder. 
					 * So we ask Xuggler to guess an appropriate output coded based on the URL
					 * and the container format.
					 */
					ICodec codec = ICodec.guessEncodingCodec(oFmt, null, outputURL, null, cType);
					if (codec == null)
						throw new RuntimeException("Could not guess " + cType + " encoder for: " + outputURL);
					outCoder.setCodec(codec);
				}

				/*
				 * In general a IStreamCoder encoding video needs to know:
				 * 1) A ICodec to use.
				 * 2) The Width and Height of the Video.
				 * 3) The pixel format (e.g. IPixelFormat.Type#YUV420P) of
				 * the video data. Most everything else can be defaulted.
				 */
				
				/*
				 * If the user didn't specify a bit-rate for encoding, then
				 * just use the same bit-rate as the input.
				 */
				if (vs.vbitrate == 0)
					vs.vbitrate = inCoder.getBitRate();
				outCoder.setBitRate(vs.vbitrate);
				
				// vbitratetolerance indicates the ability to tolerate variations in the rate that bits pass a point.
				if (vs.vbitratetolerance > 0)
					outCoder.setBitRateTolerance(vs.vbitratetolerance);

				int oWidth = inCoder.getWidth();
				int oHeight = inCoder.getHeight();

				if (oHeight <= 0 || oWidth <= 0)
					throw new RuntimeException("could not find width or height in url: " + inputURL);

				/*
				 * For this program we don't allow the user to specify the pixel
				 * format type, so we force the output to be the same as the input.
				 */
				outCoder.setPixelType(inCoder.getPixelType());

				// Setup video scaling, if needed
				if (vs.vscaleFactor != 1.0) {
					
					/*
					 * In this case, it looks like the output video requires
					 * re-scaling, so we create a IVideoResampler to do that
					 * dirty work.
					 */
					oWidth = (int) (oWidth * vs.vscaleFactor);
					oHeight = (int) (oHeight * vs.vscaleFactor);

					outVSampler = IVideoResampler.make(oWidth, oHeight, outCoder.getPixelType(),
							inCoder.getWidth(), inCoder.getHeight(), inCoder.getPixelType());
					if (outVSampler == null) {
						throw new RuntimeException(
								"This version of Xuggler does not support video resampling " + i);
					}
				} else {
					outVSampler = null;
				}
				
				outCoder.setHeight(oHeight);
				outCoder.setWidth(oWidth);

				outCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
				outCoder.setGlobalQuality(vs.vquality);

				/*
				 * TimeBases are important, especially for Video. In general
				 * Audio encoders will assume that any new audio happens
				 * IMMEDIATELY after any prior audio finishes playing. But for
				 * video, we need to make sure it's being output at the right
				 * rate.
				 * 
				 * In this case we make sure we set the same time base as the
				 * input, and then we don't change the time stamps of any
				 * IVideoPictures.
				 * 
				 * But take my word that time stamps are tricky, and this only
				 * touches the envelope. The good news is, it's easier in
				 * Xuggler than some other systems.
				 */
				IRational num = null;
				num = inCoder.getFrameRate();
				outCoder.setFrameRate(num);
				outCoder.setTimeBase(IRational.make(num.getDenominator(), num.getNumerator()));
				num = null;

				// Allocate buffers for us to store decoded and re-sample video pictures.
				inVideoPicture = IVideoPicture.make(inCoder.getPixelType(), inCoder.getWidth(),
						inCoder.getHeight());
				outVideoPicture = IVideoPicture.make(outCoder.getPixelType(), outCoder.getWidth(),
						outCoder.getHeight());

				/*
				 * Now, once you've set up all the parameters on the StreamCoder,
				 * you must open() them so they can do work.
				 * 
				 * They will return an error if not configured correctly, so we
				 * check for that here.
				 */
				if (outCoder != null) {
					retval = outCoder.open();
					if (retval < 0)
						throw new RuntimeException("Could not open output encoder for stream: " + i);
					retval = inCoder.open();
					if (retval < 0)
						throw new RuntimeException("Could not open input decoder for stream: " + i);
				}

				// Since we found a video stream, break out of the loop
				break;
			} else {
				log.warn("Ignoring input stream {} of type {}", i, cType);
			}
		}

		/*
		 * Pretty much every output container format has a header they need
		 * written, so we do that here.
		 * 
		 * You must configure your output IStreams correctly before writing a
		 * header, and few formats deal nicely with key parameters changing
		 * (e.g. video width) after a header is written.
		 */
		retval = outContainer.writeHeader();
		if (retval < 0)
			throw new RuntimeException("Could not write header for: " + outputURL);

		log.info("Finished setting up streams!");
	}	
	
	/*
	 * -----------------------------------------------------------------------------------
	 * !!! LAB EXERCISE !!!
	 * -----------------------------------------------------------------------------------
	 * Review the 'transcodeVideo' method in detail to gain an understanding of how to encode
	 * streams using Xuggler's advanced API. While the advanced API is considerably more
	 * complex than the MediaTool API, it provides low-level access to IContainer, IStream,
	 * and IStreamCoder settings.
	 * 
	 * Note: There is one code-block below that should be completed by the student.
	 */
	public static void transcodeVideo(String inputURL, String outputURL, String containerFormat, VideoSettings vs) {

		// First, setup the input and output streams
		setupStreams(inputURL, outputURL, containerFormat, vs);

		log.info("Starting to encode: {}", outputURL);
		
		// Create a local return value (for error checking)
		int retval = 0;
		
		// Create IPacket buffers for reading data from and writing data to the containers.
		IPacket iPacket = IPacket.make();
		IPacket oPacket = IPacket.make();

		/*
		 * Since we've already opened the files in setupStreams, we keep reading 
		 * packets from the inContainer until the IContainer 'readNextPacket' returns <0
		 * 
		 * Note: 'inContainer.readNextPacket' reads the next packet in the container into
		 *       the iPacket buffer created above.
		 * Note: Multiple video frames may be present within the resulting iPacket
		 */
		int i = 0;
		while (inContainer.readNextPacket(iPacket) == 0) {

			/*
			 * In this example, we only process packets belonging to the video stream. 
			 * Note: 'videoStreamIndex' was created in the 'setupStreams' method called above
			 */
			i = iPacket.getStreamIndex();
			if (i == videoStreamIndex) {
				
				// Grab the appropriate IStream from the 'inContainer'
				IStream stream = inContainer.getStream(i);
				
				/*
				 * Adjust 'tsOffset' for an IStream that has a starting time-stamp that begins
				 * later than the start time of the container.
				 * 
				 * This can be detected if the following are TRUE:
				 * 1. The stream's start time is not unknown (i.e. != Global.NO_PTS)
				 * 2. The stream's start time is greater than zero
				 * 3. The stream's time base is != null
				 */	
				long tsOffset = 0;
				if (stream.getStartTime() != Global.NO_PTS && stream.getStartTime() > 0 && stream.getTimeBase() != null) {
					IRational defTimeBase = IRational.make(1, (int) Global.DEFAULT_PTS_PER_SECOND);
					tsOffset = defTimeBase.rescale(stream.getStartTime(), stream.getTimeBase());
				}

				// Verify that the stream is indeed video
				ICodec.Type cType = inCoder.getCodecType();
				if (cType == ICodec.Type.CODEC_TYPE_VIDEO) {
					
					// Setup necessary local variables
					int offset = 0;
					IVideoPicture outFrame = null;
										
					// Process the 'iPacket' by decoding its video frames and advancing the 'offset' as we go
					while (offset < iPacket.getSize()) {
						/*
						 * -----------------------------------------------------------------------------------
						 * !!! LAB EXERCISE !!!
						 * -----------------------------------------------------------------------------------
						 * In this section, we will decode the packet into the 'inVideoPicture' buffer. 
						 * We will also handle any video frame resizing.  Finally, we will encode the 
						 * 'inVideoPicture' buffer into an output packet and write it to the 'outContainer'.
						 * 
						 * Note: During video encoding, we always work with a complete IVideoPicture 
						 * (i.e. a full video frame), whereas in audio encoding, we might only consume
						 * some samples in an IAudioSamples buffer.
						 * 
						 * Steps:
						 * 1. Use the 'inCoder' to decode a single video frame from the iPacket into
						 *    the 'inVideoPicture' buffer using the 'inCoder.decodeVideo()' method. 
						 *    Use the Link below to review the parameters of the 'decodeVideo()' method.
						 *    Hint: Store the return value (int) because it represents the number of bytes
						 *          processed during the decode (or negative for error).
						 *    Link: http://build.xuggle.com/view/Stable/job/xuggler_jdk5_stable/javadoc/java/api/com/xuggle/xuggler/IStreamCoder.html
						 *	  Hint: Do not forget to handle exceptions
						 *
						 * 2. Add the bytes returned from 'inCoder.decodeVideo()' to the 'offset' variable
						 * 3. If the 'inVideoPicture' time-stamp is known, adjust for any 'tsOffset'
						 *    Hint: First, check if 'inVideoPicture' time stamp is not equal to Global.NO_PTS
						 *          If true, set 'inVideoPicture' time stamp to 'inVideoPicture' time stamp minus 'tsOffset'
						 *          If false, do nothing.
						 *
						 * 4. Check that the 'inVideoPicture' is complete
						 * 	5. If we have a complete 'inVideoPicture', do the following:
						 *    a. If re-sizing is requested ('if (outVSampler != null)'), re-sample the video frame and 
						 *       assign the result to 'outFrame'
						 *       Hint: If we have an 'outVSampler' then re-size using the 'resample' method, with 'outVideoPicture' and 'inVideoPicture'
						 *    b. If re-sizing is not requested, simply assign 'outFrame' to 'inVideoPicture'
						 *	  Hint: Do not forget to handle exceptions
						 *
						 * 6. Set the default encoding quality on the 'outFrame'. Default quality is indicated by '0'
						 *
						 * 7. Use the 'outCoder' to encode the video from the 'outFrame' into the 'oPacket' buffer using '0' as a suggested buffer size
						 *    Hint: Use 'outCoder.encodeVideo(oPacket, outFrame, 0);'
						 *    Hint: Do not forget to handle exceptions
						 *
						 * 8. Write the encoded 'oPacket' buffer to the 'outContainer'
						 *    Hint: Before writing, verify that 'oPacket' is complete
						 *          If the oPacket is complete, write using: 'writePacket' method
						 *    Hint: Do not forget to handle exceptions
						 */
						// ===================================================================================
						// *** YOUR CODE HERE ***						
						retval = inCoder.decodeVideo(inVideoPicture, iPacket, offset);
						
						if (retval < 0)
						{
							throw new RuntimeException("Could not decode video frame from iPacket into inVideoPicture buffer.");
						}
						
						offset += retval;
						
						if (inVideoPicture.getTimeStamp() != Global.NO_PTS)
						{
							inVideoPicture.setTimeStamp(inVideoPicture.getTimeStamp() - tsOffset);
						}
						
						// Check whether inVideoPicture in complete
						if (inVideoPicture.isComplete())
						{
							// Check whether re-sizing is requested
							if (outVSampler != null)
							{
								// Resample inVideoPicture
								retval = outVSampler.resample(outVideoPicture, inVideoPicture);
								
								if (retval < 0)
								{
									throw new RuntimeException("Could not resample inVideoPicture.");
								}
								
								outFrame = outVideoPicture;
							}
							else
							{								
								outFrame = inVideoPicture;
							}
						
							// Set default quality
							if (outFrame != null)
							{
								outFrame.setQuality(0);
							}
							else
							{
								throw new RuntimeException("The outFrame is null.");
							}
							
							retval = outCoder.encodeVideo(oPacket, outFrame, 0);
							if (retval < 0)
							{
								throw new RuntimeException("Could not encode output video.");
							}
							
							// Write encoded oPacket buffer to the outContainer
							if (oPacket.isComplete())
							{
								outContainer.writePacket(oPacket);
							}
						}
						// ===================================================================================
					}
				} else {
					// Ignore other stream types
					log.trace("ignoring packet of type: {}", cType);
				}
			}
		}

		// Finally, cleanup by closing the containers
		XugglerUtils.closeContainer(inContainer);
		XugglerUtils.closeContainer(outContainer);
		
		log.info("Finished encoding!");
	}
}