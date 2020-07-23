package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

// typedef const bpConverterTypesC_TimeInfos* bpConverterTypesC_TimeInfoVector;

@Structure.FieldOrder({"mValues","mValuesCount"})
public class BPConverterTypesC_TimeInfos extends Structure {
	public static class ByReference extends BPConverterTypesC_TimeInfos implements Structure.ByReference {
		@Override
		public ByReference[] toArray(int size) {
			// TODO Auto-generated method stub
			return (ByReference[]) super.toArray(size);
		}
	}
	// public BPConverterTypesC_TimeInfo.ByReference[] mValues = new BPConverterTypesC_TimeInfo.ByReference[5]; // const bpConverterTypesC_TimeInfo*
	public BPConverterTypesC_TimeInfo.ByReference mValues; // const bpConverterTypesC_TimeInfo*
	public int mValuesCount; // unsigned int 
}
