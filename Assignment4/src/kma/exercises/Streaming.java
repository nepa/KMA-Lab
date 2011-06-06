package kma.exercises;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * OVERVIEW The Streaming class transcodes media files using techniques from the
 * Xuggler Advanced API. This class is intended to be configured with an "rtmp"
 * based IContainer that writes media to a compatible media server for
 * reflection back to RTP-based clients. While the advanced API is considerably
 * more complex than the MediaTool API, it provides low-level access to
 * IContainer, IStream and IStreamCoder settings. To help clarify Xuggler's
 * low-level media handling and streaming concepts, this class only processes
 * the first discover audio stream, leaving video and combined audio/video
 * streaming as an exercise for the interested student. This class is based on
 * source-code written by Art Clarke from Xuggle Inc.
 * 
 * TODO 1. Review the 'setupStreams' method 2. Complete the 'stream' method 3.
 * Review the 'XugglerUtils.closeContainer()' method (optional) 3. Complete and
 * run the 'main' method
 * 
 * Hint: - Read the sections marked: !!! LAB EXERCISE !!! - Complete the
 * sections marked: *** YOUR CODE HERE *** - Have fun!
 * 
 * Note: This class is intentionally incomplete and should be finished by the
 * student. To help guide your work, this class includes a partial structure
 * along with embedded comments, hints and links. An accompanying in-class
 * presentation will provide the background information and instructions
 * necessary to complete this exercise.
 * 
 * REFERENCES The Xuggler source-code can be found at:
 * http://code.google.com/p/xuggle/source/browse/
 * 
 * The Xuggler Java documentation can be found at:
 * http://build.xuggle.com/view/Stable
 * /job/xuggler_jdk5_stable/javadoc/java/api/index.html
 * 
 * A full encoder example, including both audio and video, can be found here:
 * http
 * ://code.google.com/p/xuggle/source/browse/trunk/java/xuggle-xuggler/src/com
 * /xuggle/xuggler/Converter.java
 * 
 */
public class Streaming {
	/*
	 * Create a Logger object specific to this class. For details on the Simple
	 * Logging Facade for Java (SLF4J), see: http://www.slf4j.org/
	 */
	private static final Logger log = LoggerFactory.getLogger(Streaming.class);

	// Setup local variables for the input IContainer
	private static IContainer inContainer = null;
	private static IStream inStream = null;
	private static IStreamCoder inCoder = null;
	private static IAudioSamples inSamples = null;

	// Setup local variables for the output IContainer
	private static IContainer outContainer = null;
	private static IStream outStream = null;
	private static IStreamCoder outCoder = null;

	private static IAudioSamples outSamples = null;
	private static IAudioSamples reSamples = null;
	private static IAudioResampler audioSampler = null;
	private static int audioStreamIndex = 0;

	/*
	 * AudioSettings is a simple 'struct' like inner class that is used to hold
	 * encoding settings. The defaults force the encoder to match source
	 * settings.
	 * 
	 * Note: This type of class should only be used internally for convenience
	 * since it breaks encapsulation by exposing internal variables.
	 */
	public static class AudioSettings {
		// Note: defaults force the encoder to match source settings
		ICodec.ID acodec;
		int astream = -1;
		int aquality = 0; // 0 is full quality
		int sampleRate = 0;
		int channels = 0;
		int abitrate = 0;
	}

