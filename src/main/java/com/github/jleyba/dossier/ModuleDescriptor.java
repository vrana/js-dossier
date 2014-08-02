package com.github.jleyba.dossier;

import com.google.common.collect.Iterables;
import com.google.javascript.jscomp.DossierModule;
import com.google.javascript.jscomp.Scope;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * Descriptor for a CommonJS module.
 */
class ModuleDescriptor {

  private final Descriptor descriptor;
  private final DossierModule module;

  /**
   * Descriptors for this module's exported API.
   */
  private final Map<String, Descriptor> exportedProperties = new HashMap<>();

  /**
   * Tracks this module's exported API by type (name) reference.
   */
  private final Map<String, Descriptor> exportsByTypeName = new HashMap<>();

  /**
   * Type definitions defined within the module but not on an exported property.
   */
  private final List<Descriptor> internalTypeDefs = new LinkedList<>();

  private final Map<String, Object> attributes = new HashMap<>();

  /**
   * Cretes a new module descriptor.
   *
   * @param descriptor the descriptor for the module's exported API.
   * @param module a reference to the module itelf.
   */
  ModuleDescriptor(Descriptor descriptor, DossierModule module) {
    this.descriptor = descriptor;
    this.module = module;
    this.descriptor.setModule(this);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ModuleDescriptor) {
      ModuleDescriptor that = (ModuleDescriptor) o;
      return this.descriptor.equals(that.descriptor)
          && this.module.equals(that.module);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(descriptor, module);
  }

  public String getName() {
    return module.getVarName();
  }

  public Iterable<Scope.Var> getInternalVars() {
    return module.getInternalVars();
  }

  public Descriptor getDescriptor() {
    return descriptor;
  }

  public Path getPath() {
    return module.getModulePath();
  }

  public String getSource() {
    return module.getScriptNode().getSourceFileName();
  }

  @Nullable
  public JsDoc getJsDoc() {
    return module.getScriptNode().getJSDocInfo() == null ? null
        : new JsDoc(module.getScriptNode().getJSDocInfo());
  }

  public void addExportedProperty(Descriptor descriptor) {
    descriptor.setModule(this);
    exportedProperties.put(descriptor.getFullName(), descriptor);
    JSType type = descriptor.getType();
    if (type != null && !type.isUnknownType()) {
      if (type.isConstructor()) {
        type = ((FunctionType) type).getTypeOfThis();
      }
      exportsByTypeName.put(type.toString(), descriptor);
    }
  }

  Iterable<Descriptor> getExportedProperties() {
    return Iterables.unmodifiableIterable(exportedProperties.values());
  }

  boolean exportsProperty(String name) {
    return getExportedProperty(name) != null;
  }

  @Nullable
  Descriptor getExportedProperty(String name) {
    for (Descriptor descriptor : Iterables.concat(exportedProperties.values(), internalTypeDefs)) {
      if (descriptor.getFullName().equals(name)) {
        return descriptor;
      }
    }
    return null;
  }

  /**
   * Finds the exported descriptor for the given variable. This method will return null if the
   * variable was not declared in this module, or the variable's type cannot be found in this
   * module's exported API. If the variable's type is exported by multiple properties, no
   * guarantees are made for which property descriptor will be returned (or even if the return
   * value will be stable).
   */
  @Nullable
  Descriptor findExportedVar(Scope.Var var) {
    Descriptor descriptor = null;
    if (Iterables.contains(getInternalVars(), var)) {
      String typeName = var.getNameNode().getString();
      descriptor = exportsByTypeName.get(typeName);
    }
    return descriptor;
  }

  void addTypedef(Descriptor descriptor) {
    descriptor.setModule(this);
    internalTypeDefs.add(descriptor);
  }

  Iterable<Descriptor> getInternalTypeDefs() {
    return Iterables.unmodifiableIterable(internalTypeDefs);
  }

  public void setAttribute(String key, Object value) {
    attributes.put(key, value);
  }

  @SuppressWarnings("unchecked")
  public <T> T getAttribute(String key) {
    return (T) attributes.get(key);
  }
}