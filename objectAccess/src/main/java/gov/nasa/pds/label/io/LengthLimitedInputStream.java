package gov.nasa.pds.label.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implements a stream that wraps another input stream, but limits
 * the portion read.
 */
public class LengthLimitedInputStream extends FilterInputStream {

	private long available;
	
	/**
	 * Creates a new instance wrapping the given input stream.
	 * 
	 * @param in the underlying input stream
	 * @param offset the offset from the start of the stream to begin reading
	 * @param size the size of the portion of the data to read
	 * @throws IOException if there is an error accessing the underlying input stream
	 */
	public LengthLimitedInputStream(InputStream in, long offset, long size) throws IOException {
		super(in);
		available = size;
		in.skip(offset);
	}

	@Override
	public int available() throws IOException {
		return (int) Math.min(available, Integer.MAX_VALUE);
	}

	@Override
	public int read() throws IOException {
		if (available <= 0) {
			return -1;
		} else {
			int c = super.read();
			if (c >= 0) {
				--available;
			} else {
				available = 0;
			}
			return c;
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int chunkSize = (int) Math.min(len, available);
		int nRead = super.read(b, off, chunkSize);
		if (nRead >= 0) {
			available -= nRead;
		} else {
			available = 0;
		}
		return nRead;
	}

	@Override
	public long skip(long n) throws IOException {
		long actualSkip = super.skip(Math.min(n, available));
		available -= actualSkip;
		return actualSkip;
	}

}
