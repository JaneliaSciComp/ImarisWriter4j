package org.hhmi.janelia.scicomp.imaris.writer.bdv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.hhmi.janelia.scicomp.imaris.writer.BPConverterTypesC_Options;
import org.hhmi.janelia.scicomp.imaris.writer.TCompressionAlgorithmType;

import com.sun.jna.Pointer;

import bdv.export.ExportMipmapInfo;
import bdv.export.ExportScalePyramid;
import bdv.export.ProgressWriter;
import bdv.export.ProgressWriterConsole;
import bdv.export.SubTaskProgressWriter;
import bdv.export.ExportScalePyramid.AfterEachPlane;
import bdv.export.ExportScalePyramid.Block;
import bdv.export.ExportScalePyramid.DatasetIO;
import bdv.img.hdf5.Partition;
import mpicbg.spim.data.generic.sequence.AbstractSequenceDescription;
import mpicbg.spim.data.generic.sequence.BasicImgLoader;
import mpicbg.spim.data.generic.sequence.BasicSetupImgLoader;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import mpicbg.spim.data.sequence.ImgLoader;
import mpicbg.spim.data.sequence.SequenceDescription;
import mpicbg.spim.data.sequence.SetupImgLoader;
import mpicbg.spim.data.sequence.TimePoint;
import mpicbg.spim.data.sequence.ViewId;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

public class WriteSequenceToImarisWriter {
	/**
	 * Create a ims file containing image data from all views and all
	 * timepoints in a chunked, mipmaped representation.
	 *
	 * @param seq
	 *            description of the sequence to be stored as hdf5. (The
	 *            {@link AbstractSequenceDescription} contains the number of
	 *            setups and timepoints as well as an {@link BasicImgLoader}
	 *            that provides the image data, Registration information is not
	 *            needed here, that will go into the accompanying xml).
	 * @param perSetupMipmapInfo
	 *            this maps from setup {@link BasicViewSetup#getId() id} to
	 *            {@link ExportMipmapInfo} for that setup. The
	 *            {@link ExportMipmapInfo} contains for each mipmap level, the
	 *            subsampling factors and subdivision block sizes.
	 * @param deflate
	 *            whether to compress the data with the HDF5 DEFLATE filter.
	 * @param hdf5File
	 *            hdf5 file to which the image data is written.
	 * @param loopbackHeuristic
	 *            heuristic to decide whether to create each resolution level by
	 *            reading pixels from the original image or by reading back a
	 *            finer resolution level already written to the hdf5. may be
	 *            null (in this case always use the original image).
	 * @param afterEachPlane
	 *            this is called after each "plane of chunks" is written, giving
	 *            the opportunity to clear caches, etc.
	 * @param numCellCreatorThreads
	 *            The number of threads that will be instantiated to generate
	 *            cell data. Must be at least 1. (In addition the cell creator
	 *            threads there is one writer thread that saves the generated
	 *            data to HDF5.)
	 * @param progressWriter
	 *            completion ratio and status output will be directed here.
	 */
	public static void writeImarisFile(
			final AbstractSequenceDescription< ?, ?, ? > seq,
			final Map< Integer, ExportMipmapInfo > perSetupMipmapInfo,
			final BPConverterTypesC_Options bpOptions,
			final File imsFile,
			final AfterEachPlane afterEachPlane,
			final int numCellCreatorThreads,
			final ProgressWriter progressWriter )
	{
		final HashMap< Integer, Integer > timepointIdSequenceToPartition = new HashMap<>();
		for ( final TimePoint timepoint : seq.getTimePoints().getTimePointsOrdered() )
			timepointIdSequenceToPartition.put( timepoint.getId(), timepoint.getId() );

		final HashMap< Integer, Integer > setupIdSequenceToPartition = new HashMap<>();
		for ( final BasicViewSetup setup : seq.getViewSetupsOrdered() )
			setupIdSequenceToPartition.put( setup.getId(), setup.getId() );

		final Partition partition = new Partition( imsFile.getPath(), timepointIdSequenceToPartition, setupIdSequenceToPartition );
		writeImarisPartitionFile( seq, perSetupMipmapInfo, bpOptions, partition, afterEachPlane, numCellCreatorThreads, progressWriter );
	}
	
