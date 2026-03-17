/*
 * Copyright (C) 2023 JHotDraw.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jhotdraw.draw.figure;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;

/** implementation of Attribute storage and processing. */
public final class Attributes {

  private final HashMap<AttributeKey<?>, Object> attributes = new HashMap<>();

  /**
   * Forbidden attributes can't be put by the put() operation. They can only be changed by put().
   */
  private HashSet<AttributeKey<?>> forbiddenAttributes;

  private AttributeListener listener;

  private Supplier<List<Attributes>> DEPENDENT;
  /*
   * Creates a new instance of Attributes with no listener and no dependent supplier.
   */
  public Attributes() {
    this(null, null);
  }

  /*
   * Creates a new instance of Attributes with the specified listener.
   * @param listener The attribute listener.
   */
  public Attributes(AttributeListener listener) {
    this(listener, null);
  }

  /*
   * Creates a new instance of Attributes with the specified listener and dependent supplier.
   * @param listener The attribute listener.
   * @param dependent The supplier for dependent attributes.
   */
  public Attributes(AttributeListener listener, Supplier<List<Attributes>> dependent) {
    this.listener = listener;
    this.DEPENDENT = dependent == null ? () -> Collections.emptyList() : dependent;
  }
  /*
   * Sets the supplier for dependent attributes. Dependent attributes are attributes of other figures that should be changed when an attribute of this figure is changed. For example, if a figure has a dependent attribute which is the attribute of its parent figure, then when an attribute of this figure is changed, the corresponding attribute of the parent figure should also be changed.
   * @param dependent The supplier for dependent attributes. If null, no dependent attributes will be used.
   */
  public void dependents(Supplier<List<Attributes>> dependent) {
    this.DEPENDENT = dependent == null ? () -> Collections.emptyList() : dependent;
  }

  /*
   * Enables or disables an attribute for this figure to be processed. If an attribute is disabled, it can't be set on this figure and won't be included in the attributes of this figure.
   * @param key The attribute key to enable or disable.
   * @param b true to enable the attribute, false to disable it.
   */
  public void setAttributeEnabled(AttributeKey<?> key, boolean b) {
    if (forbiddenAttributes == null) {
      forbiddenAttributes = new HashSet<>();
    }
    if (b) {
      forbiddenAttributes.remove(key);
    } else {
      forbiddenAttributes.add(key);
    }
  }

  /**
   * Is this attribute enabled for this figure to be processed.
   *
   * @param key
   * @return
   */
  public boolean isAttributeEnabled(AttributeKey<?> key) {
    return forbiddenAttributes == null || !forbiddenAttributes.contains(key);
  }

  /**
   * Copy attribute values from a map of attribute values. This detects also the use of
   * UserData sub maps and copy only those values and creates a new instance of UserData itself.
   * @param map
   */
  public void setAttributes(Map<AttributeKey<?>, Object> map) {
    for (Map.Entry<AttributeKey<?>, Object> entry : map.entrySet()) {
      if (entry.getValue() instanceof AttributeKeys.UserData ud) {
        getAndInclude(AttributeKeys.USER_DATA).data().putAll(ud.data());
      } else {
        set((AttributeKey<Object>) entry.getKey(), entry.getValue());
      }
    }
  }

  public Map<AttributeKey<?>, Object> getAttributes() {
    return Collections.unmodifiableMap(attributes);
  }

  /**
   * Gets data which can be used to restore the attributes of the figure after a set has been
   * applied to it.
   */
  public Object getAttributesRestoreData() {
    List<Attributes> dependent = DEPENDENT.get();
    if (dependent.isEmpty()) {
      return new HashMap<>(attributes);
    } else {
      List<Map<AttributeKey<?>, Object>> list = new ArrayList<>();
      list.add(new HashMap<>(attributes));
      for (Attributes attr : dependent) {
        list.add(new HashMap<>(attr.getAttributes()));
      }
      return list;
    }
  }

  /** Restores the attributes of the figure to a previously stored state. */
  public void restoreAttributesTo(Object restoreData) {
    if (restoreData instanceof List) {
      List<Map<AttributeKey<?>, Object>> list = (List<Map<AttributeKey<?>, Object>>) restoreData;
      restoreAttributesTo(list.get(0));
      int idx = 1;
      for (Attributes attr : DEPENDENT.get()) {
        attr.restoreAttributesTo(list.get(idx));
        idx++;
      }
    } else {
      attributes.clear();
      Map<AttributeKey<?>, Object> restoreDataHashMap = (Map<AttributeKey<?>, Object>) restoreData;
      setAttributes(restoreDataHashMap);
    }
  }

  /**
   * Sets an attribute on the figure and calls {@code attributeChanged} on all registered {@code
   * FigureListener}s if the attribute value has changed.
   *
   * <p>For efficiency reasons, the drawing is not automatically repainted. If you want the drawing
   * to be repainted when the attribute is changed, you can either use {@code key.set(figure,
   * value); } or
   *
   * <pre>
   * figure.willChange();
   * figure.set(...);
   * figure.changed();
   * </pre>
   *
   * @see AttributeKey#set
   */
  public <T> Attributes set(final AttributeKey<T> key, final T newValue) {
    if (forbiddenAttributes == null || !forbiddenAttributes.contains(key)) {
      T oldValue = key.put(attributes, newValue);
      fireAttributeChanged(key, oldValue, newValue);
    }

    DEPENDENT.get().forEach(a -> Optional.ofNullable(a).ifPresent(at -> at.set(key, newValue)));
    return this;
  }

