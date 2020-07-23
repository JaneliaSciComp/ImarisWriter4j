package org.hhmi.janelia.scicomp.imaris.writer;

public enum TCompressionAlgorithmType {
	eCompressionAlgorithmNone ( 0 ),
	eCompressionAlgorithmGzipLevel1 ( 1 ),
	eCompressionAlgorithmGzipLevel2 ( 2 ),
	eCompressionAlgorithmGzipLevel3 ( 3 ),
	eCompressionAlgorithmGzipLevel4 ( 4 ),
	eCompressionAlgorithmGzipLevel5 ( 5 ),
	eCompressionAlgorithmGzipLevel6 ( 6 ),
	eCompressionAlgorithmGzipLevel7 ( 7 ),
	eCompressionAlgorithmGzipLevel8 ( 8 ),
	eCompressionAlgorithmGzipLevel9 ( 9 ),
	eCompressionAlgorithmShuffleGzipLevel1 ( 11 ),
	eCompressionAlgorithmShuffleGzipLevel2 ( 12 ),
	eCompressionAlgorithmShuffleGzipLevel3 ( 13 ),
	eCompressionAlgorithmShuffleGzipLevel4 ( 14 ),
	eCompressionAlgorithmShuffleGzipLevel5 ( 15 ),
	eCompressionAlgorithmShuffleGzipLevel6 ( 16 ),
	eCompressionAlgorithmShuffleGzipLevel7 ( 17 ),
	eCompressionAlgorithmShuffleGzipLevel8 ( 18 ),
	eCompressionAlgorithmShuffleGzipLevel9 ( 19 ),
	eCompressionAlgorithmLZ4 ( 21 ),
	eCompressionAlgorithmLShuffleLZ4 ( 31 );

	public final int value;

	TCompressionAlgorithmType(int i) {
		this.value = i;
	}


}
