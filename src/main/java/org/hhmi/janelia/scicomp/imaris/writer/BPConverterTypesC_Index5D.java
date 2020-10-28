package org.hhmi.janelia.scicomp.imaris.writer;

import java.util.Arrays;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mValueX","mValueY","mValueZ","mValueC","mValueT"})
public class BPConverterTypesC_Index5D extends Structure {
	public BPConverterTypesC_Index5D(int... gridPosition) {
		if(gridPosition.length > 0)
			this.mValueX = gridPosition[0];
		else
			this.mValueX = 0;
		
		if(gridPosition.length > 1)
			this.mValueY = gridPosition[1];
		else
			this.mValueY = 0;
		
		if(gridPosition.length > 2)
			this.mValueZ = gridPosition[2];
		else
			this.mValueZ = 0;
		
		if(gridPosition.length > 3)
			this.mValueC = gridPosition[3];
		else
			this.mValueC = 0;
		
		if(gridPosition.length > 4)
			this.mValueT = gridPosition[4];
		else
			this.mValueT = 0;
	}
	public BPConverterTypesC_Index5D(long... gridPosition) {
		this( Arrays.stream(gridPosition).mapToInt(i->(int)i).toArray() );
	}
	public static class ByReference extends BPConverterTypesC_Index5D implements Structure.ByReference {}
	public int mValueX; // unsigned int
	public int mValueY; // unsigned int
	public int mValueZ; // unsigned int
	public int mValueC; // unsigned int
	public int mValueT; // unsigned int
}
