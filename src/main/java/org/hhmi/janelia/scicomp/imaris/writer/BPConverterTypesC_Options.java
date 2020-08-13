package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mThumbnailSizeXY","mFlipDimensionX","mFlipDimensionY","mFlipDimensionZ","mForceFileBlockSizeZ1","mEnableLogProgress","mNumberOfThreads","mCompressionAlgorithmType"})
public class BPConverterTypesC_Options extends Structure {
	public static class ByReference extends BPConverterTypesC_Options implements Structure.ByReference {}
	public static class ByValue extends BPConverterTypesC_Options implements Structure.ByValue {}

	public int mThumbnailSizeXY = 256; // 256, unsigned int
	public boolean mFlipDimensionX = false; // false, bool
	public boolean mFlipDimensionY = false; // false, bool
	public boolean mFlipDimensionZ = false; // false, bool
	public boolean mForceFileBlockSizeZ1 = false; // false, bool
	public boolean mEnableLogProgress = false; // false, bool
	public int mNumberOfThreads = 8; // 8, unsigned int
	//public TCompressionAlgorithmType mCompressionAlgorithmType = TCompressionAlgorithmType.eCompressionAlgorithmNone; // eCompressionAlgorithmGzipLevel2
	public int mCompressionAlgorithmType = TCompressionAlgorithmType.eCompressionAlgorithmNone.value;
	
	public BPConverterTypesC_Options() {
		super(new ImarisWriterTypeMapper());
		//this.mCompressionAlgorithmType = TCompressionAlgorithmType.eCompressionAlgorithmNone;
	}
	
	public void setCompression(TCompressionAlgorithmType type) {
		this.mCompressionAlgorithmType = (int) type.value;
	}
}

/*
typedef struct
{
  unsigned int mThumbnailSizeXY; // 256
  bool mFlipDimensionX; // false
  bool mFlipDimensionY; // false
  bool mFlipDimensionZ; // false
  bool mForceFileBlockSizeZ1; // false
  bool mEnableLogProgress; // false
  unsigned int mNumberOfThreads; // 8
  tCompressionAlgorithmType mCompressionAlgorithmType; // eCompressionAlgorithmGzipLevel2
} bpConverterTypesC_Options;
*/