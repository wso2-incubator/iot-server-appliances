#
# Autogenerated by Thrift Compiler (0.9.1)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#
#  options string: py
#

from thrift.Thrift import TType, TMessageType, TException, TApplicationException

from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol, TProtocol
try:
  from thrift.protocol import fastbinary
except:
  fastbinary = None


class ThriftAttributeType:
  INT = 0
  LONG = 1
  FLOAT = 2
  DOUBLE = 3
  BOOL = 4
  STRING = 5

  _VALUES_TO_NAMES = {
    0: "INT",
    1: "LONG",
    2: "FLOAT",
    3: "DOUBLE",
    4: "BOOL",
    5: "STRING",
  }

  _NAMES_TO_VALUES = {
    "INT": 0,
    "LONG": 1,
    "FLOAT": 2,
    "DOUBLE": 3,
    "BOOL": 4,
    "STRING": 5,
  }


class ThriftAttribute:
  """
  Attributes:
   - name
   - attributeType
  """

  thrift_spec = (
    None, # 0
    (1, TType.STRING, 'name', None, None, ), # 1
    (2, TType.I32, 'attributeType', None, None, ), # 2
  )

  def __init__(self, name=None, attributeType=None,):
    self.name = name
    self.attributeType = attributeType

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRING:
          self.name = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.I32:
          self.attributeType = iprot.readI32();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('ThriftAttribute')
    if self.name is not None:
      oprot.writeFieldBegin('name', TType.STRING, 1)
      oprot.writeString(self.name)
      oprot.writeFieldEnd()
    if self.attributeType is not None:
      oprot.writeFieldBegin('attributeType', TType.I32, 2)
      oprot.writeI32(self.attributeType)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def validate(self):
    return


  def __repr__(self):
    L = ['%s=%r' % (key, value)
      for key, value in self.__dict__.iteritems()]
    return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class ThriftEventBundle:
  """
  Attributes:
   - sessionId
   - eventNum
   - intAttributeList
   - longAttributeList
   - doubleAttributeList
   - boolAttributeList
   - stringAttributeList
   - arbitraryDataMapMap
  """

  thrift_spec = (
    None, # 0
    (1, TType.STRING, 'sessionId', None, None, ), # 1
    (2, TType.I32, 'eventNum', None, None, ), # 2
    (3, TType.LIST, 'intAttributeList', (TType.I32,None), None, ), # 3
    (4, TType.LIST, 'longAttributeList', (TType.I64,None), None, ), # 4
    (5, TType.LIST, 'doubleAttributeList', (TType.DOUBLE,None), None, ), # 5
    (6, TType.LIST, 'boolAttributeList', (TType.BOOL,None), None, ), # 6
    (7, TType.LIST, 'stringAttributeList', (TType.STRING,None), None, ), # 7
    (8, TType.MAP, 'arbitraryDataMapMap', (TType.I32,None,TType.MAP,(TType.STRING,None,TType.STRING,None)), None, ), # 8
  )

  def __init__(self, sessionId=None, eventNum=None, intAttributeList=None, longAttributeList=None, doubleAttributeList=None, boolAttributeList=None, stringAttributeList=None, arbitraryDataMapMap=None,):
    self.sessionId = sessionId
    self.eventNum = eventNum
    self.intAttributeList = intAttributeList
    self.longAttributeList = longAttributeList
    self.doubleAttributeList = doubleAttributeList
    self.boolAttributeList = boolAttributeList
    self.stringAttributeList = stringAttributeList
    self.arbitraryDataMapMap = arbitraryDataMapMap

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRING:
          self.sessionId = iprot.readString();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.I32:
          self.eventNum = iprot.readI32();
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.LIST:
          self.intAttributeList = []
          (_etype3, _size0) = iprot.readListBegin()
          for _i4 in xrange(_size0):
            _elem5 = iprot.readI32();
            self.intAttributeList.append(_elem5)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 4:
        if ftype == TType.LIST:
          self.longAttributeList = []
          (_etype9, _size6) = iprot.readListBegin()
          for _i10 in xrange(_size6):
            _elem11 = iprot.readI64();
            self.longAttributeList.append(_elem11)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 5:
        if ftype == TType.LIST:
          self.doubleAttributeList = []
          (_etype15, _size12) = iprot.readListBegin()
          for _i16 in xrange(_size12):
            _elem17 = iprot.readDouble();
            self.doubleAttributeList.append(_elem17)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 6:
        if ftype == TType.LIST:
          self.boolAttributeList = []
          (_etype21, _size18) = iprot.readListBegin()
          for _i22 in xrange(_size18):
            _elem23 = iprot.readBool();
            self.boolAttributeList.append(_elem23)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 7:
        if ftype == TType.LIST:
          self.stringAttributeList = []
          (_etype27, _size24) = iprot.readListBegin()
          for _i28 in xrange(_size24):
            _elem29 = iprot.readString();
            self.stringAttributeList.append(_elem29)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 8:
        if ftype == TType.MAP:
          self.arbitraryDataMapMap = {}
          (_ktype31, _vtype32, _size30 ) = iprot.readMapBegin()
          for _i34 in xrange(_size30):
            _key35 = iprot.readI32();
            _val36 = {}
            (_ktype38, _vtype39, _size37 ) = iprot.readMapBegin()
            for _i41 in xrange(_size37):
              _key42 = iprot.readString();
              _val43 = iprot.readString();
              _val36[_key42] = _val43
            iprot.readMapEnd()
            self.arbitraryDataMapMap[_key35] = _val36
          iprot.readMapEnd()
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('ThriftEventBundle')
    if self.sessionId is not None:
      oprot.writeFieldBegin('sessionId', TType.STRING, 1)
      oprot.writeString(self.sessionId)
      oprot.writeFieldEnd()
    if self.eventNum is not None:
      oprot.writeFieldBegin('eventNum', TType.I32, 2)
      oprot.writeI32(self.eventNum)
      oprot.writeFieldEnd()
    if self.intAttributeList is not None:
      oprot.writeFieldBegin('intAttributeList', TType.LIST, 3)
      oprot.writeListBegin(TType.I32, len(self.intAttributeList))
      for iter44 in self.intAttributeList:
        oprot.writeI32(iter44)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.longAttributeList is not None:
      oprot.writeFieldBegin('longAttributeList', TType.LIST, 4)
      oprot.writeListBegin(TType.I64, len(self.longAttributeList))
      for iter45 in self.longAttributeList:
        oprot.writeI64(iter45)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.doubleAttributeList is not None:
      oprot.writeFieldBegin('doubleAttributeList', TType.LIST, 5)
      oprot.writeListBegin(TType.DOUBLE, len(self.doubleAttributeList))
      for iter46 in self.doubleAttributeList:
        oprot.writeDouble(iter46)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.boolAttributeList is not None:
      oprot.writeFieldBegin('boolAttributeList', TType.LIST, 6)
      oprot.writeListBegin(TType.BOOL, len(self.boolAttributeList))
      for iter47 in self.boolAttributeList:
        oprot.writeBool(iter47)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.stringAttributeList is not None:
      oprot.writeFieldBegin('stringAttributeList', TType.LIST, 7)
      oprot.writeListBegin(TType.STRING, len(self.stringAttributeList))
      for iter48 in self.stringAttributeList:
        oprot.writeString(iter48)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.arbitraryDataMapMap is not None:
      oprot.writeFieldBegin('arbitraryDataMapMap', TType.MAP, 8)
      oprot.writeMapBegin(TType.I32, TType.MAP, len(self.arbitraryDataMapMap))
      for kiter49,viter50 in self.arbitraryDataMapMap.items():
        oprot.writeI32(kiter49)
        oprot.writeMapBegin(TType.STRING, TType.STRING, len(viter50))
        for kiter51,viter52 in viter50.items():
          oprot.writeString(kiter51)
          oprot.writeString(viter52)
        oprot.writeMapEnd()
      oprot.writeMapEnd()
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def validate(self):
    return


  def __repr__(self):
    L = ['%s=%r' % (key, value)
      for key, value in self.__dict__.iteritems()]
    return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)