	public static void writeImarisFile(
			final AbstractSequenceDescription< ?, ?, ? > seq,
			final Map< Integer, ExportMipmapInfo > perSetupMipmapInfo,
			final TCompressionAlgorithmType compression,
			final File imsFile,
			final AfterEachPlane afterEachPlane,
			final int numCellCreatorThreads,
			final ProgressWriter progressWriter )
	{
		BPConverterTypesC_Options bpOptions = new BPConverterTypesC_Options();
		bpOptions.mCompressionAlgorithmType = compression.value;
		writeImarisFile( seq, perSetupMipmapInfo, bpOptions, imsFile, afterEachPlane, numCellCreatorThreads, progressWriter );
	}
	
	public static void writeImarisFile(
			final AbstractSequenceDescription< ?, ?, ? > seq,
			final int[] subdivisions,
			final TCompressionAlgorithmType compression,
			final File imsFile,
			final AfterEachPlane afterEachPlane,
			final int numCellCreatorThreads,
			final ProgressWriter progressWriter )
	{
		final Map< Integer, ExportMipmapInfo > perSetupMipmapInfo = ImarisWriterOptions.perSetupMipmapInfo(seq, subdivisions);
		writeImarisFile( seq, perSetupMipmapInfo, compression, imsFile, afterEachPlane, numCellCreatorThreads, progressWriter );
	}
	
	public static void writeImarisFile(
			final AbstractSequenceDescription< ?, ?, ? > seq,
			final File imsFile,
			final ImarisWriterOptions options) {
		writeImarisFile( seq, options.perSetupMipmapInfo, options.bpOptions, imsFile, options.afterEachPlane, options.numCellCreatorThreads, options.progressWriter);
	}
	
	public static void writeImarisSingleFilePerSetup(
			final SequenceDescription seq,
			final Map< Integer, ExportMipmapInfo > perSetupMipmapInfo,
			final BPConverterTypesC_Options bpOptions,
			final File hdf5File,
			final AfterEachPlane afterEachPlane,
			final int numCellCreatorThreads,
			final ProgressWriter progressWriter )
	{
		final HashMap< Integer, Integer > timepointIdSequenceToPartition = new HashMap<>();
		for ( final TimePoint timepoint : seq.getTimePoints().getTimePointsOrdered() )
			timepointIdSequenceToPartition.put( timepoint.getId(), timepoint.getId() );

		final HashMap< Integer, Integer > setupIdSequenceToPartition = new HashMap<>();
		for ( final BasicViewSetup setup : seq.getViewSetupsOrdered() )
			setupIdSequenceToPartition.put( setup.getId(), setup.getId() );

		final Partition partition = new Partition( hdf5File.getPath(), timepointIdSequenceToPartition, setupIdSequenceToPartition );
		writeImarisSingleFilePerSetup( seq, perSetupMipmapInfo, bpOptions, partition, afterEachPlane, numCellCreatorThreads, progressWriter );
	}
	
	public static void writeImarisSingleFilePerSetup(
			final SequenceDescription seq,
			final int[] subdivisions,
			final BPConverterTypesC_Options bpOptions,
			final File imsFile,
			final AfterEachPlane afterEachPlane,
			final int numCellCreatorThreads,
			final ProgressWriter progressWriter )
	{
		final HashMap< Integer, ExportMipmapInfo > perSetupMipmapInfo = new HashMap<>();
		final int[] resolutions = new int[ subdivisions.length ];
		Arrays.fill(resolutions, 1);
		final ExportMipmapInfo mipmapInfo = new ExportMipmapInfo( new int[][]{resolutions}, new int[][]{subdivisions} );
		for ( final BasicViewSetup setup : seq.getViewSetupsOrdered() )
			perSetupMipmapInfo.put( setup.getId(), mipmapInfo );
		writeImarisSingleFilePerSetup( seq, perSetupMipmapInfo, bpOptions, imsFile, afterEachPlane, numCellCreatorThreads, progressWriter );
	}
	
