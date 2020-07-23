package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mValueX","mValueY","mValueZ","mValueC","mValueT"})
public class BPConverterTypesC_Index5D extends Structure {
	public static class ByReference extends BPConverterTypesC_Index5D implements Structure.ByReference {}
	public int mValueX; // unsigned int
	public int mValueY; // unsigned int
	public int mValueZ; // unsigned int
	public int mValueC; // unsigned int
	public int mValueT; // unsigned int
}
