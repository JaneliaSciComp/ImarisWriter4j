package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mExtentMinX","mExtentMinY","mExtentMinZ","mExtentMaxX","mExtentMaxY","mExtentMaxZ"})
public class BPConverterTypesC_ImageExtent extends Structure {
	public static class ByReference extends BPConverterTypesC_ImageExtent implements Structure.ByReference {
		public ByReference(float mExtentMinX, float mExtentMinY, float mExtentMinZ, float mExtentMaxX,
				float mExtentMaxY, float mExtentMaxZ) {
			super(mExtentMinX, mExtentMinY, mExtentMinZ, mExtentMaxX,
				mExtentMaxY, mExtentMaxZ);
		}
		public ByReference() {
			super();
		}
	}

	public float mExtentMinX;
	public float mExtentMinY;
	public float mExtentMinZ;
	public float mExtentMaxX;
	public float mExtentMaxY;
	public float mExtentMaxZ;
	
	public BPConverterTypesC_ImageExtent() {
		super();
	}
	
	public BPConverterTypesC_ImageExtent(float mExtentMinX, float mExtentMinY, float mExtentMinZ, float mExtentMaxX,
			float mExtentMaxY, float mExtentMaxZ) {
		super();
		this.mExtentMinX = mExtentMinX;
		this.mExtentMinY = mExtentMinY;
		this.mExtentMinZ = mExtentMinZ;
		this.mExtentMaxX = mExtentMaxX;
		this.mExtentMaxY = mExtentMaxY;
		this.mExtentMaxZ = mExtentMaxZ;
	}


}
