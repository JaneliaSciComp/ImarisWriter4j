package org.hhmi.janelia.scicomp.imaris.writer;

import java.util.Arrays;

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
	
	public BPConverterTypesC_Size5D(int... dimensions) {
		if(dimensions.length > 0)
			this.mValueX = dimensions[0];
		else
			this.mValueX = 1;
		
		if(dimensions.length > 1)
			this.mValueY = dimensions[1];
		else
			this.mValueY = 1;
		
		if(dimensions.length > 2)
			this.mValueZ = dimensions[2];
		else
			this.mValueZ = 1;
		
		if(dimensions.length > 3)
			this.mValueC = dimensions[3];
		else
			this.mValueC = 1;
		
		if(dimensions.length > 4)
			this.mValueT = dimensions[4];
		else
			this.mValueT = 1;
	}
	
	public BPConverterTypesC_Size5D(long... dimensions) {
		this( Arrays.stream( dimensions ).mapToInt(i->(int) i).toArray() );
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
