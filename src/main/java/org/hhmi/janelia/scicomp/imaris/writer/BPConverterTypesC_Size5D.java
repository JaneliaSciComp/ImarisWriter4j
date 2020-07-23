package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mValueX","mValueY","mValueZ","mValueC","mValueT"})
public class BPConverterTypesC_Size5D extends Structure {
	public static class ByReference extends BPConverterTypesC_Size5D implements Structure.ByReference {

		public ByReference() {
			super();
			// TODO Auto-generated constructor stub
		}

		public ByReference(int mValueX, int mValueY, int mValueZ, int mValueC, int mValueT) {
			super(mValueX, mValueY, mValueZ, mValueC, mValueT);
			// TODO Auto-generated constructor stub
		}
	}
	public int mValueX; // unsigned int
	public int mValueY; // unsigned int
	public int mValueZ; // unsigned int
	public int mValueC; // unsigned int
	public int mValueT; // unsigned int

	public BPConverterTypesC_Size5D(int mValueX, int mValueY, int mValueZ, int mValueC, int mValueT) {
		super();
		this.mValueX = mValueX;
		this.mValueY = mValueY;
		this.mValueZ = mValueZ;
		this.mValueC = mValueC;
		this.mValueT = mValueT;
	}

	public BPConverterTypesC_Size5D() {
		super();
		this.mValueX = 0;
		this.mValueY = 0;
		this.mValueZ = 0;
		this.mValueC = 0;
		this.mValueT = 0;
	}


}
