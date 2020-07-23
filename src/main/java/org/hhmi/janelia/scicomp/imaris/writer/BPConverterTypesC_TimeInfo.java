package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mJulianDay","mNanosecondsOfDay"})
public class BPConverterTypesC_TimeInfo extends Structure {
	public static class ByReference extends BPConverterTypesC_TimeInfo implements Structure.ByReference {
		@Override
		public ByReference[] toArray(int size) {
			// TODO Auto-generated method stub
			return (ByReference[]) super.toArray(size);
		}
	}

	public int mJulianDay; // unsigned int 
	public long mNanosecondsOfDay; // unsigned long long
}