  /**
   * Gets an attribute from the Figure.
   *
   * @see AttributeKey#get
   * @return Returns the attribute value. If the Figure does not have an attribute with the
   *     specified key, returns key.getDefaultValue().
   */
  public <T> T get(AttributeKey<T> key) {
    return key.get(attributes);
  }

  /**
   * Gets an attribute from the Figure. Includes this attribute, if it was not there.
   *
   * @see AttributeKey#get
   * @return Returns the attribute value. If the Figure does not have an attribute with the
   *     specified key, returns key.getDefaultValue().
   */
  public <T> T getAndInclude(AttributeKey<T> key) {
    return key.getAndInclude(attributes);
  }

  public static AttributeKey<?> getAttributeKey(String name) {
    return AttributeKeys.SUPPORTED_ATTRIBUTES_MAP.get(name);
  }

  /*
   * Removes an attribute from the figure and calls {@code attributeChanged} on all registered {@code FigureListener}s if the attribute value has changed.
   * For efficiency reasons, the drawing is not automatically repainted. If you want the drawing to be repainted when the attribute is changed, you can either use {@code key.remove(figure); } or
   * <pre>
   * figure.willChange();
   * figure.remove(...);
   * figure.changed();
   * </pre>
   * @see AttributeKey#remove
   **
  * @param key The attribute key to remove.
  * @param <T> The type of the attribute value.
  * @return Returns the old value of the attribute that was removed, or {@code null} if the
    attribute
  *
  was not set before.
  * @see AttributeKey#remove
  */
  public <T> void removeAttribute(AttributeKey<T> key) {
    if (hasAttribute(key)) {
      T oldValue = get(key);
      attributes.remove(key);
      fireAttributeChanged(key, oldValue, key.getDefaultValue());
    }
  }

  /**
   * Is this attribute set within this container.
   *
   * @param key
   * @return
   */
  public boolean hasAttribute(AttributeKey<?> key) {
    return attributes.containsKey(key);
  }

  /**
   * Fires an attribute changed event to all registered {@code FigureListener}s.
   *
   * @param attribute The attribute that has changed.
   * @param oldValue The old value of the attribute.
   * @param newValue The new value of the attribute.
   * <T> The type of the attribute value.
   * @see AttributeListener#attributeChanged
   * @see AttributeKey#remove
   */
  private <T> void fireAttributeChanged(AttributeKey<T> attribute, T oldValue, T newValue) {
    if (listener != null) {
      listener.attributeChanged(attribute, oldValue, newValue);
    }
  }

  /* The AttributeListener interface defines a listener for attribute changes. It has a single method, {@code attributeChanged}, which is called when an attribute of the figure is changed. The method receives the attribute that has changed, the old value of the attribute, and the new value of the attribute. This allows the listener to react to changes in the attributes of the figure, for example by updating the appearance of the figure or by triggering other actions.
   * @see AttributeKey#put
   * @see AttributeKey#remove
   */
  @FunctionalInterface
  public static interface AttributeListener {
    <T> void attributeChanged(AttributeKey<T> attribute, T oldValue, T newValue);
  }
  /* Creates a new instance of Attributes by copying the attributes from the source Attributes. The listener and dependent supplier are set to null.
   * @param source The source Attributes to copy from.
   * @return A new instance of Attributes with the copied attributes and no listener or dependent supplier.
   */

  public static Attributes from(Attributes source) {
    return from(source, null, null);
  }

  /* Creates a new instance of Attributes by copying the attributes from the source Attributes and using the specified listener. The dependent supplier is set to null.
   * @param source The source Attributes to copy from.
   * @param listener The attribute listener for the new Attributes. If null, no listener will be used.
   * @return A new instance of Attributes with the copied attributes and specified listener.
   */

  public static Attributes from(Attributes source, AttributeListener listener) {
    return from(source, listener, null);
  }

  /* Creates a new instance of Attributes by copying the attributes from the source Attributes and using the specified listener and dependent supplier.
   * @param source The source Attributes to copy from.
   * @param listener The attribute listener for the new Attributes. If null, no listener will be used.
   * @param dependent The supplier for dependent attributes for the new Attributes. If null, no dependent attributes will be used.
   * @return A new instance of Attributes with the copied attributes and specified listener and dependent supplier.
   */
  public static Attributes from(
      Attributes source, AttributeListener listener, Supplier<List<Attributes>> dependent) {
    Attributes attr = new Attributes(listener, dependent);
    attr.attributes.putAll(source.attributes);
    if (source.forbiddenAttributes != null) {
      attr.forbiddenAttributes = new HashSet<>(source.forbiddenAttributes);
    }
    return attr;
  }

  /* Creates a supplier for the attributes of a list of figures. The supplier returns the list of attributes of the figures in the list. If a figure in the list is null, it is ignored.
   * @param dependent The supplier for the list of figures. If null, an empty list will be used.
   * @return A supplier for the attributes of the figures in the list.
   */
  public static Supplier<List<Attributes>> attrSupplier(Supplier<List<Figure>> dependent) {
    return () ->
        dependent.get().stream().filter(f -> f != null).map(f -> f.attr()).collect(toList());
  }
}
