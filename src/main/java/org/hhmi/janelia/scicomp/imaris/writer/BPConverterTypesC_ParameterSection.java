package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

//typedef const char* bpConverterTypesC_String;

@Structure.FieldOrder({"mName","mValues","mValuesCount"})
public class BPConverterTypesC_ParameterSection extends Structure {
	public static class ByReference extends BPConverterTypesC_ParameterSection implements Structure.ByReference {
		@Override
		public ByReference[] toArray(int size) {
			// TODO Auto-generated method stub
			return (ByReference[]) super.toArray(size);
		}
	}

	public String mName; // bpConverterTypesC_String
	// 	public BPConverterTypesC_Parameter.ByReference[] mValues = new BPConverterTypesC_Parameter.ByReference[5]; // const bpConverterTypesC_Parameter*
	public BPConverterTypesC_Parameter.ByReference mValues; // const bpConverterTypesC_Parameter*
	public int mValuesCount; // unsigned int 
}
