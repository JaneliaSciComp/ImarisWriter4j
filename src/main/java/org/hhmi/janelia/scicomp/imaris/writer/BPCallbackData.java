package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mImageIndex","mProgress"})
public class BPCallbackData extends Structure {
	public static class ByReference extends BPCallbackData implements Structure.ByReference {}

	public int mImageIndex; // unsigned int
	public int mProgress;
}
