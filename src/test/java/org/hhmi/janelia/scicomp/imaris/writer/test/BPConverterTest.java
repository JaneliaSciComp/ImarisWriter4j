package org.hhmi.janelia.scicomp.imaris.writer.test;

import org.hhmi.janelia.scicomp.imaris.writer.BPCallbackData;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverter;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Color;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_ColorInfo;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_ColorInfos;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_DataType;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Dimension;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_DimensionSequence5D;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_ImageExtent;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Index5D;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Options;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Parameter;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_ParameterSection;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Parameters;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Size5D;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_TimeInfo;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_TimeInfos;
import org.hhmi.janelia.scicomp.imaris.writer.TCompressionAlgorithmType;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverter.CLibrary;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

class BPConverterTest {
	
	class ProgressCallback implements CLibrary.BPConverterTypesC_ProgressCallback {
		public void invoke(float aProgress, long aTotalBytesWritten, BPCallbackData vCallbackData)
		{
			//BPCallbackData vCallbackData = aUserData;

			int vProgress = (int)(aProgress * 100);
			if (vProgress - vCallbackData.mProgress < 5 && vProgress != 100) {
				return;
			}

			int vImageIndex = vCallbackData.mImageIndex;
			if (aTotalBytesWritten < 10 * 1024 * 1024) {
				System.out.println("Progress image " + vImageIndex + ":" + vProgress + "% [" + (aTotalBytesWritten / 1024) + " KB]");
				//printf("Progress image %u: %d%% [%llu KB]\n", vImageIndex, vProgress, aTotalBytesWritten / 1024);
			}
			else {
				System.out.println("Progress image " + vImageIndex + ":" + vProgress + "% [" + (aTotalBytesWritten / (1024 * 1024)) + " MB]");
				//printf("Progress image %u: %d%% [%llu MB]\n", vImageIndex, vProgress, aTotalBytesWritten / (1024 * 1024));
			}
			vCallbackData.mProgress = vProgress;
		}
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void testConvert() {
		doConvert(0);
		doConvert(1);
	}

