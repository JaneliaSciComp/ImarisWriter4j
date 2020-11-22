package org.hhmi.janelia.scicomp.imaris.writer.direct;

import java.nio.ShortBuffer;

import org.hhmi.janelia.scicomp.imaris.writer.BPCallbackData;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverter;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_ColorInfos;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_DimensionSequence5D;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_ImageExtent;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Index5D;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Options;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Parameters;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Size5D;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_TimeInfos;
import org.hhmi.janelia.scicomp.imaris.writer.ImarisWriterError;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverter.CLibrary;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverter.CLibrary.BPConverterTypesC_ProgressCallback;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class ImarisWriter {
	/**
	 * bpImageConverterC_Create initializes an Imaris file
	 * 
	 * @param aDataType See BPConverterTypesC_DataType.value
	 * @param aImageSize Describe the min and max coordinates
	 * @param aSample Describe the sample size
	 * @param aDimensionSequence Describe of the order of XYZCT
	 * @param aFileBlockSize The size of a block
	 * @param aOutputFile The output file name (output.ims)
	 * @param aOptions Options for thumbnails, flipping dimensions, and compression
	 * @param aApplicationName Name of your application
	 * @param aApplicationVersion Version of your application
	 * @param aProgressCallback Callback function to monitor and report progress
	 * @param aCallbackUserData Callback data structure, can persist between callback calls
	 * @return A pointer used by subsequent functions
	 */
	public native static Pointer bpImageConverterC_Create(
			int aDataType, BPConverterTypesC_Size5D aImageSize, BPConverterTypesC_Size5D aSample,
			BPConverterTypesC_DimensionSequence5D aDimensionSequence, BPConverterTypesC_Size5D aFileBlockSize,
			String aOutputFile, BPConverterTypesC_Options aOptions,
			String aApplicationName, String aApplicationVersion,
			BPConverterTypesC_ProgressCallback aProgressCallback, BPCallbackData aCallbackUserData);
	
	/**
	 * bpImageConverterC_Destroy removes the structure created by bpImageConverterC_Create from memory
	 * 
	 * @param aImageConverterC Pointer returned by bpImageConverterC_Create
	 */
	public native static void bpImageConverterC_Destroy(Pointer aImageConverterC);

	/**
	 * bpImageConverterC_GetLastException retrieves information about an exception that occurred
	 * 
	 * @param aImageConverterC
	 * @return A String describing the last error that occurred
	 * 
	 * @see BPConverter#checkErrors(Pointer)
	 */
	public native static String bpImageConverterC_GetLastException(Pointer aImageConverterC);

	/**
	 * bpImageConverterC_NeedCopyBlock
	 * 
	 * @param aImageConverterC
	 * @param aBlockIndex
	 * @return
	 */
	public native static boolean bpImageConverterC_NeedCopyBlock(Pointer aImageConverterC, BPConverterTypesC_Index5D aBlockIndex);

	//typedef unsigned char bpConverterTypesC_UInt8; 
	//typedef unsigned short int bpConverterTypesC_UInt16;
	//typedef unsigned int bpConverterTypesC_UInt32;
	//typedef unsigned long long bpConverterTypesC_UInt64;
	//typedef float bpConverterTypesC_Float;

	/*
	void BPImageConverterC_CopyBlockUInt8(BPImageConverterCPtr aImageConverterC, ByteByReference aFileDataBlock, BPConverterTypesC_Index5D.ByReference aBlockIndex);
	void BPImageConverterC_CopyBlockUInt16(BPImageConverterCPtr aImageConverterC, ShortByReference aFileDataBlock, BPConverterTypesC_Index5D.ByReference aBlockIndex);
	void BPImageConverterC_CopyBlockUInt32(BPImageConverterCPtr aImageConverterC, IntByReference aFileDataBlock, BPConverterTypesC_Index5D.ByReference aBlockIndex);
	void BPImageConverterC_CopyBlockFloat(BPImageConverterCPtr aImageConverterC, FloatByReference aFileDataBlock, BPConverterTypesC_Index5D.ByReference aBlockIndex);
	 */
	//void BPImageConverterC_CopyBlockUInt8(BPImageConverterCPtr aImageConverterC, byte[] aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
	
	
	/**
	 * bpImageConverterC_CopyBlockUInt8 copies an unsigned byte array
	 * 
	 * @param aImageConverterC Pointer created by bpImageConverterC_Create
	 * @param aFileDataBlock Data array
	 * @param aBlockIndex Index for the block
	 */
	public native static void bpImageConverterC_CopyBlockUInt8(Pointer aImageConverterC, byte[] aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
	/**
	 * bpImageConverterC_CopyBlockUInt16 copies an unsigned short array
	 * 
	 * @param aImageConverterC Pointer created by bpImageConverterC_Create
	 * @param aFileDataBlock Data array
	 * @param aBlockIndex Index for the block
	 */
	public native static void bpImageConverterC_CopyBlockUInt16(Pointer aImageConverterC, short[] aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
	public native static void bpImageConverterC_CopyBlockUInt16(Pointer aImageConverterC, ShortBuffer aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
	public native static void bpImageConverterC_CopyBlockUInt16(Pointer aImageConverterC, Pointer aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
	/**
	 * bpImageConverterC_CopyBlockUInt32 copies an unsigned int array
	 * 
	 * @param aImageConverterC Pointer created by bpImageConverterC_Create
	 * @param aFileDataBlock Data array
	 * @param aBlockIndex Index for the block
	 */
	public native static void bpImageConverterC_CopyBlockUInt32(Pointer aImageConverterC, int[] aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
	/**
	 * bpImageConverterC_CopyBlockFloat copies a float array
	 * 
	 * @param aImageConverterC Pointer created by bpImageConverterC_Create
	 * @param aFileDataBlock Data array
	 * @param aBlockIndex Index for the block
	 */
	public native static void bpImageConverterC_CopyBlockFloat(Pointer aImageConverterC, float[] aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);

	/**
	 * bpImageConverterC_Finish
	 * 
	 * @param aImageConverterC Pointer created by bpImageConverterC_Create
	 * @param aImageExtent Physical extent of the image
	 * @param aParameters Container for an array of BPConverterTypesC_Parameter
	 * @param aTimeInfoPerTimePoint Temporal data data
	 * @param aColorInfoPerChannel Container for an array of BPConverterTypesC_TimeInfo
	 * @param aAutoAdjustColorRange Boolean to determine if color range is adjusted
	 * 
	 * @see #bpImageConverterC_Create
	 */
	public native static void bpImageConverterC_Finish(
			Pointer aImageConverterC,
			BPConverterTypesC_ImageExtent aImageExtent,
			BPConverterTypesC_Parameters aParameters,
			BPConverterTypesC_TimeInfos aTimeInfoPerTimePoint,
			BPConverterTypesC_ColorInfos aColorInfoPerChannel,
			boolean aAutoAdjustColorRange);
	
	/**
	 * numBlocks is a static utility method to calculate the number of blocks
	 * 
	 * @param aSize
	 * @param aBlockSize
	 * @return
	 */
	public static int numBlocks(int aSize, int aBlockSize)
	{
		return (aSize + aBlockSize - 1) / aBlockSize;
	}

	/**
	 * checkErrors is a static utility method to check for errors
	 * 
	 * @param aConverter Pointer created by bpImageConverterC_Create
	 */
	public static void checkErrors(Pointer aConverter)
	{
		String vException = bpImageConverterC_GetLastException(aConverter);
		if (vException != null && !vException.isEmpty()) {
			throw new ImarisWriterError(vException);
		}
	}
	
	static {
		System.loadLibrary("zlib");
		System.loadLibrary("hdf5");
		Native.register("bpImarisWriter96");
	}

}
