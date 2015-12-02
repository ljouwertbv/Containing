// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: SimItemProto.proto

package nhl.containing.networking.protobuf;

public final class SimulationItemProto {
  private SimulationItemProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface SimulationItemOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required string id = 1;
    /**
     * <code>required string id = 1;</code>
     */
    boolean hasId();
    /**
     * <code>required string id = 1;</code>
     */
    java.lang.String getId();
    /**
     * <code>required string id = 1;</code>
     */
    com.google.protobuf.ByteString
        getIdBytes();

    // required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;
    /**
     * <code>required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;</code>
     */
    boolean hasType();
    /**
     * <code>required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;</code>
     */
    nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType getType();
  }
  /**
   * Protobuf type {@code nhl.containing.networking.protobuf.SimulationItem}
   */
  public static final class SimulationItem extends
      com.google.protobuf.GeneratedMessage
      implements SimulationItemOrBuilder {
    // Use SimulationItem.newBuilder() to construct.
    private SimulationItem(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private SimulationItem(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final SimulationItem defaultInstance;
    public static SimulationItem getDefaultInstance() {
      return defaultInstance;
    }

    public SimulationItem getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private SimulationItem(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              bitField0_ |= 0x00000001;
              id_ = input.readBytes();
              break;
            }
            case 16: {
              int rawValue = input.readEnum();
              nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType value = nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(2, rawValue);
              } else {
                bitField0_ |= 0x00000002;
                type_ = value;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return nhl.containing.networking.protobuf.SimulationItemProto.internal_static_nhl_containing_networking_protobuf_SimulationItem_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return nhl.containing.networking.protobuf.SimulationItemProto.internal_static_nhl_containing_networking_protobuf_SimulationItem_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.class, nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.Builder.class);
    }

    public static com.google.protobuf.Parser<SimulationItem> PARSER =
        new com.google.protobuf.AbstractParser<SimulationItem>() {
      public SimulationItem parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SimulationItem(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<SimulationItem> getParserForType() {
      return PARSER;
    }

    /**
     * Protobuf enum {@code nhl.containing.networking.protobuf.SimulationItem.SimulationItemType}
     */
    public enum SimulationItemType
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>CRANE = 0;</code>
       */
      CRANE(0, 0),
      /**
       * <code>PLATFORM = 1;</code>
       */
      PLATFORM(1, 1),
      ;

      /**
       * <code>CRANE = 0;</code>
       */
      public static final int CRANE_VALUE = 0;
      /**
       * <code>PLATFORM = 1;</code>
       */
      public static final int PLATFORM_VALUE = 1;


      public final int getNumber() { return value; }

      public static SimulationItemType valueOf(int value) {
        switch (value) {
          case 0: return CRANE;
          case 1: return PLATFORM;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<SimulationItemType>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static com.google.protobuf.Internal.EnumLiteMap<SimulationItemType>
          internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<SimulationItemType>() {
              public SimulationItemType findValueByNumber(int number) {
                return SimulationItemType.valueOf(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(index);
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.getDescriptor().getEnumTypes().get(0);
      }

      private static final SimulationItemType[] VALUES = values();

      public static SimulationItemType valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int index;
      private final int value;

      private SimulationItemType(int index, int value) {
        this.index = index;
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:nhl.containing.networking.protobuf.SimulationItem.SimulationItemType)
    }

    private int bitField0_;
    // required string id = 1;
    public static final int ID_FIELD_NUMBER = 1;
    private java.lang.Object id_;
    /**
     * <code>required string id = 1;</code>
     */
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string id = 1;</code>
     */
    public java.lang.String getId() {
      java.lang.Object ref = id_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          id_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string id = 1;</code>
     */
    public com.google.protobuf.ByteString
        getIdBytes() {
      java.lang.Object ref = id_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        id_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    // required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;
    public static final int TYPE_FIELD_NUMBER = 2;
    private nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType type_;
    /**
     * <code>required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;</code>
     */
    public boolean hasType() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;</code>
     */
    public nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType getType() {
      return type_;
    }

    private void initFields() {
      id_ = "";
      type_ = nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType.CRANE;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasType()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getIdBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeEnum(2, type_.getNumber());
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getIdBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(2, type_.getNumber());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code nhl.containing.networking.protobuf.SimulationItem}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements nhl.containing.networking.protobuf.SimulationItemProto.SimulationItemOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return nhl.containing.networking.protobuf.SimulationItemProto.internal_static_nhl_containing_networking_protobuf_SimulationItem_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return nhl.containing.networking.protobuf.SimulationItemProto.internal_static_nhl_containing_networking_protobuf_SimulationItem_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.class, nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.Builder.class);
      }

      // Construct using nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        id_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        type_ = nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType.CRANE;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return nhl.containing.networking.protobuf.SimulationItemProto.internal_static_nhl_containing_networking_protobuf_SimulationItem_descriptor;
      }

      public nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem getDefaultInstanceForType() {
        return nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.getDefaultInstance();
      }

      public nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem build() {
        nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem buildPartial() {
        nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem result = new nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.type_ = type_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem) {
          return mergeFrom((nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem other) {
        if (other == nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.getDefaultInstance()) return this;
        if (other.hasId()) {
          bitField0_ |= 0x00000001;
          id_ = other.id_;
          onChanged();
        }
        if (other.hasType()) {
          setType(other.getType());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasId()) {
          
          return false;
        }
        if (!hasType()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required string id = 1;
      private java.lang.Object id_ = "";
      /**
       * <code>required string id = 1;</code>
       */
      public boolean hasId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string id = 1;</code>
       */
      public java.lang.String getId() {
        java.lang.Object ref = id_;
        if (!(ref instanceof java.lang.String)) {
          java.lang.String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          id_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string id = 1;</code>
       */
      public com.google.protobuf.ByteString
          getIdBytes() {
        java.lang.Object ref = id_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          id_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string id = 1;</code>
       */
      public Builder setId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string id = 1;</code>
       */
      public Builder clearId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        id_ = getDefaultInstance().getId();
        onChanged();
        return this;
      }
      /**
       * <code>required string id = 1;</code>
       */
      public Builder setIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }

      // required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;
      private nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType type_ = nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType.CRANE;
      /**
       * <code>required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;</code>
       */
      public boolean hasType() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;</code>
       */
      public nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType getType() {
        return type_;
      }
      /**
       * <code>required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;</code>
       */
      public Builder setType(nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000002;
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required .nhl.containing.networking.protobuf.SimulationItem.SimulationItemType type = 2;</code>
       */
      public Builder clearType() {
        bitField0_ = (bitField0_ & ~0x00000002);
        type_ = nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType.CRANE;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:nhl.containing.networking.protobuf.SimulationItem)
    }

    static {
      defaultInstance = new SimulationItem(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:nhl.containing.networking.protobuf.SimulationItem)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_nhl_containing_networking_protobuf_SimulationItem_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_nhl_containing_networking_protobuf_SimulationItem_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\022SimItemProto.proto\022\"nhl.containing.net" +
      "working.protobuf\"\240\001\n\016SimulationItem\022\n\n\002i" +
      "d\030\001 \002(\t\022S\n\004type\030\002 \002(\0162E.nhl.containing.n" +
      "etworking.protobuf.SimulationItem.Simula" +
      "tionItemType\"-\n\022SimulationItemType\022\t\n\005CR" +
      "ANE\020\000\022\014\n\010PLATFORM\020\001B\025B\023SimulationItemPro" +
      "to"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_nhl_containing_networking_protobuf_SimulationItem_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_nhl_containing_networking_protobuf_SimulationItem_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_nhl_containing_networking_protobuf_SimulationItem_descriptor,
              new java.lang.String[] { "Id", "Type", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
