package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

/*typedef unsigned char bpConverterTypesC_UInt8;
typedef unsigned short int bpConverterTypesC_UInt16;
typedef unsigned int bpConverterTypesC_UInt32;
typedef unsigned long long bpConverterTypesC_UInt64;
typedef float bpConverterTypesC_Float;*/

@Structure.FieldOrder({"mDimension0","mDimension1","mDimension2","mDimension3","mDimension4"})
public class BPConverterTypesC_DimensionSequence5D extends Structure {
	public static class ByReference extends BPConverterTypesC_DimensionSequence5D implements Structure.ByReference {

		public ByReference(BPConverterTypesC_Dimension bpconvertertypescDimensionx,
				BPConverterTypesC_Dimension bpconvertertypescDimensiony,
				BPConverterTypesC_Dimension bpconvertertypescDimensionz,
				BPConverterTypesC_Dimension bpconvertertypescDimensionc,
				BPConverterTypesC_Dimension bpconvertertypescDimensiont) {
			super(bpconvertertypescDimensionx, bpconvertertypescDimensiony, bpconvertertypescDimensionz,
					bpconvertertypescDimensionc, bpconvertertypescDimensiont);
			// TODO Auto-generated constructor stub
		}

		public ByReference(int mDimension0, int mDimension1, int mDimension2, int mDimension3, int mDimension4) {
			super(mDimension0, mDimension1, mDimension2, mDimension3, mDimension4);
			// TODO Auto-generated constructor stub
		}
		
	}

	public int mDimension0; // bpConverterTypesC_Dimension
	public int mDimension1; // bpConverterTypesC_Dimension
	public int mDimension2; // bpConverterTypesC_Dimension
	public int mDimension3; // bpConverterTypesC_Dimension
	public int mDimension4; // bpConverterTypesC_Dimension

	public BPConverterTypesC_DimensionSequence5D(int mDimension0, int mDimension1, int mDimension2, int mDimension3,
			int mDimension4) {
		super();
		this.mDimension0 = mDimension0;
		this.mDimension1 = mDimension1;
		this.mDimension2 = mDimension2;
		this.mDimension3 = mDimension3;
		this.mDimension4 = mDimension4;
	}

	public BPConverterTypesC_DimensionSequence5D(BPConverterTypesC_Dimension bpconvertertypescDimensionx,
			BPConverterTypesC_Dimension bpconvertertypescDimensiony,
			BPConverterTypesC_Dimension bpconvertertypescDimensionz,
			BPConverterTypesC_Dimension bpconvertertypescDimensionc,
			BPConverterTypesC_Dimension bpconvertertypescDimensiont) {
		super();
		this.mDimension0 = (int) mDimension0;
		this.mDimension1 = (int) mDimension1;
		this.mDimension2 = (int) mDimension2;
		this.mDimension3 = (int) mDimension3;
		this.mDimension4 = (int) mDimension4;
	}
}
