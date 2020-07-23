package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

// typedef const bpConverterTypesC_ColorInfos* bpConverterTypesC_ColorInfoVector;

@Structure.FieldOrder({"mValues","mValuesCount"})
public class BPConverterTypesC_ColorInfos extends Structure {
	public static class ByReference extends BPConverterTypesC_ColorInfos implements Structure.ByReference { }
	//public BPConverterTypesC_ColorInfo[] mValues = new BPConverterTypesC_ColorInfo[1]; // const bpConverterTypesC_ColorInfo*
	public BPConverterTypesC_ColorInfo.ByReference mValues; // BPConverterTypesC_ColorInfo
	public int mValuesCount; // unsigned int
}
