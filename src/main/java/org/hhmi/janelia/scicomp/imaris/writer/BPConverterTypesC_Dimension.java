package org.hhmi.janelia.scicomp.imaris.writer;

public enum BPConverterTypesC_Dimension {
	  bpConverterTypesC_DimensionX(0),
	  bpConverterTypesC_DimensionY(1),
	  bpConverterTypesC_DimensionZ(2),
	  bpConverterTypesC_DimensionC(3),
	  bpConverterTypesC_DimensionT(4);
	
	public final int value;
	
	BPConverterTypesC_Dimension(int v) {
		this.value = v;
	}
}
