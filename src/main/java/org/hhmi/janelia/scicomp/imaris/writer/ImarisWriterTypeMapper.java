package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

/**
 * ImarisWriterTypeMapper currently converts Java boolean to C99 bool (a byte)
 * 
 * @author kittisopikulm
 *
 */
public class ImarisWriterTypeMapper extends DefaultTypeMapper implements TypeConverter {
	
	public ImarisWriterTypeMapper() {
		this.addTypeConverter(Boolean.class, this);
		//this.addTypeConverter(TCompressionAlgorithmType.class, new TCompressionAlgorithmType.CompressionAlgorithmTypeConverter());
	}
	
	public Object fromNative(Object nativeValue, FromNativeContext context) {

		return ( (byte) nativeValue ) != 0;
	}

	public Class<?> nativeType() {
		return byte.class;
	}

	public Object toNative(Object value, ToNativeContext context) {	
		//An example context would be MethodParameterContext
		
		// Use C99 bool which is a byte (rather than an int)
        if(value.getClass() == Boolean.class) {
            value = (boolean) value;
        }
        if((boolean) value) {
            return (byte) 0xFF;
        } else {
            return (byte) 0;
        }
	}

}
