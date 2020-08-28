package org.hhmi.janelia.scicomp.imaris.writer;

import com.sun.jna.Structure;

/**
 * @author kittisopikulm
 *
 */
@Structure.FieldOrder({"mIsBaseColorMode","mBaseColor","mColorTable","mColorTableSize","mOpacity","mRangeMin","mRangeMax","mGammaCorrection"})
public class BPConverterTypesC_ColorInfo extends Structure {
	public static class ByReference extends BPConverterTypesC_ColorInfo implements Structure.ByReference {
		@Override
		public ByReference[] toArray(int size) {
			// TODO Auto-generated method stub
			return (ByReference[]) super.toArray(size);
		}
	}
	public boolean mIsBaseColorMode; // 
	public BPConverterTypesC_Color mBaseColor; // bpConverterTypesC_Color
	public BPConverterTypesC_Color mColorTable; // const bpConverterTypesC_Color*
	public int mColorTableSize; // unsigned int
	public float mOpacity;
	public float mRangeMin;
	public float mRangeMax;
	public float mGammaCorrection;
}
