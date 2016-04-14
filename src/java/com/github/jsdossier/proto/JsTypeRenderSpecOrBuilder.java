// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: dossier.proto

package com.github.jsdossier.proto;

public interface JsTypeRenderSpecOrBuilder extends
    // @@protoc_insertion_point(interface_extends:dossier.JsTypeRenderSpec)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .dossier.JsType type = 1;</code>
   *
   * <pre>
   * The types to generate documentation for.
   * </pre>
   */
  java.util.List<com.github.jsdossier.proto.JsType> 
      getTypeList();
  /**
   * <code>repeated .dossier.JsType type = 1;</code>
   *
   * <pre>
   * The types to generate documentation for.
   * </pre>
   */
  com.github.jsdossier.proto.JsType getType(int index);
  /**
   * <code>repeated .dossier.JsType type = 1;</code>
   *
   * <pre>
   * The types to generate documentation for.
   * </pre>
   */
  int getTypeCount();
  /**
   * <code>repeated .dossier.JsType type = 1;</code>
   *
   * <pre>
   * The types to generate documentation for.
   * </pre>
   */
  java.util.List<? extends com.github.jsdossier.proto.JsTypeOrBuilder> 
      getTypeOrBuilderList();
  /**
   * <code>repeated .dossier.JsType type = 1;</code>
   *
   * <pre>
   * The types to generate documentation for.
   * </pre>
   */
  com.github.jsdossier.proto.JsTypeOrBuilder getTypeOrBuilder(
      int index);

  /**
   * <code>optional .dossier.Resources resources = 2;</code>
   *
   * <pre>
   * The resources to include.
   * </pre>
   */
  boolean hasResources();
  /**
   * <code>optional .dossier.Resources resources = 2;</code>
   *
   * <pre>
   * The resources to include.
   * </pre>
   */
  com.github.jsdossier.proto.Resources getResources();
  /**
   * <code>optional .dossier.Resources resources = 2;</code>
   *
   * <pre>
   * The resources to include.
   * </pre>
   */
  com.github.jsdossier.proto.ResourcesOrBuilder getResourcesOrBuilder();

  /**
   * <code>optional .dossier.Index index = 3;</code>
   *
   * <pre>
   * Navigation index.
   * </pre>
   */
  boolean hasIndex();
  /**
   * <code>optional .dossier.Index index = 3;</code>
   *
   * <pre>
   * Navigation index.
   * </pre>
   */
  com.github.jsdossier.proto.Index getIndex();
  /**
   * <code>optional .dossier.Index index = 3;</code>
   *
   * <pre>
   * Navigation index.
   * </pre>
   */
  com.github.jsdossier.proto.IndexOrBuilder getIndexOrBuilder();
}