	/**
	 * main method
	 */
	public static void main(String[] args) {
		log.info("Starting tests...");

		AudioSettings as = new AudioSettings();
		String containerFormat = "flv";
		as.acodec = ICodec.ID.CODEC_ID_MP3;

		/*
		 * ----------------------------------------------------------------------
		 * ------------- !!! LAB EXERCISE !!!
		 * ------------------------------------
		 * ----------------------------------------------- 1. Before beginning
		 * this exercise, download 1 sample mp3 file from the course exercise
		 * page (for details, please see the accompanying slide presentation).
		 * 
		 * 2. Prepare an Eclipse "Run configuration" for the Streaming class.
		 * 
		 * 3. Set the 'inputFilePath' variable to the complete file path of your
		 * downloaded sample media file. Also, set the 'lastName' variable to
		 * your last name.
		 * 
		 * 5. Run the 'main' method and then start the VLC Player to verify the
		 * stream. In VLC, do the following: Media > Open Network Stream... >
		 * 'rtsp://85.114.135.105:1935/rtplive/<lastName>'
		 * 
		 * 6. Listen to the reflected stream and note the quality and any
		 * unusual audio artifacts.
		 * 
		 * Note 1: Without modification, this class does not work properly! This
		 * is intentional. An important part of this lab is fixing the problem.
		 * See the notes in the comments below for help.
		 * 
		 * Note 2: You may need to adjust the 'AudioSettings' for optimal stream
		 * quality (Try different 'abitrate', 'sampleRate' and 'channels'
		 * settings.)
		 * 
		 * Note 3: This class may only be tested when the media server is
		 * running! Please check with your instructor regarding server
		 * availability.
		 */
		log.info("Your JVM is running on "
				+ System.getProperty("sun.arch.data.model") + " Bit.");

		// Check arguments passed to application
		if (args.length < 1) {
			log.error("Please pass input file when starting application.");
			System.exit(1);
		}

		String inputFilePath = args[0];
		File f = new File(inputFilePath);
		if (!f.exists())
			throw new IllegalArgumentException(
					"Valid input file path required!");

		String lastName = "";
		String serverPath = "rtmp://141.83.68.80:10001/live/" + lastName;
		// String serverPath = "rtmp://85.114.135.105:1935/rtplive/" + lastName;

		stream(inputFilePath, serverPath, containerFormat, as);
		log.info("Tests complete!");
	}

