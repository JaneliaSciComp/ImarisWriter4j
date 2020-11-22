package org.hhmi.janelia.scicomp.imaris.writer;

public class ImarisWriterError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4566481387779604129L;

	public ImarisWriterError(String vException) {
		super(vException);
	}

	public ImarisWriterError() {
		super();
	}

	public ImarisWriterError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ImarisWriterError(String message, Throwable cause) {
		super(message, cause);
	}

	public ImarisWriterError(Throwable cause) {
		super(cause);
	}
}
