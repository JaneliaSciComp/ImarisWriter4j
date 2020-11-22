package org.hhmi.janelia.scicomp.imaris.writer;

import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.Map;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * BPConverter is the main interface class for ImarisWriter Java Native Access bindings.
 * It provides a low level interface to the ImarisWriter C interface.
 * 
 * This class depends on the bpImarisWriter96.{dll,so,dylib} shared library.
 * which in turn depends on zlib and hdf5. It may also depend on lz4 but that is
 * currently compiled in statically. 
 * 
 * @author kittisopikulm (Mark Kittisopikul)
 *
 */
public class BPConverter {
	static {
		System.loadLibrary("zlib");
		System.loadLibrary("hdf5");
		//Debugging library names
		//System.loadLibrary("zlibd");
		//System.loadLibrary("hdf5_D");
	}
	
	/**
	 * 
	 * CLibrary is JNA Library containing the main methods
	 * @author kittisopikulm (Mark Kittisopikul)
	 *
	 */
	public interface CLibrary extends Library {
		Map<String, Object> OPTIONS = Collections.singletonMap(Library.OPTION_TYPE_MAPPER, new ImarisWriterTypeMapper());
		CLibrary INSTANCE = (CLibrary)
				Native.load("bpImarisWriter96",CLibrary.class,OPTIONS);

		/**
		 * BPConverterTypesC_ProgressCallback provides a callback template to monitor progress.
		 * See BPConverterTest for an example. 
		 * 
		 * It is the analog of this C typedef:
		 * typedef void(*bpConverterTypesC_ProgressCallback)(bpConverterTypesC_Float aProgress, bpConverterTypesC_UInt64 aTotalBytesWritten, void* aUserData);
		 * 
		 * @author kittisopikulm (Mark Kittisopikul)
		 * 
		 * @see com.sun.jna.Callback
		 * @see com.sun.jna.CallbackThreadInitializer
		 * @see BPConverterTest
		 *
		 */
		public interface BPConverterTypesC_ProgressCallback extends Callback {
			void invoke(float aProgress, long aTotalBytesWritten, BPCallbackData aUserData);
		}

		
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
		Pointer bpImageConverterC_Create(
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
		void bpImageConverterC_Destroy(Pointer aImageConverterC);

		/**
		 * bpImageConverterC_GetLastException retrieves information about an exception that occurred
		 * 
		 * @param aImageConverterC
		 * @return A String describing the last error that occurred
		 * 
		 * @see BPConverter#checkErrors(Pointer)
		 */
		String bpImageConverterC_GetLastException(Pointer aImageConverterC);

		/**
		 * bpImageConverterC_NeedCopyBlock
		 * 
		 * @param aImageConverterC
		 * @param aBlockIndex
		 * @return
		 */
		boolean bpImageConverterC_NeedCopyBlock(Pointer aImageConverterC, BPConverterTypesC_Index5D aBlockIndex);

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
		void bpImageConverterC_CopyBlockUInt8(Pointer aImageConverterC, byte[] aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
		/**
		 * bpImageConverterC_CopyBlockUInt16 copies an unsigned short array
		 * 
		 * @param aImageConverterC Pointer created by bpImageConverterC_Create
		 * @param aFileDataBlock Data array
		 * @param aBlockIndex Index for the block
		 */
		void bpImageConverterC_CopyBlockUInt16(Pointer aImageConverterC, short[] aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
		void bpImageConverterC_CopyBlockUInt16(Pointer aImageConverterC, ShortBuffer aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
		/**
		 * bpImageConverterC_CopyBlockUInt32 copies an unsigned int array
		 * 
		 * @param aImageConverterC Pointer created by bpImageConverterC_Create
		 * @param aFileDataBlock Data array
		 * @param aBlockIndex Index for the block
		 */
		void bpImageConverterC_CopyBlockUInt32(Pointer aImageConverterC, int[] aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);
		/**
		 * bpImageConverterC_CopyBlockFloat copies a float array
		 * 
		 * @param aImageConverterC Pointer created by bpImageConverterC_Create
		 * @param aFileDataBlock Data array
		 * @param aBlockIndex Index for the block
		 */
		void bpImageConverterC_CopyBlockFloat(Pointer aImageConverterC, float[] aFileDataBlock, BPConverterTypesC_Index5D aBlockIndex);

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
		void bpImageConverterC_Finish(
				Pointer aImageConverterC,
				BPConverterTypesC_ImageExtent aImageExtent,
				BPConverterTypesC_Parameters aParameters,
				BPConverterTypesC_TimeInfos aTimeInfoPerTimePoint,
				BPConverterTypesC_ColorInfos aColorInfoPerChannel,
				boolean aAutoAdjustColorRange);


	}

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
		String vException = CLibrary.INSTANCE.bpImageConverterC_GetLastException(aConverter);
		if (vException != null && !vException.isEmpty()) {
			throw new ImarisWriterError(vException);
		}
	}
	
}
