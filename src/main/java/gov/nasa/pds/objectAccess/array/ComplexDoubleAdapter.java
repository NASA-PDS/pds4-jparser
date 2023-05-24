package gov.nasa.pds.objectAccess.array;

import java.nio.ByteBuffer;

public class ComplexDoubleAdapter extends DoubleAdapter implements ComplexDataTypeAdapter {
  public ComplexDoubleAdapter(boolean isBigEndian) {
    super(isBigEndian);
    // TODO Auto-generated constructor stub
  }
  @Override
  public int getInt(ByteBuffer buf) {
    return (int)this.getDouble(buf);
  }
  @Override
  public long getLong(ByteBuffer buf) {
    return (long)this.getDouble(buf);
  }
  @Override
  public double getDouble(ByteBuffer buf) {
    double imag=this.getImagDouble(buf), real=this.getRealDouble(buf);
    return Math.sqrt(imag*imag + real*real);
  }
  @Override
  public int getImagInt(ByteBuffer buf) {
    return super.getInt(buf);
  }
  @Override
  public long getImagLong(ByteBuffer buf) {
    return super.getLong(buf);
  }
  @Override
  public double getImagDouble(ByteBuffer buf) {
    return super.getDouble(buf);
  }
  @Override
  public int getRealInt(ByteBuffer buf) {
    return super.getInt(buf);
  }
  @Override
  public long getRealLong(ByteBuffer buf) {
    return super.getLong(buf);
  }
  @Override
  public double getRealDouble(ByteBuffer buf) {
    return super.getDouble(buf);
  }
}
