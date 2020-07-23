package org.hhmi.janelia.scicomp.imaris.writer;

/*typedef unsigned char bpConverterTypesC_UInt8;
typedef unsigned short int bpConverterTypesC_UInt16;
typedef unsigned int bpConverterTypesC_UInt32;
typedef unsigned long long bpConverterTypesC_UInt64;
typedef float bpConverterTypesC_Float;*/

public enum BPConverterTypesC_DataType {
	  bpConverterTypesC_UInt8Type(0),
	  bpConverterTypesC_UInt16Type(1),
	  bpConverterTypesC_UInt32Type(2),
	  bpConverterTypesC_FloatType(3);
	  
	  public final int value;
	  
	  BPConverterTypesC_DataType(int v) {
		  this.value = v;
	  }
}
