// Copyright 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
