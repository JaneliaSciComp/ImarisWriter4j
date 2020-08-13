package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

public enum TCompressionAlgorithmType {
	eCompressionAlgorithmNone ( 0 ),
	eCompressionAlgorithmGzipLevel1 ( 1 ),
	eCompressionAlgorithmGzipLevel2 ( 2 ),
	eCompressionAlgorithmGzipLevel3 ( 3 ),
	eCompressionAlgorithmGzipLevel4 ( 4 ),
	eCompressionAlgorithmGzipLevel5 ( 5 ),
	eCompressionAlgorithmGzipLevel6 ( 6 ),
	eCompressionAlgorithmGzipLevel7 ( 7 ),
	eCompressionAlgorithmGzipLevel8 ( 8 ),
	eCompressionAlgorithmGzipLevel9 ( 9 ),
	eCompressionAlgorithmShuffleGzipLevel1 ( 11 ),
	eCompressionAlgorithmShuffleGzipLevel2 ( 12 ),
	eCompressionAlgorithmShuffleGzipLevel3 ( 13 ),
	eCompressionAlgorithmShuffleGzipLevel4 ( 14 ),
	eCompressionAlgorithmShuffleGzipLevel5 ( 15 ),
	eCompressionAlgorithmShuffleGzipLevel6 ( 16 ),
	eCompressionAlgorithmShuffleGzipLevel7 ( 17 ),
	eCompressionAlgorithmShuffleGzipLevel8 ( 18 ),
	eCompressionAlgorithmShuffleGzipLevel9 ( 19 ),
	eCompressionAlgorithmLZ4 ( 21 ),
	eCompressionAlgorithmLShuffleLZ4 ( 31 );

	public final int value;

	TCompressionAlgorithmType(int i) {
		this.value = i;
	}
	
	public static class CompressionAlgorithmTypeConverter implements TypeConverter {
		
		private static TCompressionAlgorithmType[] cachedValues = TCompressionAlgorithmType.values();
		
		public static TCompressionAlgorithmType fromInt(int i) {
			for ( TCompressionAlgorithmType type : cachedValues ) {
				if( i == type.value )
					return type;
			}
			return eCompressionAlgorithmNone;
			/*
			 * switch(i) { case eCompressionAlgorithmNone.value: return
			 * eCompressionAlgorithmNone; case eCompressionAlgorithmGzipLevel1.value: return
			 * eCompressionAlgorithmGzipLevel1; case eCompressionAlgorithmGzipLevel2.value:
			 * return eCompressionAlgorithmGzipLevel2; case
			 * eCompressionAlgorithmGzipLevel3.value: return
			 * eCompressionAlgorithmGzipLevel3; case eCompressionAlgorithmGzipLevel4.value:
			 * return eCompressionAlgorithmGzipLevel5; case
			 * eCompressionAlgorithmGzipLevel5.value: return
			 * eCompressionAlgorithmGzipLevel6; case eCompressionAlgorithmGzipLevel7.value:
			 * return eCompressionAlgorithmGzipLevel7; case
			 * eCompressionAlgorithmGzipLevel8.value: return
			 * eCompressionAlgorithmGzipLevel8; case eCompressionAlgorithmGzipLevel9.value:
			 * return eCompressionAlgorithmGzipLevel9; case
			 * eCompressionAlgorithmShuffleGzipLevel1.value: return
			 * eCompressionAlgorithmShuffleGzipLevel1; case
			 * eCompressionAlgorithmShuffleGzipLevel2.value: return
			 * eCompressionAlgorithmShuffleGzipLevel2; case
			 * eCompressionAlgorithmShuffleGzipLevel3.value: return
			 * eCompressionAlgorithmShuffleGzipLevel3; case
			 * eCompressionAlgorithmShuffleGzipLevel4.value: return
			 * eCompressionAlgorithmShuffleGzipLevel4; case
			 * eCompressionAlgorithmShuffleGzipLevel5.value: return
			 * eCompressionAlgorithmShuffleGzipLevel5; case
			 * eCompressionAlgorithmShuffleGzipLevel6.value: return
			 * eCompressionAlgorithmShuffleGzipLevel6; case
			 * eCompressionAlgorithmShuffleGzipLevel7.value: return
			 * eCompressionAlgorithmShuffleGzipLevel7; case
			 * eCompressionAlgorithmShuffleGzipLevel8.value: return
			 * eCompressionAlgorithmShuffleGzipLevel8; case
			 * eCompressionAlgorithmShuffleGzipLevel9.value: return
			 * eCompressionAlgorithmShuffleGzipLevel9; case eCompressionAlgorithmLZ4.value:
			 * return eCompressionAlgorithmLZ4; case eCompressionAlgorithmLShuffleLZ4.value:
			 * return eCompressionAlgorithmLShuffleLZ4; default: return
			 * eCompressionAlgorithmNone; }
			 */
		}

		@Override
		public Object fromNative(Object nativeValue, FromNativeContext context) {
			// TODO Auto-generated method stub
			return fromInt((int)nativeValue);
		}

		@Override
		public Class<?> nativeType() {
			// TODO Auto-generated method stub
			return int.class;
		}

		@Override
		public Object toNative(Object value, ToNativeContext context) {
			// TODO Auto-generated method stub
			if(value == null)
				return eCompressionAlgorithmNone;
			else
				return ((TCompressionAlgorithmType)value).value;
		}
		
	}


}
