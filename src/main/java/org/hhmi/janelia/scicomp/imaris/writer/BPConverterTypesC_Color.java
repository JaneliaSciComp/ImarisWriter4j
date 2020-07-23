package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

@Structure.FieldOrder({"mRed","mGreen","mBlue","mAlpha"})
public class BPConverterTypesC_Color extends Structure {
	public float mRed;
	public float mGreen;
	public float mBlue;
	public float mAlpha;
}
