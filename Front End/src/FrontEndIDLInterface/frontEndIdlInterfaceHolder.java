package FrontEndIDLInterface;

/**
* FrontEndIDLInterface/frontEndIdlInterfaceHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from FrontEndIDLInterface.idl
* Sunday, December 6, 2015 5:15:56 PM EST
*/

public final class frontEndIdlInterfaceHolder implements org.omg.CORBA.portable.Streamable
{
  public FrontEndIDLInterface.frontEndIdlInterface value = null;

  public frontEndIdlInterfaceHolder ()
  {
  }

  public frontEndIdlInterfaceHolder (FrontEndIDLInterface.frontEndIdlInterface initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = FrontEndIDLInterface.frontEndIdlInterfaceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    FrontEndIDLInterface.frontEndIdlInterfaceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return FrontEndIDLInterface.frontEndIdlInterfaceHelper.type ();
  }

}