	public static void writeImarisSingleFilePerSetup(
			final SequenceDescription seq,
			final File imsFile,
			final ImarisWriterOptions options) {
		writeImarisSingleFilePerSetup( seq, options.perSetupMipmapInfo, options.bpOptions, imsFile, options.afterEachPlane, options.numCellCreatorThreads, options.progressWriter);
	}
	
	/**
	 * Create a hdf5 partition file containing image data for a subset of views
	 * and timepoints in a chunked, mipmaped representation.
	 *
	 * Please note that the description of the <em>full</em> dataset must be
	 * given in the <code>seq</code>, <code>perSetupResolutions</code>, and
	 * <code>perSetupSubdivisions</code> parameters. Then only the part
	 * described by <code>partition</code> will be written.
	 *
	 * @param seq
	 *            description of the sequence to be stored as hdf5. (The
	 *            {@link AbstractSequenceDescription} contains the number of
	 *            setups and timepoints as well as an {@link BasicImgLoader}
	 *            that provides the image data, Registration information is not
	 *            needed here, that will go into the accompanying xml).
	 * @param perSetupMipmapInfo
	 *            this maps from setup {@link BasicViewSetup#getId() id} to
	 *            {@link ExportMipmapInfo} for that setup. The
	 *            {@link ExportMipmapInfo} contains for each mipmap level, the
	 *            subsampling factors and subdivision block sizes.
	 * @param deflate
	 *            whether to compress the data with the HDF5 DEFLATE filter.
	 * @param partition
	 *            which part of the dataset to write, and to which file.
	 * @param loopbackHeuristic
	 *            heuristic to decide whether to create each resolution level by
	 *            reading pixels from the original image or by reading back a
	 *            finer resolution level already written to the hdf5. may be
	 *            null (in this case always use the original image).
	 * @param afterEachPlane
	 *            this is called after each "plane of chunks" is written, giving
	 *            the opportunity to clear caches, etc.
	 * @param numCellCreatorThreads
	 *            The number of threads that will be instantiated to generate
	 *            cell data. Must be at least 1. (In addition the cell creator
	 *            threads there is one writer thread that saves the generated
	 *            data to HDF5.)
	 * @param progressWriter
	 *            completion ratio and status output will be directed here.
	 */
	public static void writeImarisPartitionFile(
			final AbstractSequenceDescription< ?, ?, ? > seq,
			final Map< Integer, ExportMipmapInfo > perSetupMipmapInfo,
			final BPConverterTypesC_Options bpOptions,
			final Partition partition,
			final AfterEachPlane afterEachPlane,
			final int numCellCreatorThreads,
			ProgressWriter progressWriter )
	{
		//final int blockWriterQueueLength = 100;

		if ( progressWriter == null )
			progressWriter = new ProgressWriterConsole();
		progressWriter.setProgress( 0 );

		// get sequence timepointIds for the timepoints contained in this partition
		final ArrayList< Integer > timepointIdsSequence = new ArrayList<>( partition.getTimepointIdSequenceToPartition().keySet() );
		Collections.sort( timepointIdsSequence );
		final int numTimepoints = timepointIdsSequence.size();
		final ArrayList< Integer > setupIdsSequence = new ArrayList<>( partition.getSetupIdSequenceToPartition().keySet() );
		Collections.sort( setupIdsSequence );

		// get the BasicImgLoader that supplies the images
		final BasicImgLoader imgLoader = seq.getImgLoader();

		for ( final BasicViewSetup setup : seq.getViewSetupsOrdered() ) {
			final Object type = imgLoader.getSetupImgLoader( setup.getId() ).getImageType();
			if ( !( type instanceof UnsignedShortType ) )
				throw new IllegalArgumentException( "Expected BasicImgLoader<UnsignedShortTyp> but your dataset has BasicImgLoader<"
						+ type.getClass().getSimpleName() + ">.\nCurrently writing to ImarisWriter is only supported for UnsignedShortType." );
		}


		// open HDF5 partition output file
		final File imsfile = new File( partition.getPath() );
		if ( imsfile.exists() )
			imsfile.delete();
		//final Hdf5BlockWriterThread writerQueue = new Hdf5BlockWriterThread( imsfile, blockWriterQueueLength );
		try
		{
			//writerQueue.start();

			// start CellCreatorThreads
			final ExecutorService executorService = Executors.newFixedThreadPool( numCellCreatorThreads );
			try
			{
				// calculate number of tasks for progressWriter
				int numTasks = 0; // first task is for writing mipmap descriptions etc...
				for ( final int timepointIdSequence : timepointIdsSequence )
					for ( final int setupIdSequence : setupIdsSequence )
						if ( seq.getViewDescriptions().get( new ViewId( timepointIdSequence, setupIdSequence ) ).isPresent() )
							numTasks++;
				int numCompletedTasks = 0;

				// write Mipmap descriptions
				/*for ( final Entry< Integer, Integer > entry : partition.getSetupIdSequenceToPartition().entrySet() )
				{
					final int setupIdSequence = entry.getKey();
					final int setupIdPartition = entry.getValue();
					final ExportMipmapInfo mipmapInfo = perSetupMipmapInfo.get( setupIdSequence );
					//writerQueue.writeMipmapDescription( setupIdPartition, mipmapInfo );
				}*/

				// Progress of 1% for writing meta data
				progressWriter.setProgress(0.01);
				progressWriter = new SubTaskProgressWriter(progressWriter, 0.01, 1.0);

				// write image data for all views to the HDF5 file
				int timepointIndex = 0;
				for ( final int timepointIdSequence : timepointIdsSequence )
				{
					final int timepointIdPartition = partition.getTimepointIdSequenceToPartition().get( timepointIdSequence );
					progressWriter.out().printf( "proccessing timepoint %d / %d\n", ++timepointIndex, numTimepoints );

					// assemble the viewsetups that are present in this timepoint
					final ArrayList< Integer > setupsTimePoint = new ArrayList<>();

					for ( final int setupIdSequence : setupIdsSequence )
						if ( seq.getViewDescriptions().get( new ViewId( timepointIdSequence, setupIdSequence ) ).isPresent() )
							setupsTimePoint.add( setupIdSequence );

					final int numSetups = setupsTimePoint.size();

					int setupIndex = 0;
					for ( final int setupIdSequence : setupsTimePoint )
					{
						final int setupIdPartition = partition.getSetupIdSequenceToPartition().get( setupIdSequence );
						progressWriter.out().printf( "proccessing setup %d / %d\n", ++setupIndex, numSetups );

						@SuppressWarnings( "unchecked" )
						final RandomAccessibleInterval< UnsignedShortType > img = ( ( BasicSetupImgLoader< UnsignedShortType > ) imgLoader.getSetupImgLoader( setupIdSequence ) ).getImage( timepointIdSequence );
						final ExportMipmapInfo mipmapInfo = perSetupMipmapInfo.get( setupIdSequence );
						final double startCompletionRatio = ( double ) numCompletedTasks++ / numTasks;
						final double endCompletionRatio = ( double ) numCompletedTasks / numTasks;
						final ProgressWriter subProgressWriter = new SubTaskProgressWriter( progressWriter, startCompletionRatio, endCompletionRatio );

						/*writeViewToHdf5PartitionFile(
								img, timepointIdPartition, setupIdPartition, mipmapInfo, false,
								deflate, writerQueue, executorService, numCellCreatorThreads, loopbackHeuristic, afterEachPlane, subProgressWriter );*/
						writeViewToImarisWriterFile(
								img,
								timepointIdPartition, setupIdPartition,
								mipmapInfo, bpOptions,
								executorService, numCellCreatorThreads,
								afterEachPlane, subProgressWriter);
					}
				}
			}
			finally
			{
				executorService.shutdown();
			}
		}
		finally {
			//writerQueue.close();
		}
		progressWriter.setProgress( 1.0 );
	}
	
