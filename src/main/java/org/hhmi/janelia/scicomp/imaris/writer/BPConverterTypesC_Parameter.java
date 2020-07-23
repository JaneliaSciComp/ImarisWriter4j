package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

// typedef const char* bpConverterTypesC_String;
@Structure.FieldOrder({"mName","mValue"})
public class BPConverterTypesC_Parameter extends Structure {
	public static class ByReference extends BPConverterTypesC_Parameter implements Structure.ByReference {

		public ByReference() {
			super();
			// TODO Auto-generated constructor stub
		}

		public ByReference(String mName, String mValue) {
			super(mName, mValue);
			// TODO Auto-generated constructor stub
		}

		@Override
		public ByReference[] toArray(int size) {
			// TODO Auto-generated method stub
			return (ByReference[]) super.toArray(size);
		}
		
	}

	public String mName; // bpConverterTypesC_String
	public String mValue; // bpConverterTypesC_String
	public BPConverterTypesC_Parameter(String mName, String mValue) {
		super();
		this.mName = mName;
		this.mValue = mValue;
	}
	public BPConverterTypesC_Parameter() {
		super();
	}
}
