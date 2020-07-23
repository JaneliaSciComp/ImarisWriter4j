package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mThumbnailSizeXY","mFlipDimensionX","mFlipDimensionY","mFlipDimensionZ","mForceFileBlockSizeZ1","mEnableLogProgress","mNumberOfThreads","mCompressionAlgorithmType"})
public class BPConverterTypesC_Options extends Structure {
	public static class ByReference extends BPConverterTypesC_Options implements Structure.ByReference {}
	public static class ByValue extends BPConverterTypesC_Options implements Structure.ByValue {}

	public int mThumbnailSizeXY; // 256, unsigned int
	public boolean mFlipDimensionX; // false, bool
	public boolean mFlipDimensionY; // false, bool
	public boolean mFlipDimensionZ; // false, bool
	public boolean mForceFileBlockSizeZ1; // false, bool
	public boolean mEnableLogProgress; // false, bool
	public int mNumberOfThreads; // 8, unsigned int
	//public TCompressionAlgorithmType mCompressionAlgorithmType; // eCompressionAlgorithmGzipLevel2
	public int mCompressionAlgorithmType;
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