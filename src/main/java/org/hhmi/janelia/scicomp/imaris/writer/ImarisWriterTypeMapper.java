package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

public class ImarisWriterTypeMapper extends DefaultTypeMapper implements TypeConverter {
	
	public ImarisWriterTypeMapper() {
		// TODO Auto-generated constructor stub
		this.addTypeConverter(Boolean.class, this);
		//this.addTypeConverter(TCompressionAlgorithmType.class, new TCompressionAlgorithmType.CompressionAlgorithmTypeConverter());
	}
	
	public Object fromNative(Object nativeValue, FromNativeContext context) {
		// TODO Auto-generated method stub
		//System.out.println("fromNative");
		return ( (byte) nativeValue ) != 0;
	}

	public Class<?> nativeType() {
		// TODO Auto-generated method stub
		return byte.class;
	}

	public Object toNative(Object value, ToNativeContext context) {
		/*
		 * System.out.println("toNative"); System.out.println(context.getClass());
		 * if(context instanceof MethodParameterContext) {
		 * System.out.println(((MethodParameterContext) context).getMethod()); }
		 */
		// TODO Auto-generated method stub
        if(value.getClass() == Boolean.class) {
            value = (boolean) value;
        }
        if((boolean) value) {
            return (byte) 0xFF;
        } else {
            return (byte) 0;
        }
	}

	
	/*
	 * @Override public FromNativeConverter getFromNativeConverter(Class<?>
	 * javaType) { // TODO Auto-generated method stub
	 * System.out.println("getFromNativeConverter: " + javaType); return
	 * super.getFromNativeConverter(javaType); }
	 * 
	 * @Override public ToNativeConverter getToNativeConverter(Class<?> javaType) {
	 * // TODO Auto-generated method stub
	 * System.out.println("getToNativeConverter: " + javaType); return
	 * super.getToNativeConverter(javaType); }
	 */
	 
	
	



}
