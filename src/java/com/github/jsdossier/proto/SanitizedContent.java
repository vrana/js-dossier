// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: dossier.proto

package com.github.jsdossier.proto;

/**
 * Protobuf type {@code dossier.SanitizedContent}
 */
public  final class SanitizedContent extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:dossier.SanitizedContent)
    SanitizedContentOrBuilder {
  // Use SanitizedContent.newBuilder() to construct.
  private SanitizedContent(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
  }
  private SanitizedContent() {
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private SanitizedContent(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 8: {
            kindCase_ = 1;
            kind_ = input.readBool();
            break;
          }
          case 16: {
            kindCase_ = 2;
            kind_ = input.readBool();
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw new RuntimeException(e.setUnfinishedMessage(this));
    } catch (java.io.IOException e) {
      throw new RuntimeException(
          new com.google.protobuf.InvalidProtocolBufferException(
              e.getMessage()).setUnfinishedMessage(this));
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.github.jsdossier.proto.Dossier.internal_static_dossier_SanitizedContent_descriptor;
  }

  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.github.jsdossier.proto.Dossier.internal_static_dossier_SanitizedContent_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.github.jsdossier.proto.SanitizedContent.class, com.github.jsdossier.proto.SanitizedContent.Builder.class);
  }

  private int kindCase_ = 0;
  private java.lang.Object kind_;
  public enum KindCase
      implements com.google.protobuf.Internal.EnumLite {
    URI(1),
    HTML(2),
    KIND_NOT_SET(0);
    private int value = 0;
    private KindCase(int value) {
      this.value = value;
    }
    public static KindCase valueOf(int value) {
      switch (value) {
        case 1: return URI;
        case 2: return HTML;
        case 0: return KIND_NOT_SET;
        default: throw new java.lang.IllegalArgumentException(
          "Value is undefined for this oneof enum.");
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public KindCase
  getKindCase() {
    return KindCase.valueOf(
        kindCase_);
  }

  public static final int URI_FIELD_NUMBER = 1;
  /**
   * <code>optional bool uri = 1;</code>
   *
   * <pre>
   * Indicates a field should be treated as a sanitized URI when
   * injected into a Soy template.
   * </pre>
   */
  public boolean getUri() {
    if (kindCase_ == 1) {
      return (java.lang.Boolean) kind_;
    }
    return false;
  }

  public static final int HTML_FIELD_NUMBER = 2;
  /**
   * <code>optional bool html = 2;</code>
   *
   * <pre>
   * Indicates a field should be treated as sanitized HTML content when
   * injected into a Soy template.
   * </pre>
   */
  public boolean getHtml() {
    if (kindCase_ == 2) {
      return (java.lang.Boolean) kind_;
    }
    return false;
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (kindCase_ == 1) {
      output.writeBool(
          1, (boolean)((java.lang.Boolean) kind_));
    }
    if (kindCase_ == 2) {
      output.writeBool(
          2, (boolean)((java.lang.Boolean) kind_));
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (kindCase_ == 1) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(
            1, (boolean)((java.lang.Boolean) kind_));
    }
    if (kindCase_ == 2) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(
            2, (boolean)((java.lang.Boolean) kind_));
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  public static com.github.jsdossier.proto.SanitizedContent parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.github.jsdossier.proto.SanitizedContent parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.github.jsdossier.proto.SanitizedContent parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.github.jsdossier.proto.SanitizedContent parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.github.jsdossier.proto.SanitizedContent parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.github.jsdossier.proto.SanitizedContent parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static com.github.jsdossier.proto.SanitizedContent parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static com.github.jsdossier.proto.SanitizedContent parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static com.github.jsdossier.proto.SanitizedContent parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.github.jsdossier.proto.SanitizedContent parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.github.jsdossier.proto.SanitizedContent prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessage.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code dossier.SanitizedContent}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:dossier.SanitizedContent)
      com.github.jsdossier.proto.SanitizedContentOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.github.jsdossier.proto.Dossier.internal_static_dossier_SanitizedContent_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.github.jsdossier.proto.Dossier.internal_static_dossier_SanitizedContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.github.jsdossier.proto.SanitizedContent.class, com.github.jsdossier.proto.SanitizedContent.Builder.class);
    }

    // Construct using com.github.jsdossier.proto.SanitizedContent.newBuilder()
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
    public Builder clear() {
      super.clear();
      kindCase_ = 0;
      kind_ = null;
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.github.jsdossier.proto.Dossier.internal_static_dossier_SanitizedContent_descriptor;
    }

    public com.github.jsdossier.proto.SanitizedContent getDefaultInstanceForType() {
      return com.github.jsdossier.proto.SanitizedContent.getDefaultInstance();
    }

    public com.github.jsdossier.proto.SanitizedContent build() {
      com.github.jsdossier.proto.SanitizedContent result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.github.jsdossier.proto.SanitizedContent buildPartial() {
      com.github.jsdossier.proto.SanitizedContent result = new com.github.jsdossier.proto.SanitizedContent(this);
      if (kindCase_ == 1) {
        result.kind_ = kind_;
      }
      if (kindCase_ == 2) {
        result.kind_ = kind_;
      }
      result.kindCase_ = kindCase_;
      onBuilt();
      return result;
    }

    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.github.jsdossier.proto.SanitizedContent) {
        return mergeFrom((com.github.jsdossier.proto.SanitizedContent)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.github.jsdossier.proto.SanitizedContent other) {
      if (other == com.github.jsdossier.proto.SanitizedContent.getDefaultInstance()) return this;
      switch (other.getKindCase()) {
        case URI: {
          setUri(other.getUri());
          break;
        }
        case HTML: {
          setHtml(other.getHtml());
          break;
        }
        case KIND_NOT_SET: {
          break;
        }
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.github.jsdossier.proto.SanitizedContent parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.github.jsdossier.proto.SanitizedContent) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int kindCase_ = 0;
    private java.lang.Object kind_;
    public KindCase
        getKindCase() {
      return KindCase.valueOf(
          kindCase_);
    }

    public Builder clearKind() {
      kindCase_ = 0;
      kind_ = null;
      onChanged();
      return this;
    }


    /**
     * <code>optional bool uri = 1;</code>
     *
     * <pre>
     * Indicates a field should be treated as a sanitized URI when
     * injected into a Soy template.
     * </pre>
     */
    public boolean getUri() {
      if (kindCase_ == 1) {
        return (java.lang.Boolean) kind_;
      }
      return false;
    }
    /**
     * <code>optional bool uri = 1;</code>
     *
     * <pre>
     * Indicates a field should be treated as a sanitized URI when
     * injected into a Soy template.
     * </pre>
     */
    public Builder setUri(boolean value) {
      kindCase_ = 1;
      kind_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional bool uri = 1;</code>
     *
     * <pre>
     * Indicates a field should be treated as a sanitized URI when
     * injected into a Soy template.
     * </pre>
     */
    public Builder clearUri() {
      if (kindCase_ == 1) {
        kindCase_ = 0;
        kind_ = null;
        onChanged();
      }
      return this;
    }

    /**
     * <code>optional bool html = 2;</code>
     *
     * <pre>
     * Indicates a field should be treated as sanitized HTML content when
     * injected into a Soy template.
     * </pre>
     */
    public boolean getHtml() {
      if (kindCase_ == 2) {
        return (java.lang.Boolean) kind_;
      }
      return false;
    }
    /**
     * <code>optional bool html = 2;</code>
     *
     * <pre>
     * Indicates a field should be treated as sanitized HTML content when
     * injected into a Soy template.
     * </pre>
     */
    public Builder setHtml(boolean value) {
      kindCase_ = 2;
      kind_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional bool html = 2;</code>
     *
     * <pre>
     * Indicates a field should be treated as sanitized HTML content when
     * injected into a Soy template.
     * </pre>
     */
    public Builder clearHtml() {
      if (kindCase_ == 2) {
        kindCase_ = 0;
        kind_ = null;
        onChanged();
      }
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:dossier.SanitizedContent)
  }

  // @@protoc_insertion_point(class_scope:dossier.SanitizedContent)
  private static final com.github.jsdossier.proto.SanitizedContent DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.github.jsdossier.proto.SanitizedContent();
  }

  public static com.github.jsdossier.proto.SanitizedContent getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SanitizedContent>
      PARSER = new com.google.protobuf.AbstractParser<SanitizedContent>() {
    public SanitizedContent parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      try {
        return new SanitizedContent(input, extensionRegistry);
      } catch (RuntimeException e) {
        if (e.getCause() instanceof
            com.google.protobuf.InvalidProtocolBufferException) {
          throw (com.google.protobuf.InvalidProtocolBufferException)
              e.getCause();
        }
        throw e;
      }
    }
  };

  public static com.google.protobuf.Parser<SanitizedContent> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SanitizedContent> getParserForType() {
    return PARSER;
  }

  public com.github.jsdossier.proto.SanitizedContent getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