	/**
	 * Create a hdf5 partition file containing image data for a subset of views
	 * and timepoints in a chunked, mipmaped representation.
	 *
	 * Please note that the description of the <em>full</em> dataset must be
	 * given in the <code>seq</code>, <code>perSetupResolutions</code>, and
	 * <code>perSetupSubdivisions</code> parameters. Then only the part
	 * described by <code>partition</code> will be written.
	 *
	 * @param seq
	 *            description of the sequence to be stored as hdf5. (The
	 *            {@link AbstractSequenceDescription} contains the number of
	 *            setups and timepoints as well as an {@link BasicImgLoader}
	 *            that provides the image data, Registration information is not
	 *            needed here, that will go into the accompanying xml).
	 * @param perSetupMipmapInfo
	 *            this maps from setup {@link BasicViewSetup#getId() id} to
	 *            {@link ExportMipmapInfo} for that setup. The
	 *            {@link ExportMipmapInfo} contains for each mipmap level, the
	 *            subsampling factors and subdivision block sizes.
	 * @param deflate
	 *            whether to compress the data with the HDF5 DEFLATE filter.
	 * @param partition
	 *            which part of the dataset to write, and to which file.
	 * @param loopbackHeuristic
	 *            heuristic to decide whether to create each resolution level by
	 *            reading pixels from the original image or by reading back a
	 *            finer resolution level already written to the hdf5. may be
	 *            null (in this case always use the original image).
	 * @param afterEachPlane
	 *            this is called after each "plane of chunks" is written, giving
	 *            the opportunity to clear caches, etc.
	 * @param numCellCreatorThreads
	 *            The number of threads that will be instantiated to generate
	 *            cell data. Must be at least 1. (In addition the cell creator
	 *            threads there is one writer thread that saves the generated
	 *            data to HDF5.)
	 * @param progressWriter
	 *            completion ratio and status output will be directed here.
	 */
	public static void writeImarisSingleFilePerSetup(
			final SequenceDescription seq,
			final Map< Integer, ExportMipmapInfo > perSetupMipmapInfo,
			final BPConverterTypesC_Options bpOptions,
			final Partition partition,
			final AfterEachPlane afterEachPlane,
			final int numCellCreatorThreads,
			ProgressWriter progressWriter )
	{
		//final int blockWriterQueueLength = 100;
		long totalLoadingTime = 0;

		if ( progressWriter == null )
			progressWriter = new ProgressWriterConsole();
		progressWriter.setProgress( 0 );

		// get sequence timepointIds for the timepoints contained in this partition
		final ArrayList< Integer > timepointIdsSequence = new ArrayList<>( partition.getTimepointIdSequenceToPartition().keySet() );
		Collections.sort( timepointIdsSequence );
		final int numTimepoints = timepointIdsSequence.size();
		final ArrayList< Integer > setupIdsSequence = new ArrayList<>( partition.getSetupIdSequenceToPartition().keySet() );
		Collections.sort( setupIdsSequence );

		// get the BasicImgLoader that supplies the images
		final ImgLoader imgLoader = seq.getImgLoader();

		for ( final BasicViewSetup setup : seq.getViewSetupsOrdered() ) {
			final Object type = imgLoader.getSetupImgLoader( setup.getId() ).getImageType();
			if ( !( type instanceof UnsignedShortType ) )
				throw new IllegalArgumentException( "Expected BasicImgLoader<UnsignedShortTyp> but your dataset has BasicImgLoader<"
						+ type.getClass().getSimpleName() + ">.\nCurrently writing to ImarisWriter is only supported for UnsignedShortType." );
		}


		// open HDF5 partition output file
		final File imsfile = new File( partition.getPath() );
		if ( imsfile.exists() )
			imsfile.delete();
		//final Hdf5BlockWriterThread writerQueue = new Hdf5BlockWriterThread( imsfile, blockWriterQueueLength );
		try
		{
			//writerQueue.start();

			// start CellCreatorThreads
			final ExecutorService executorService = Executors.newFixedThreadPool( numCellCreatorThreads );
			try
			{
				// calculate number of tasks for progressWriter
				int numTasks = 0; // first task is for writing mipmap descriptions etc...
				for ( final int timepointIdSequence : timepointIdsSequence )
					for ( final int setupIdSequence : setupIdsSequence )
						if ( seq.getViewDescriptions().get( new ViewId( timepointIdSequence, setupIdSequence ) ).isPresent() )
							numTasks++;
				int numCompletedTasks = 0;

				// write Mipmap descriptions
				/*for ( final Entry< Integer, Integer > entry : partition.getSetupIdSequenceToPartition().entrySet() )
				{
					final int setupIdSequence = entry.getKey();
					final int setupIdPartition = entry.getValue();
					final ExportMipmapInfo mipmapInfo = perSetupMipmapInfo.get( setupIdSequence );
					//writerQueue.writeMipmapDescription( setupIdPartition, mipmapInfo );
				}*/

				// Progress of 1% for writing meta data
				progressWriter.setProgress(0.01);
				progressWriter = new SubTaskProgressWriter(progressWriter, 0.01, 1.0);
				
				// write image data for all views to the HDF5 file
				int timepointIndex = 0;
				final ImarisWriterDatasetIO io = new ImarisWriterDatasetIO(imsfile.getAbsolutePath(), progressWriter, bpOptions, 1, timepointIdsSequence.size());
				
				for ( final int timepointIdSequence : timepointIdsSequence )
				{
					io.setCurrentTimepointId(timepointIdSequence);
					
					final DatasetIO< Pointer, UnsignedShortType > fakeio = new DatasetIO< Pointer, UnsignedShortType >() {
						Pointer pConverter = null;

						@Override
						public Pointer createDataset(int level, long[] dimensions, int[] blockSize) throws IOException {
							if(pConverter == null) {
								pConverter = io.createDataset(level, dimensions, blockSize);
							}
							return pConverter;
						}

						@Override
						public void writeBlock(Pointer dataset, Block<UnsignedShortType> dataBlock) throws IOException {
							io.writeBlock(dataset, dataBlock);
						}

						@Override
						public void flush(Pointer dataset) throws IOException {
							// do nothing
						}
					};
					
					final int timepointIdPartition = partition.getTimepointIdSequenceToPartition().get( timepointIdSequence );
					progressWriter.out().printf( "proccessing timepoint %d / %d\n", ++timepointIndex, numTimepoints );

					// assemble the viewsetups that are present in this timepoint
					final ArrayList< Integer > setupsTimePoint = new ArrayList<>();

					for ( final int setupIdSequence : setupIdsSequence )
						if ( seq.getViewDescriptions().get( new ViewId( timepointIdSequence, setupIdSequence ) ).isPresent() )
							setupsTimePoint.add( setupIdSequence );

					final int numSetups = setupsTimePoint.size();

					int setupIndex = 0;
					for ( final int setupIdSequence : setupsTimePoint )
					{
						final int setupIdPartition = partition.getSetupIdSequenceToPartition().get( setupIdSequence );
						progressWriter.out().printf( "proccessing setup %d / %d\n", ++setupIndex, numSetups );

						long start = System.currentTimeMillis();
						@SuppressWarnings( "unchecked" )
						final RandomAccessibleInterval< UnsignedShortType > img = ( ( BasicSetupImgLoader< UnsignedShortType > ) imgLoader.getSetupImgLoader( setupIdSequence ) ).getImage( timepointIdSequence );
						//System.out.println("Loading (ms): " + (System.currentTimeMillis() - start));
						totalLoadingTime +=  (System.currentTimeMillis() - start);
						final ExportMipmapInfo mipmapInfo = perSetupMipmapInfo.get( setupIdSequence );
						final double startCompletionRatio = ( double ) numCompletedTasks++ / numTasks;
						final double endCompletionRatio = ( double ) numCompletedTasks / numTasks;
						final ProgressWriter subProgressWriter = new SubTaskProgressWriter( progressWriter, startCompletionRatio, endCompletionRatio );

						/*writeViewToHdf5PartitionFile(
								img, timepointIdPartition, setupIdPartition, mipmapInfo, false,
								deflate, writerQueue, executorService, numCellCreatorThreads, loopbackHeuristic, afterEachPlane, subProgressWriter );*/
						/*writeViewToImarisWriterFile(
								img,
								timepointIdPartition, setupIdPartition,
								mipmapInfo, compression,
								executorService, numCellCreatorThreads,
								afterEachPlane, subProgressWriter);*/
						writeDatasetToImarisWriterFile(
								img,
								fakeio,
								mipmapInfo,
								executorService, numCellCreatorThreads,
								afterEachPlane, subProgressWriter);
					}
				}
				
				try {
					io.flush(io.pConverter);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			finally
			{
				executorService.shutdown();
			}
		}
		finally {
			//writerQueue.close();
		}
		progressWriter.setProgress( 1.0 );
		System.out.println("Total loading time (ms): " + totalLoadingTime);
	}
	
	/**
	 * Write a single view to a hdf5 partition file, in a chunked, mipmaped
	 * representation. Note that the specified view must not already exist in
	 * the partition file!
	 *
	 * @param img
	 *            the view to be written.
	 * @param timepointIdPartition
	 *            the timepoint id wrt the partition of the view to be written.
	 *            The information in {@code partition} relates this to timepoint
	 *            id in the full sequence.
	 * @param setupIdPartition
	 *            the setup id wrt the partition of the view to be written. The
	 *            information in {@code partition} relates this to setup id in
	 *            the full sequence.
	 * @param mipmapInfo
	 *            contains for each mipmap level of the setup, the subsampling
	 *            factors and subdivision block sizes.
	 * @param writeMipmapInfo
	 *            whether to write mipmap description for the setup. must be
	 *            done (at least) once for each setup in the partition.
	 * @param deflate
	 *            whether to compress the data with the HDF5 DEFLATE filter.
	 * @param writerQueue
	 *            block writing tasks are enqueued here.
	 * @param executorService
	 *            executor used for creating (possibly down-sampled) blocks of
	 *            the view to be written.
	 * @param numThreads
	 * @param loopbackHeuristic
	 *            heuristic to decide whether to create each resolution level by
	 *            reading pixels from the original image or by reading back a
	 *            finer resolution level already written to the hdf5. may be
	 *            null (in this case always use the original image).
	 * @param afterEachPlane
	 *            this is called after each "plane of chunks" is written, giving
	 *            the opportunity to clear caches, etc.
	 * @param progressWriter
	 *            completion ratio and status output will be directed here. may
	 *            be null.
	 */
	public static void writeViewToImarisWriterFile(
			final RandomAccessibleInterval< UnsignedShortType > img,
			final int timepointIdPartition,
			final int setupIdPartition,
			final ExportMipmapInfo mipmapInfo,
			final BPConverterTypesC_Options bpOptions,
			final ExecutorService executorService, // TODO
			final int numThreads, // TODO
			final AfterEachPlane afterEachPlane,
			ProgressWriter progressWriter )
	{

		final DatasetIO< Pointer, UnsignedShortType > io = new ImarisWriterDatasetIO("writeSequenceTest" + timepointIdPartition + ".ims", progressWriter, bpOptions ,1,1);

		writeDatasetToImarisWriterFile(
				img,
				io,
				mipmapInfo,
				executorService,
				numThreads,
				afterEachPlane,
				progressWriter);
				

	}
	
	public static void writeDatasetToImarisWriterFile(
			final RandomAccessibleInterval< UnsignedShortType > img,
			final DatasetIO< Pointer, UnsignedShortType > io,
			final ExportMipmapInfo mipmapInfo,
			final ExecutorService executorService,
			final int numThreads, // TODO
			final AfterEachPlane afterEachPlane,
			ProgressWriter progressWriter)
	{
		/*
		 * Extend the image such that all the blocks can be of the same size
		 */
		int n = img.numDimensions();
		long[] dimensions = new long[n];
		img.dimensions(dimensions);

		// Find the lowest common multiple (lcm) of the subdivisions (cell size)
		int[][] subdivisions = mipmapInfo.getSubdivisions();
		int[] lcm = subdivisions[0];
		// This might not be necessary since we only have one level of blocks to send to ImarisWriter
		for(int i = 1; i < subdivisions.length; ++i)
			for(int j = 0; j < n; ++j)
				lcm[j] = ArithmeticUtils.lcm(lcm[j], subdivisions[i][j]);

		// Round up the image size to the nearest multiple of lcm
		for(int i = 0; i < n; ++i) {
			// Do integer arithmetic to find the lcm multiplier
			dimensions[i] = (dimensions[i] + lcm[i] - 1)/lcm[i];
			dimensions[i] *= lcm[i];
		}
		
		//dimensions[0] = 4096;
		//dimensions[1] = 768;
		//dimensions[2] = 16;
		
		// Pad the image by zeros
		RandomAccessibleInterval< UnsignedShortType > extendedImg = Views.interval( Views.extendZero(img) , new FinalInterval(dimensions) );
		
		try
		{
			ExportScalePyramid.writeScalePyramid(
					extendedImg,
					new UnsignedShortType(),
					mipmapInfo,
					io,
					executorService,
					numThreads,
					null, // loopbackHeuristic is irrelevant since ImarisWriter handles levels
					afterEachPlane,
					progressWriter );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public static class ImarisWriterOptions implements Cloneable {
		public AfterEachPlane afterEachPlane;
		public int numCellCreatorThreads;
		public ProgressWriter progressWriter;
		public Map< Integer, ExportMipmapInfo > perSetupMipmapInfo;
		public BPConverterTypesC_Options bpOptions;
		
		public ImarisWriterOptions() {
			bpOptions = new BPConverterTypesC_Options();
		}
		
		protected ImarisWriterOptions(ImarisWriterOptions other) {
			this.afterEachPlane = other.afterEachPlane;
			this.numCellCreatorThreads = other.numCellCreatorThreads;
			this.progressWriter = other.progressWriter;
			this.perSetupMipmapInfo = other.perSetupMipmapInfo;
			this.bpOptions = other.bpOptions;
		}
		
		public static Map< Integer, ExportMipmapInfo > perSetupMipmapInfo(
				final AbstractSequenceDescription< ?, ?, ? > seq,
				final int[] resolutions,
				final int[] subdivisions) {
			final HashMap< Integer, ExportMipmapInfo > perSetupMipmapInfo = new HashMap<>();
			final ExportMipmapInfo mipmapInfo = new ExportMipmapInfo( new int[][]{resolutions}, new int[][]{subdivisions} );
			for ( final BasicViewSetup setup : seq.getViewSetupsOrdered() )
				perSetupMipmapInfo.put( setup.getId(), mipmapInfo );
			return perSetupMipmapInfo;
		}
		
		public static Map< Integer, ExportMipmapInfo > perSetupMipmapInfo(
				final AbstractSequenceDescription< ?, ?, ? > seq,
				final int[] subdivisions) {
			final int[] resolutions = new int[ subdivisions.length ];
			Arrays.fill(resolutions, 1);
			return perSetupMipmapInfo(seq, resolutions, subdivisions);
		}
		
		public void setCompression(TCompressionAlgorithmType compression) {
			bpOptions.mCompressionAlgorithmType = compression.value;
		}
		
		public ImarisWriterOptions clone() {
			return new ImarisWriterOptions(this);
		}
		
		public static ImarisWriterOptions getDefault(final AbstractSequenceDescription< ?, ?, ? > seq) {
			ImarisWriterOptions options = new ImarisWriterOptions();
			options.setCompression( TCompressionAlgorithmType.eCompressionAlgorithmNone );
			options.afterEachPlane = new AfterEachPlane() {

				@Override
				public void afterEachPlane(boolean usedLoopBack) {
				}
				
			};
			options.numCellCreatorThreads = 1;
			options.progressWriter = new ProgressWriterConsole();
			
			int[] subdivisions = new int[] { 96, 96, 12 };
			
			options.perSetupMipmapInfo = ImarisWriterOptions.perSetupMipmapInfo(seq, subdivisions);
			
			
			return options;
		}
	}
}
