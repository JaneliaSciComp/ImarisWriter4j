package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mValues","mValuesCount"})
public class BPConverterTypesC_Parameters extends Structure {
	public static class ByReference extends BPConverterTypesC_Parameters implements Structure.ByReference {

		public ByReference(BPConverterTypesC_ParameterSection.ByReference mValues, int mValuesCount) {
			super(mValues, mValuesCount);
			// TODO Auto-generated constructor stub
		}
		public ByReference() {
			super();
		}
	}
	// 	public BPConverterTypesC_ParameterSection.ByReference[] mValues = new BPConverterTypesC_ParameterSection.ByReference[5]; // const bpConverterTypesC_ParameterSection*
	public BPConverterTypesC_ParameterSection.ByReference mValues; // const bpConverterTypesC_ParameterSection*
	public int mValuesCount; // unsigned int
	
	public BPConverterTypesC_Parameters(BPConverterTypesC_ParameterSection.ByReference mValues, int mValuesCount) {
		super();
		this.mValues = mValues;
		this.mValuesCount = mValuesCount;
	}
	public BPConverterTypesC_Parameters() {
		super();
	}
}