	/*
	 * --------------------------------------------------------------------------
	 * --------- !!! LAB EXERCISE !!!
	 * --------------------------------------------
	 * --------------------------------------- Study the 'setupStreams' method
	 * in detail to gain an understanding of how to setup streams using
	 * Xuggler's advanced API. While the advanced API is considerably more
	 * complex than the MediaTool API, it provides low-level access to
	 * IContainer, IStream, and IStreamCoder settings.
	 * 
	 * As you study this method, note the following: 1. The output IContainer
	 * format can vary from the underlying codec's default container format
	 * (e.g. including MP3 audio within a Flash FLV container).
	 * 
	 * 2. The encoding codec can be specified directly, including its bit-rate.
	 * 
	 * 3. We have precise control over the output sample rate through an
	 * IAudioResampler.
	 * 
	 * 4. Direct access to IAudioSamples is supported.
	 */
	private static void setupStreams(String inputURL, String outputURL,
			String containerFormat, AudioSettings as) {
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
		 * If there was a specified containerFormat (i.e. containerFormat !=
		 * null) then create a specific IContainerFormat for use in creating the
		 * output IContainer
		 * 
		 * Note: This is useful if you need to use a particular container format
		 * with a given media type (e.g. including MP3 audio within a Flash FLV
		 * container)
		 */
		IContainerFormat oFmt = null;
		if (containerFormat != null) {
			oFmt = IContainerFormat.make();
			retval = oFmt.setOutputFormat(containerFormat, outputURL, null);
			if (retval < 0)
				throw new RuntimeException(
						"Coul not find output container format: "
								+ containerFormat);
		}

		/*
		 * Open the output IContainer for writing. If oFmt is null, we are
		 * telling Xuggler to guess the output container format based on the
		 * outputURL. Otherwise, we use the IContainerFormat created above.
		 */
		retval = outContainer.open(outputURL, IContainer.Type.WRITE, oFmt);
		if (retval < 0)
			throw new RuntimeException("could not open output url: "
					+ outputURL);

		/*
		 * Now let's search through the streams to find the first audio stream
		 */
		for (int i = 0; i < inContainer.getNumStreams(); i++) {

			// Get the IStream for this input stream.
			inStream = inContainer.getStream(i);

			/*
			 * Next, get the input stream coder. Xuggler will set up all sorts
			 * of defaults on this StreamCoder for you (such as the audio sample
			 * rate) when you open it.
			 * 
			 * You can create IStreamCoders yourself using
			 * IStreamCoder#make(IStreamCoder.Direction), but then you have to
			 * set all parameters yourself.
			 */
			inCoder = inStream.getStreamCoder();

			// Find out what Codec Xuggler guessed the input stream was encoded
			// with.
			ICodec.Type cType = inCoder.getCodecType();

			// Check for an audio stream, ignore otherwise
			if (cType == ICodec.Type.CODEC_TYPE_AUDIO) {
				log.info("Found an audio stream!");
				audioStreamIndex = i;

				// Since we found an audio stream, add a new stream to our
				// output IContainer
				outStream = outContainer.addNewStream(i);

				// Grab the output stream coder from the 'outStream' and set its
				// codec and quality
				outCoder = outStream.getStreamCoder();

				if (as.acodec != null) {
					outCoder.setCodec(as.acodec);
				} else {
					/*
					 * Looks like the user didn't specify an output coder. So we
					 * ask Xuggler to guess an appropriate output coded based on
					 * the URL and the container format.
					 */
					ICodec codec = ICodec.guessEncodingCodec(oFmt, null,
							outputURL, null, cType);
					if (codec == null)
						throw new RuntimeException("Could not guess " + cType
								+ " encoder for: " + outputURL);
					outCoder.setCodec(codec);
				}

				/*
				 * In general a IStreamCoder encoding audio needs to know: 1) A
				 * ICodec to use. 2) The sample rate and number of channels of
				 * the audio. Most everything else can be defaulted.
				 */

				/*
				 * If the user didn't specify a sample rate for encoding, then
				 * just use the same sample rate as the input.
				 */
				if (as.sampleRate == 0)
					as.sampleRate = inCoder.getSampleRate();
				outCoder.setSampleRate(as.sampleRate);

				/*
				 * If the user didn't specify a bit-rate, then just use the same
				 * bit-rate as the input.
				 */
				if (as.abitrate == 0)
					as.abitrate = inCoder.getBitRate();
				outCoder.setBitRate(as.abitrate);

				/*
				 * If the user didn't specify the number of channels, just
				 * assume we're keeping the same number of channels as the
				 * source.
				 */
				if (as.channels == 0)
					as.channels = inCoder.getChannels();
				outCoder.setChannels(as.channels);

				/*
				 * And set the quality (which defaults to 0, or highest, if the
				 * user doesn't tell us one).
				 */
				outCoder.setGlobalQuality(as.aquality);

				/*
				 * Now check if our output channels or sample rate differ from
				 * our input channels or sample rate.
				 * 
				 * If they do, we're going to need to re-sample the input audio
				 * to be in the right format to output.
				 */
				if (outCoder.getChannels() != inCoder.getChannels()
						|| outCoder.getSampleRate() != inCoder.getSampleRate()) {
					/*
					 * Create an audio re-sampler to do that job.
					 */
					audioSampler = IAudioResampler.make(outCoder.getChannels(),
							inCoder.getChannels(), outCoder.getSampleRate(),
							inCoder.getSampleRate());
					if (audioSampler == null) {
						throw new RuntimeException(
								"Could not open audio resampler for stream: "
										+ i);
					}
				} else {
					audioSampler = null;
				}

				/*
				 * Finally, create some buffers for the input and output audio
				 * themselves. We'll use these repeatedly during the 'stream'
				 * method.
				 */
				inSamples = IAudioSamples.make(1024, inCoder.getChannels());
				outSamples = IAudioSamples.make(1024, outCoder.getChannels());

				/*
				 * Now, once you've set up all the parameters on the
				 * StreamCoder, you must open() them so they can do work.
				 * 
				 * They will return an error if not configured correctly, so we
				 * check for that here.
				 */
				if (outCoder != null) {
					retval = outCoder.open();
					if (retval < 0)
						throw new RuntimeException(
								"Could not open output encoder for stream: "
										+ i);
					retval = inCoder.open();
					if (retval < 0)
						throw new RuntimeException(
								"Could not open input decoder for stream: " + i);
				}

				// Since we found an audio stream, break out of the loop
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
			throw new RuntimeException("Could not write header for: "
					+ outputURL);

		log.info("Finished setting up streams!");
	}

	/*
	 * --------------------------------------------------------------------------
	 * --------- !!! LAB EXERCISE !!!
	 * --------------------------------------------
	 * --------------------------------------- Study the 'stream' method in
	 * detail to gain an understanding of how to encode streams using Xuggler's
	 * advanced API. While the advanced API is considerably more complex than
	 * the MediaTool API, it provides low-level access to IContainer, IStream,
	 * and IStreamCoder settings.
	 * 
	 * Note: There is one code-block below that should be completed by the
	 * student.
	 */
	public static void stream(String inputURL, String serverPath, String containerFormat, AudioSettings as) {
		// Setup the input and output streams
		setupStreams(inputURL, serverPath, containerFormat, as);

		log.info("Starting to stream to: {}", serverPath);

		// Create a local return value (for error checking)
		int retval = 0;

		// Create packet buffers for reading data from and writing data to the
		// containers.
		IPacket iPacket = IPacket.make();
		IPacket oPacket = IPacket.make();

		/*
		 * Since we've already opened the files in 'setupStreams', we can just
		 * keep reading packets from the inContainer until the IContainer
		 * 'readNextPacket' returns < 0
		 * 
		 * Note: 'inContainer.readNextPacket' reads the next packet in the
		 * container into the iPacket buffer created above. Note: Multiple audio
		 * samples will be present within the resulting iPacket
		 */
		int i = 0;
		while (inContainer.readNextPacket(iPacket) == 0) {

			/*
			 * In this example, we only process packets belonging to the audio
			 * stream. Note: 'audioStreamIndex' was created in the
			 * 'setupStreams' method called above
			 */
			i = iPacket.getStreamIndex();
			if (i == audioStreamIndex) {

				// Grab the appropriate IStream from the 'inContainer'
				IStream stream = inContainer.getStream(i);

				/*
				 * Adjust 'tsOffset' for an IStream that has a starting
				 * time-stamp that begins later than the start time of the
				 * container.
				 * 
				 * This can be detected if the following are TRUE: 1. The
				 * stream's start time is not unknown (i.e. != Global.NO_PTS) 2.
				 * The stream's start time is greater than zero 3. The stream's
				 * time base is != null
				 */
				long tsOffset = 0;
				if (stream.getStartTime() != Global.NO_PTS
						&& stream.getStartTime() > 0
						&& stream.getTimeBase() != null) {
					IRational defTimeBase = IRational.make(1,
							(int) Global.DEFAULT_PTS_PER_SECOND);
					tsOffset = defTimeBase.rescale(stream.getStartTime(),
							stream.getTimeBase());
				}

				// Verify that the stream is indeed audio
				ICodec.Type cType = inCoder.getCodecType();
				if (cType == ICodec.Type.CODEC_TYPE_AUDIO) {

					// Setup necessary local variables
					int offset = 0;

					/*
					 * Decoding audio works by taking the data in the packet,
					 * and eating chunks from it to create decoded raw data.
					 * 
					 * However, there may be more data in a packet than is
					 * needed to get one set of samples (or less), so you need
					 * to iterate through the bytes to get that data.
					 * 
					 * The following loop is the standard way of doing that.
					 */
					while (offset < iPacket.getSize()) {
						/*
						 * ------------------------------------------------------
						 * ----------------------------- !!! LAB EXERCISE !!!
						 * ----
						 * --------------------------------------------------
						 * ----------------------------- In this section, we
						 * will decode the packet into the 'inSamples' buffer.
						 * We will also handle any audio re-sampling. Finally,
						 * we will encode the 'inSamples' buffer into an output
						 * packet and write it to the 'outContainer'.
						 * 
						 * Steps: 1. Use the 'inCoder' to decode some audio
						 * samples from the iPacket into the 'inSamples' buffer
						 * using the 'inCoder.decodeAudio()' method. Use the
						 * Link below to review the parameters of the
						 * 'decodeAudio()' method. Hint: Store the return value
						 * (int) because it represents the number of bytes
						 * processed during the decode (or negative for error).
						 * Link: http://build.xuggle.com/view/Stable/job/
						 * xuggler_jdk5_stable
						 * /javadoc/java/api/com/xuggle/xuggler
						 * /IStreamCoder.html
						 * 
						 * 2. Add the bytes returned from
						 * 'inCoder.decodeAudio()' to the 'offset' variable
						 * 
						 * 3. If the 'inSamples' time-stamp is known, adjust for
						 * any 'tsOffset' Hint: First, check 'if
						 * (inSamples.getTimeStamp() != Global.NO_PTS)' If true,
						 * set 'inSamples.setTimeStamp(inSamples.getTimeStamp()
						 * - tsOffset);' If false, do nothing.
						 * 
						 * 4. If re-sampling is requested, and there are more
						 * than 0 samples to process, then re-sample the audio.
						 * Hint: Check 'if (audioSampler != null &&
						 * inSamples.getNumSamples() > 0) Hint: If re-sampling
						 * is requested, use the 'audioSampler' to re-sample the
						 * 'inSamples' Use: 'audioSampler.resample(reSamples,
						 * inSamples, inSamples.getNumSamples());' Make sure to
						 * set 'outSamples = reSamples;' once finished. Hint: If
						 * re-sampling is NOT requested, simply set 'outSamples
						 * = inSamples;'
						 */
						// ===================================================================================
						// *** YOUR CODE HERE ***
						retval = inCoder
								.decodeAudio(inSamples, iPacket, offset);

						if (retval < 0) {
							throw new RuntimeException("Could not decode audio samples from iPacket into inSamples buffer.");
						}

						offset += retval;

						if (inSamples.getTimeStamp() != Global.NO_PTS) {
							inSamples.setTimeStamp(inSamples.getTimeStamp() - tsOffset);
						}

						// Check whether re-sampling was requested
						if (audioSampler != null
								&& inSamples.getNumSamples() > 0) {
							audioSampler.resample(reSamples, inSamples, inSamples.getNumSamples());

							outSamples = reSamples;
						} else {
							outSamples = inSamples;
						}
						// ===================================================================================

						/*
						 * Now that we've re-sampled, it's time to encode the
						 * audio.
						 * 
						 * This work-flow is similar to decoding; you may have
						 * more, less or just enough audio samples available to
						 * encode a packet. But you must iterate through.
						 * 
						 * Unfortunately (don't ask why) there is a slight
						 * difference between encodeAudio and decodeAudio.
						 * 
						 * encodeAudio returns the number of samples consumed,
						 * NOT the number of bytes. This can be confusing, and
						 * we encourage you to read the IAudioSamples
						 * documentation to find out what the difference is.
						 * 
						 * But in any case, the following loop encodes the
						 * samples we have into packets.
						 */
						int numSamplesConsumed = 0;
						while (numSamplesConsumed < outSamples.getNumSamples()) {
							retval = outCoder.encodeAudio(oPacket, outSamples,
									numSamplesConsumed);
							if (retval <= 0)
								throw new RuntimeException(
										"Could not encode any audio: " + retval);
							/*
							 * Increment the number of samples consumed, so that
							 * the next time through this loop we encode new
							 * audio
							 */
							numSamplesConsumed += retval;
							if (oPacket.isComplete()) {
								/*
								 * ----------------------------------------------
								 * ------------------------------------- !!! LAB
								 * EXERCISE !!!
								 * ----------------------------------
								 * ------------
								 * ------------------------------------- Without
								 * modification, this class does not work
								 * properly! This is intentional. An important
								 * part of this lab is fixing the problem. The
								 * problem lies in this code section. You will
								 * find a method below named
								 * 'StreamingHelper.fixProblem' that is a static
								 * method of an inner class called
								 * 'StreamingHelper'. You should complete
								 * 'StreamingHelper' to fix the problem. Begin
								 * by reading through the preliminary hints
								 * below and then read through the hints in the
								 * 'StreamingHelper' class.
								 * 
								 * Hints: 1. Note that this class simulates a
								 * "live" streaming scenario, where incoming
								 * media should be encoded and streamed to
								 * clients in real-time. Real-time, in this
								 * case, means that media data is sent to
								 * clients at approximately the source's native
								 * playback rate. 2. How long do you think it
								 * takes to complete the decoding and encoding
								 * of each audio packet? 3. What happens during
								 * 'outContainer.writePacket' when writing to an
								 * RTMP IContainer? 4. Consider where the
								 * 'StreamingHelper.fixProblem' is called within
								 * the encoding process.
								 */
								StreamingHelper.fixProblem(inSamples);

								/*
								 * If we got a complete packet out of the
								 * encoder, then go ahead and write it to the
								 * container.
								 */
								retval = outContainer.writePacket(oPacket, true);
								if (retval < 0)
									throw new RuntimeException(
											"could not write output packet");
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

	/*
	 * StreamingHelper is a utility class that fixes the streaming problem
	 * introduced by the 'stream' method above. This class provides a static
	 * 'fixProblem(IAudioSamples samples)' method that should be completed by
	 * the student. Note that 'fixProblem' is called repeatedly by the 'stream'
	 * method during packet processing.
	 * 
	 * Hint 1: Study the 'DecodeAndPlay' sample code provided by following the
	 * 'Link' below. While 'DecodeAndPlay' is specifically designed for video,
	 * we can adapt portions of the code to fix our audio streaming problems.
	 * 
	 * To adapt the code, read through 'DecodeAndPlay', paying special attention
	 * to the section of code following 'if (picture.isComplete())'. Note that
	 * this section of code is very similar to the 'if (oPacket.isComplete())'
	 * section from the 'stream' method above. Make sure to read the in-line
	 * comments of 'DecodeAndPlay' carefully.
	 * 
	 * Hint 2: Remember that all Xuggler IAudioSamples objects always give
	 * time-stamps in Microseconds, relative to the first decoded item (simply
	 * divide by 1000 to get milliseconds).
	 * 
	 * Hint 3: Note that you receive an 'IAudioSamples' object each time the
	 * 'fixProblem' method is called by 'stream'. Pay special attention to
	 * 'samples.getTimeStamp()'.
	 * 
	 * Link:
	 * http://xuggle.googlecode.com/svn/trunk/java/xuggle-xuggler/src/com/xuggle
	 * /xuggler/demos/DecodeAndPlayVideo.java
	 */
	private static class StreamingHelper {

		private static long systemClockStartTime = 0;
		private static long firstTimestampInStream = Global.NO_PTS;

		public static void fixProblem(IAudioSamples samples) {
			/*
			 * ------------------------------------------------------------------
			 * ----------------- !!! LAB EXERCISE !!!
			 * ----------------------------
			 * ------------------------------------------------------- Complete
			 * the 'fixProblem' method using the above hints and 'DecodeAndPlay'
			 * as a template.
			 */
			// ===================================================================================
			// *** YOUR CODE HERE ***
			
			// Do we run through this for the very first time?
			if (firstTimestampInStream == Global.NO_PTS)
			{
				// Store start time
				firstTimestampInStream = samples.getTimeStamp();
				systemClockStartTime = System.currentTimeMillis();
			}
			// We have been through the if-part already
			else
			{
				long systemClockCurrentTime = System.currentTimeMillis();
				long millisecondsClockTimeSinceStartOfAudio = systemClockCurrentTime - systemClockStartTime;

				// How much time elapsed since the first sample?
				long millisecondsStreamTimeSinceStartOfAudio = (samples.getTimeStamp() - firstTimestampInStream) / 1000; // microseconds, so we divide by 1000 to get ms
				final long millisecondsTolerance = 50; // some 50 ms of tolerance
				final long millisecondsToSleep = (millisecondsStreamTimeSinceStartOfAudio - (millisecondsClockTimeSinceStartOfAudio + millisecondsTolerance));

				// Sleep for a couple of ms to hold back sample
				if (millisecondsToSleep > 0)
				{
					try
					{
						Thread.sleep(millisecondsToSleep);
					}
					catch (InterruptedException e)
					{
						// Just return to normal flow.
						return;
					}
				}
			}
			// ===================================================================================
		}
	}
}