	void doConvert(int aTestIndex) {
		// Output file name
		String aOutputFile = "./out_" + aTestIndex + ".ims";
		
		// Get value from enum BPConverterTypesC_DataType
		int aDataType = BPConverterTypesC_DataType.bpConverterTypesC_UInt8Type.value;
		
		// Specify image size in pixels
		BPConverterTypesC_Size5D aImageSize = new BPConverterTypesC_Size5D();
		aImageSize.mValueX = 512;
		aImageSize.mValueY = 512;
		aImageSize.mValueZ = 32;
		aImageSize.mValueT = 4;
		aImageSize.mValueC = 2;
		
		// Specify image size in real units
		float vVoxelSizeXY = 1.4f;
		float vVoxelSizeZ = 5.4f;
		BPConverterTypesC_ImageExtent aImageExtent = new BPConverterTypesC_ImageExtent(
				0, 0, 0,
				aImageSize.mValueX * vVoxelSizeXY,
				aImageSize.mValueY * vVoxelSizeXY,
				aImageSize.mValueZ * vVoxelSizeZ
				);
		
		// Describe sampling
		BPConverterTypesC_Size5D aSample = new BPConverterTypesC_Size5D( 1, 1, 1, 1, 1 );

		// Describe dimension order
		BPConverterTypesC_DimensionSequence5D aDimensionSequence = new BPConverterTypesC_DimensionSequence5D(
				BPConverterTypesC_Dimension.bpConverterTypesC_DimensionX.value,
				BPConverterTypesC_Dimension.bpConverterTypesC_DimensionY.value,
				BPConverterTypesC_Dimension.bpConverterTypesC_DimensionZ.value,
				BPConverterTypesC_Dimension.bpConverterTypesC_DimensionC.value,
				BPConverterTypesC_Dimension.bpConverterTypesC_DimensionT.value
				);
		
		// Describe block size
		BPConverterTypesC_Size5D aBlockSize = new BPConverterTypesC_Size5D(256, 256, 8, 1, 1);

		// Options
		BPConverterTypesC_Options aOptions = new BPConverterTypesC_Options();
		aOptions.mThumbnailSizeXY = 256;
		aOptions.mFlipDimensionX = false;
		aOptions.mFlipDimensionY = false;
		aOptions.mFlipDimensionZ = false;
		aOptions.mForceFileBlockSizeZ1 = false;
		aOptions.mEnableLogProgress = true;
		aOptions.mNumberOfThreads = 8;
		aOptions.setCompression(TCompressionAlgorithmType.eCompressionAlgorithmLZ4);
		//aOptions.mCompressionAlgorithmType = (int) TCompressionAlgorithmType.eCompressionAlgorithmLZ4.value;
		//aOptions.mCompressionAlgorithmType = TCompressionAlgorithmType.eCompressionAlgorithmGzipLevel2;
		
		System.out.println(aOptions.mCompressionAlgorithmType);

		String aApplicationName = "TestC";
		String aApplicationVersion = "1.0.0";
		
		// Callback
		CLibrary.BPConverterTypesC_ProgressCallback aProgressCallback = new ProgressCallback();
		Native.setCallbackThreadInitializer(aProgressCallback, new CallbackThreadInitializer());

		BPCallbackData aCallbackUserData = new BPCallbackData();
		aCallbackUserData.mImageIndex = aTestIndex;
		aCallbackUserData.mProgress = -5;

		/*
		 * Original C function signature for bpImageConverterC_Create
		 * 
		 * bpImageConverterCPtr vConverter = bpImageConverterC_Create( aDataType,
		 * &aImageSize, &aSample, &aDimensionSequence, &aBlockSize, aOutputFile,
		 * &aOptions, aApplicationName, aApplicationVersion, aProgressCallback,
		 * &aCallbackUserData );
		 */

		System.out.println("create");
		//Returns a bpImageConverterCPtr
		Pointer vConverter = CLibrary.INSTANCE.bpImageConverterC_Create(
				aDataType, aImageSize, aSample,
				aDimensionSequence, aBlockSize,
				aOutputFile, aOptions,
				aApplicationName, aApplicationVersion,
				aProgressCallback, aCallbackUserData
				);
		BPConverter.checkErrors(vConverter);
		System.out.println("done with create");


		// unsigned long long
		//use int because this is an index
		int vBlockSize =
				aBlockSize.mValueX *
				aBlockSize.mValueY * aBlockSize.mValueZ *
				aBlockSize.mValueC * aBlockSize.mValueT;

		//unsigned char* vData = malloc(vBlockSize);
		byte[] vData = new byte[vBlockSize];
		for (int vIndex = 0; vIndex < vBlockSize; ++vIndex) {
			vData[vIndex] = (byte)(vIndex % 256);
		}

		int vNBlocksX = BPConverter.numBlocks(aImageSize.mValueX, aBlockSize.mValueX);
		int vNBlocksY = BPConverter.numBlocks(aImageSize.mValueY, aBlockSize.mValueY);
		int vNBlocksZ = BPConverter.numBlocks(aImageSize.mValueZ, aBlockSize.mValueZ);
		int vNBlocksC = BPConverter.numBlocks(aImageSize.mValueC, aBlockSize.mValueC);
		int vNBlocksT = BPConverter.numBlocks(aImageSize.mValueT, aBlockSize.mValueT);

		BPConverterTypesC_Index5D aBlockIndex = new BPConverterTypesC_Index5D();
		
		//int debugLoop = 0;

		for (int vC = 0; vC < vNBlocksC; ++vC) {
			aBlockIndex.mValueC = vC;
			for (int vT = 0; vT < vNBlocksT; ++vT) {
				aBlockIndex.mValueT = vT;
				for (int vZ = 0; vZ < vNBlocksZ; ++vZ) {
					aBlockIndex.mValueZ = vZ;
					for (int vY = 0; vY < vNBlocksY; ++vY) {
						aBlockIndex.mValueY = vY;
						for (int vX = 0; vX < vNBlocksX; ++vX) {
							aBlockIndex.mValueX = vX;
							//System.out.println("loop: " + debugLoop++);
							//void BPImageConverterC_CopyBlockUInt8(BPImageConverterCPtr aImageConverterC, ByteByReference aFileDataBlock, BPConverterTypesC_Index5D.ByReference aBlockIndex);
							CLibrary.INSTANCE.bpImageConverterC_CopyBlockUInt8(vConverter, vData, aBlockIndex);
							BPConverter.checkErrors(vConverter);
						}
					}
				}
			}
		}

		//free(vData);

		int vNumberOfOtherSections = 1; // Image
		int vNumberOfSections = vNumberOfOtherSections + aImageSize.mValueC;
		//bpConverterTypesC_ParameterSection* vParameterSections = malloc(vNumberOfSections * sizeof(bpConverterTypesC_ParameterSection));

		BPConverterTypesC_Parameters aParameters = new BPConverterTypesC_Parameters();
		aParameters.mValuesCount = vNumberOfSections;
		aParameters.mValues = new BPConverterTypesC_ParameterSection.ByReference();
		
		BPConverterTypesC_ParameterSection.ByReference[] vParameterSections = aParameters.mValues.toArray(aParameters.mValuesCount);

		BPConverterTypesC_Parameter.ByReference vUnitParameter = new BPConverterTypesC_Parameter.ByReference("Unit", "um");
		BPConverterTypesC_ParameterSection.ByReference vImageSection = vParameterSections[0];
		vImageSection = vParameterSections[0];
		vImageSection.mName = "Image";
		vImageSection.mValuesCount = 1;
		vImageSection.mValues = vUnitParameter;

		//char vChannelNamesBuffer[1024]; // will this be enough?
		//char* vChannelNameBuffer = vChannelNamesBuffer;
		//String vChannelNameBuffer = new String();
		//StringBuffer vChannelNameBuffer = new StringBuffer(1024);

		int vNumberOfParametersPerChannel = 3;
		//bpConverterTypesC_Parameter* vChannelParameters = malloc(aImageSize.mValueC * vNumberOfParametersPerChannel * sizeof(bpConverterTypesC_Parameter));
		//BPConverterTypesC_Parameter[] vChannelParameters = new BPConverterTypesC_Parameter[aImageSize.mValueC * vNumberOfParametersPerChannel];


		//int vThisChannelParameters_idx = 0;
		for (int vC = 0; vC < aImageSize.mValueC; ++vC) {
			/*BPConverterTypesC_Parameter vThisChannelParameters = vChannelParameters[vNumberOfParametersPerChannel * vC];*/
			//vThisChannelParameters_idx = vNumberOfParametersPerChannel * vC;
			//check this for memory
			//BPConverterTypesC_Parameter.ByReference[] vThisChannelParameters = new BPConverterTypesC_Parameter.ByReference[vNumberOfParametersPerChannel];
			
			BPConverterTypesC_ParameterSection.ByReference vChannelSection = vParameterSections[vNumberOfOtherSections + vC];
			vChannelSection.mValues = new BPConverterTypesC_Parameter.ByReference();
			
			BPConverterTypesC_Parameter.ByReference[] vThisChannelParameters = vChannelSection.mValues.toArray(vNumberOfParametersPerChannel);
			//vThisChannelParameters[0] = new BPConverterTypesC_Parameter.ByReference();
			vThisChannelParameters[0].mName = "Name";
			vThisChannelParameters[0].mValue = vC == 0 ? "First channel" : vC == 1 ? "Second channel" : vC == 2 ? "Third channel" : "Other channel";
			//vThisChannelParameters[1] = new BPConverterTypesC_Parameter.ByReference();
			vThisChannelParameters[1].mName = "LSMEmissionWavelength";
			vThisChannelParameters[1].mValue = "700";
			//vThisChannelParameters[2] = new BPConverterTypesC_Parameter.ByReference();
			vThisChannelParameters[2].mName = "OtherChannelParameter";
			vThisChannelParameters[2].mValue = "OtherChannelValue";
			//BPConverterTypesC_ParameterSection vChannelSection = vParameterSections[vNumberOfOtherSections + vC];
			//vChannelNameBuffer.append("Channel ");

			/*int vChannelNameLength = sprintf(vChannelNameBuffer, "Channel %i", vC);
	    vChannelSection.mName = "Channel " + vC;
	    vChannelNameBuffer += vChannelNameLength + 1;
			 */
			//BPConverterTypesC_ParameterSection.ByReference vChannelSection = new BPConverterTypesC_ParameterSection.ByReference();
			//BPConverterTypesC_ParameterSection.ByReference vChannelSection = new BPConverterTypesC_ParameterSection.ByReference();
			vChannelSection.mName = "Channel " + vC;
			//vChannelSection.mValues = vThisChannelParameters;
			vChannelSection.mValuesCount = vNumberOfParametersPerChannel;
		}

		BPConverterTypesC_TimeInfos aTimeInfoPerTimePoint = new BPConverterTypesC_TimeInfos();
		aTimeInfoPerTimePoint.mValuesCount = aImageSize.mValueT;
		aTimeInfoPerTimePoint.mValues = new BPConverterTypesC_TimeInfo.ByReference();

		BPConverterTypesC_TimeInfo.ByReference[] vTimeInfos = aTimeInfoPerTimePoint.mValues.toArray(aImageSize.mValueT);
		for (int vT = 0; vT < aImageSize.mValueT; ++vT) {
			//vTimeInfos[vT] = new BPConverterTypesC_TimeInfo.ByReference();
			vTimeInfos[vT].mJulianDay = 2458885; // 5 feb 2020
			long vSeconds = vT + 4 + 60 * (27 + 60 * 15); // 3:27.04 PM + 1 sec per time point
			vTimeInfos[vT].mNanosecondsOfDay = vSeconds * 1000000000;
		}

		
		BPConverterTypesC_ColorInfos aColorInfoPerChannel = new BPConverterTypesC_ColorInfos();
		aColorInfoPerChannel.mValuesCount = aImageSize.mValueC;
		aColorInfoPerChannel.mValues = new BPConverterTypesC_ColorInfo.ByReference();

		BPConverterTypesC_ColorInfo.ByReference[] vColorInfos = aColorInfoPerChannel.mValues.toArray(aImageSize.mValueC);
		for (int vC = 0; vC < aImageSize.mValueC; ++vC) {
			BPConverterTypesC_ColorInfo vColor = vColorInfos[vC];
			vColor.mIsBaseColorMode = true;
			vColor.mBaseColor = new BPConverterTypesC_Color();
			vColor.mBaseColor.mRed =   (vC % 3) == 0 ? 1.0f : 0.0f;
			vColor.mBaseColor.mGreen = (vC % 3) == 1 ? 1.0f : 0.0f;
			vColor.mBaseColor.mBlue =  (vC % 3) == 2 ? 1.0f : 0.0f;
			vColor.mBaseColor.mAlpha = 1;
			vColor.mColorTableSize = 0;
			vColor.mOpacity = 0;
			vColor.mRangeMin = 0;
			vColor.mRangeMax = 255;
			vColor.mGammaCorrection = 1;
			/*
	    vColor->mIsBaseColorMode = true;
	    vColor->mBaseColor.mRed =   (vC % 3) == 0 ? 1.0f : 0.0f;
	    vColor->mBaseColor.mGreen = (vC % 3) == 1 ? 1.0f : 0.0f;
	    vColor->mBaseColor.mBlue =  (vC % 3) == 2 ? 1.0f : 0.0f;
	    vColor->mBaseColor.mAlpha = 1;
	    vColor->mColorTableSize = 0;
	    vColor->mOpacity = 0;
	    vColor->mRangeMin = 0;
	    vColor->mRangeMax = 255;
	    vColor->mGammaCorrection = 1;
			 */
		}


		boolean aAutoAdjustColorRange = true;

		/*	  bpImageConverterC_Finish(vConverter,
			    &aImageExtent, &aParameters, &aTimeInfoPerTimePoint,
			    &aColorInfoPerChannel, aAutoAdjustColorRange);*/
		
		System.out.println("Finishing");
		CLibrary.INSTANCE.bpImageConverterC_Finish(vConverter,
				aImageExtent, aParameters, aTimeInfoPerTimePoint,
				aColorInfoPerChannel, aAutoAdjustColorRange);
		BPConverter.checkErrors(vConverter);
		System.out.println("Done Finishing");

		//free(vTimeInfos);
		//free(vColorInfos);

		//free(vParameterSections);
		//free(vChannelParameters);

		CLibrary.INSTANCE.bpImageConverterC_Destroy(vConverter);
	}

}
