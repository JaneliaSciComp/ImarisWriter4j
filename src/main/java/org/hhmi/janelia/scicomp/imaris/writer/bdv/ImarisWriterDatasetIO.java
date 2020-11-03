package org.hhmi.janelia.scicomp.imaris.writer.bdv;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hhmi.janelia.scicomp.imaris.writer.BPCallbackData;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverter;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Color;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_ColorInfo;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_ColorInfos;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverter.CLibrary;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverter.CLibrary.BPConverterTypesC_ProgressCallback;
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

import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import bdv.export.ExportScalePyramid.Block;
import bdv.export.ExportScalePyramid.DatasetIO;
import bdv.export.ProgressWriter;
import bdv.export.ProgressWriterConsole;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class ImarisWriterDatasetIO implements DatasetIO< Pointer,UnsignedShortType > {
	
	class ProgressCallback implements CLibrary.BPConverterTypesC_ProgressCallback {
		public void invoke(float aProgress, long aTotalBytesWritten, BPCallbackData vCallbackData)
		{
			
			//System.out.println("Bytes Written: " + aTotalBytesWritten);
			//System.out.flush();
			
			//BPCallbackData vCallbackData = aUserData;

			int vProgress = (int)(aProgress * 100);
			if (vProgress - vCallbackData.mProgress < 1 && vProgress != 100) {
				return;
			}
			
			progressWriter.setProgress(aProgress);

			int vImageIndex = vCallbackData.mImageIndex;
			if (aTotalBytesWritten < 10 * 1024 * 1024) {
				//System.out.println("Progress image " + vImageIndex + ":" + vProgress + "% [" + (aTotalBytesWritten / 1024) + " KB]");
				System.out.println("[" + (aTotalBytesWritten / 1024) + " KB]");
				//printf("Progress image %u: %d%% [%llu KB]\n", vImageIndex, vProgress, aTotalBytesWritten / 1024);
			}
			else {
				//System.out.println("Progress image " + vImageIndex + ":" + vProgress + "% [" + (aTotalBytesWritten / (1024 * 1024)) + " MB]");
				System.out.println("[" + (aTotalBytesWritten / (1024 * 1024)) + " MB]");
				//printf("Progress image %u: %d%% [%llu MB]\n", vImageIndex, vProgress, aTotalBytesWritten / (1024 * 1024));
			}
			vCallbackData.mProgress = vProgress;
		}
	}
	
	final BPConverter.CLibrary converter = BPConverter.CLibrary.INSTANCE;
	final String aOutputFile;
	final String aApplicationName = "Janelia ImarisWriter4j";
	final String aApplicationVersion = "0.1.0";
	final boolean aAutoAdjustColorRange = false;
	float vVoxelSizeXY = 1.0f;
	float vVoxelSizeZ = 1.0f;
	final TCompressionAlgorithmType compression;
	BPConverterTypesC_ImageExtent aImageExtent;
	BPConverterTypesC_Size5D aImageSize;
	final private ProgressWriter progressWriter;
	private boolean datasetCreated = false;
	private final Map< String, Map< String,String > > parameterSections;
	private List< LocalDateTime > times;
	Pointer pConverter;
	final BPConverterTypesC_ProgressCallback aProgressCallback;
	BPCallbackData aCallbackUserData;
	private long dimC;
	private long dimT;
	private int currentTimepointId;
	
	public ImarisWriterDatasetIO(String aOutputFile, ProgressWriter progressWriter, TCompressionAlgorithmType compression, long dimC, long dimT) {
		this.aOutputFile = aOutputFile;
		this.progressWriter = progressWriter;
		this.parameterSections = new HashMap<>();
		this.compression = compression;
		
		this.dimC = dimC;
		this.dimT = dimT;
		
		this.currentTimepointId = 1;
		
		aProgressCallback = new ProgressCallback();
		Native.setCallbackThreadInitializer(aProgressCallback, new CallbackThreadInitializer());

		aCallbackUserData = new BPCallbackData();
		aCallbackUserData.mImageIndex = 0;
		aCallbackUserData.mProgress = -5;
	}
	
	public ImarisWriterDatasetIO(String aOutputFile) {
		this(aOutputFile, new ProgressWriterConsole(),TCompressionAlgorithmType.eCompressionAlgorithmNone,1,1);
	}

	@Override
	public Pointer createDataset(int level, long[] dimensions, int[] blockSize) throws IOException {
		
		if( !this.datasetCreated ) {
			int aDataType = BPConverterTypesC_DataType.bpConverterTypesC_UInt16Type.value;
			
			aImageSize = new BPConverterTypesC_Size5D(dimensions[0], dimensions[1], dimensions[2], dimC, dimT);
			BPConverterTypesC_Size5D aSample = new BPConverterTypesC_Size5D( 1, 1, 1, 1, 1 );
			BPConverterTypesC_DimensionSequence5D aDimensionSequence = new BPConverterTypesC_DimensionSequence5D(
					BPConverterTypesC_Dimension.bpConverterTypesC_DimensionX.value,
					BPConverterTypesC_Dimension.bpConverterTypesC_DimensionY.value,
					BPConverterTypesC_Dimension.bpConverterTypesC_DimensionZ.value,
					BPConverterTypesC_Dimension.bpConverterTypesC_DimensionC.value,
					BPConverterTypesC_Dimension.bpConverterTypesC_DimensionT.value
					);
			
			System.out.println(Arrays.toString(blockSize));
			
			BPConverterTypesC_Size5D aFileBlockSize = new BPConverterTypesC_Size5D(blockSize);
			
			BPConverterTypesC_Options aOptions = new BPConverterTypesC_Options();
			aOptions.mThumbnailSizeXY = 256;
			aOptions.mFlipDimensionX = false;
			aOptions.mFlipDimensionY = false;
			aOptions.mFlipDimensionZ = false;
			aOptions.mForceFileBlockSizeZ1 = false;
			aOptions.mEnableLogProgress = true;
			aOptions.mNumberOfThreads = 8;
			aOptions.mCompressionAlgorithmType = this.compression.value;
			
			aImageExtent = new BPConverterTypesC_ImageExtent(
					0, 0, 0,
					aImageSize.mValueX * vVoxelSizeXY,
					aImageSize.mValueY * vVoxelSizeXY,
					aImageSize.mValueZ * vVoxelSizeZ
					);
			
	
			

			
			pConverter = converter.bpImageConverterC_Create(
					aDataType,
					aImageSize,
					aSample,
					aDimensionSequence,
					aFileBlockSize,
					aOutputFile,
					aOptions,
					aApplicationName,
					aApplicationVersion,
					aProgressCallback, aCallbackUserData);
			
			BPConverter.checkErrors(pConverter);
			
			this.datasetCreated = true;
		}
		
		
		return pConverter;
	}

	@Override
	public void writeBlock(Pointer dataset, Block<UnsignedShortType> dataBlock) throws IOException {
		short[] aFileDataBlock = (short[]) dataBlock.getData().getStorageArray();
		//System.out.println("Block length:" + aFileDataBlock.length);
		BPConverterTypesC_Index5D aBlockIndex = new BPConverterTypesC_Index5D(dataBlock.getGridPosition());
		aBlockIndex.mValueT = this.currentTimepointId;
		converter.bpImageConverterC_CopyBlockUInt16(dataset, aFileDataBlock, aBlockIndex );
		BPConverter.checkErrors(dataset);
	}
	
	/**
	 * Add a generic parameter section
	 * 
	 * @param sectionName
	 * @param map
	 */
	public void addParameterSection(String sectionName, Map< String, String > map) {
		parameterSections.put(sectionName, map);
	}
	
	/**
	 * Add a parameter for the image
	 * 
	 * @param key
	 * @param value
	 */
	public void addImageParameter(String key, String value ) {
		Map< String, String > imageSection;
		if(!parameterSections.containsKey("Image")) {
			imageSection = new HashMap<>();
			parameterSections.put("Image", imageSection);
		} else {
			imageSection = parameterSections.get("Image");
		}
		imageSection.put(key, value);
	}
	
	/**
	 * Add a parameter for a channel
	 * 
	 * @param channel
	 * @param key
	 * @param value
	 */
	public void addChannelParameter(int channel, String key, String value) {
		final String sectionName = "Channel " + channel;
		Map< String, String > channelSection;
		
		if(!parameterSections.containsKey(sectionName)) {
			channelSection = new HashMap<>();
			parameterSections.put("Image", channelSection);
		} else {
			channelSection = parameterSections.get(sectionName);
		}
		
		channelSection.put(key, value);
	}
	
	public void setCurrentTimepointId(int timepointid) {
		this.currentTimepointId = timepointid;
	}
	
	/**
	 * Construct parameter information
	 * 
	 * Build parameter information from Map 
	 * 
	 * @return
	 */
	private BPConverterTypesC_Parameters buildParameters() {
		BPConverterTypesC_Parameters aParameters = new BPConverterTypesC_Parameters();
		aParameters.mValuesCount = parameterSections.size();
		aParameters.mValues = new BPConverterTypesC_ParameterSection.ByReference();
		
		if( aParameters.mValuesCount == 0 )
			return aParameters;
		
		BPConverterTypesC_ParameterSection.ByReference[] vParameterSections = aParameters.mValues.toArray(aParameters.mValuesCount);
		
		//TODO: Move this logic into core ImarisWriter4j?
		int s = 0;
		int p = 0;
		Map< String, String > parameters;
		BPConverterTypesC_Parameter.ByReference[] vParameters;
		for(Map.Entry< String, Map< String,String > > entry: parameterSections.entrySet()) {
			vParameterSections[s].mName = entry.getKey();
			parameters = entry.getValue();
			vParameterSections[s].mValuesCount = parameters.size();
			vParameterSections[s].mValues = new BPConverterTypesC_Parameter.ByReference();
			vParameters = vParameterSections[s].mValues.toArray( vParameterSections[s].mValuesCount );
			p = 0;
			for(Map.Entry< String, String > parameter: parameters.entrySet()) {
				vParameters[p].mName = parameter.getKey();
				vParameters[p].mValue = parameter.getValue();
				++p;
			}
			++s;
		}
		return aParameters;
	}
	
	/**
	 * Construct information on time
	 * 
	 * @return
	 */
	private BPConverterTypesC_TimeInfos buildTimeInfos() {
		BPConverterTypesC_TimeInfos aTimeInfoPerTimePoint = new BPConverterTypesC_TimeInfos();
		aTimeInfoPerTimePoint.mValuesCount = aImageSize.mValueT;
		aTimeInfoPerTimePoint.mValues = new BPConverterTypesC_TimeInfo.ByReference();
		
		if(aImageSize.mValueT == 0)
			return aTimeInfoPerTimePoint;

		BPConverterTypesC_TimeInfo.ByReference[] vTimeInfos = aTimeInfoPerTimePoint.mValues.toArray(aImageSize.mValueT);
		for (int vT = 0; vT < aImageSize.mValueT; ++vT) {
			//vTimeInfos[vT] = new BPConverterTypesC_TimeInfo.ByReference();
			vTimeInfos[vT].mJulianDay = 2458885; // 5 feb 2020
			long vSeconds = vT + 4 + 60 * (27 + 60 * 15); // 3:27.04 PM + 1 sec per time point
			vTimeInfos[vT].mNanosecondsOfDay = vSeconds * 1000000000;
		}
		return aTimeInfoPerTimePoint;
	}
	
	/**
	 * Construct information on color per channels
	 * 
	 * @return
	 */
	private BPConverterTypesC_ColorInfos buildColorInfos() {
		BPConverterTypesC_ColorInfos aColorInfoPerChannel = new BPConverterTypesC_ColorInfos();
		aColorInfoPerChannel.mValuesCount = aImageSize.mValueC;
		aColorInfoPerChannel.mValues = new BPConverterTypesC_ColorInfo.ByReference();
		
		if(aImageSize.mValueC == 0)
			return aColorInfoPerChannel;

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
		}
		return aColorInfoPerChannel;
	}

	@Override
	public void flush(Pointer dataset) throws IOException {

		final BPConverterTypesC_Parameters aParameters = buildParameters();
		final BPConverterTypesC_TimeInfos aTimeInfoPerTimePoint = buildTimeInfos();
		final BPConverterTypesC_ColorInfos aColorInfoPerChannel = buildColorInfos();

		converter.bpImageConverterC_Finish( dataset, aImageExtent, aParameters, aTimeInfoPerTimePoint, aColorInfoPerChannel, aAutoAdjustColorRange);
		BPConverter.checkErrors(dataset);
		System.out.println("Done Finishing");
		converter.bpImageConverterC_Destroy(dataset);
	}
	
}
