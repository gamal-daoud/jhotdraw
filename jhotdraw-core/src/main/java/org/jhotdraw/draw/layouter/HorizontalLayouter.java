/*
 * @(#)HorizontalLayouter.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.layouter;

import java.awt.geom.*;
import org.jhotdraw.draw.AttributeKeys.Alignment;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.geom.Dimension2DDouble;
import org.jhotdraw.utils.geom.Insets2D;

/**
 * A {@link Layouter} which lays out all children of a {@link
 * org.jhotdraw.draw.figure.CompositeFigure} in horizontal direction.
 *
 * <p>The preferred size of the figures is used to determine the layout. This may cause some figures
 * to resize.
 *
 * <p>The HorizontalLayouter honors the LAYOUT_INSETS and the COMPOSITE_ALIGNMENT AttributeKey when
 * laying out a CompositeFigure.
 *
 * <p>If COMPOSITE_ALIGNMENT is not set on the composite figure, the layout assigns the same height
 * to all figures.
 *
 * <p>Grande modification GM1 : maintenant sous-classe de {@link AbstractLinearLayouter}. La
 * logique commune (calculateLayout / layout) est héritée ; seule la direction horizontale est
 * définie ici.
 */
public class HorizontalLayouter extends AbstractLinearLayouter {

  // --- Axe principal : largeur (X) ---

  @Override
  protected double getPrimaryPreferredSize(Dimension2DDouble size) {
    return size.width;
  }

  @Override
  protected double getSecondaryPreferredSize(Dimension2DDouble size) {
    return size.height;
  }

  @Override
  protected double getPrimaryInsets(Insets2D.Double insets) {
    return insets.left + insets.right;
  }

  @Override
  protected double getSecondaryInsets(Insets2D.Double insets) {
    return insets.top + insets.bottom;
  }

  @Override
  protected double getPrimaryLayoutInsetsStart(Insets2D.Double layoutInsets) {
    return layoutInsets.left;
  }

  @Override
  protected double getPrimaryLayoutInsetsEnd(Insets2D.Double layoutInsets) {
    return layoutInsets.right;
  }

  @Override
  protected double getSecondaryLayoutInsetsStart(Insets2D.Double layoutInsets) {
    return layoutInsets.top;
  }

  @Override
  protected double getSecondaryLayoutInsetsEnd(Insets2D.Double layoutInsets) {
    return layoutInsets.bottom;
  }

  @Override
  protected double getPrimaryBound(Rectangle2D.Double rect) {
    return rect.width;
  }

  @Override
  protected double getSecondaryBound(Rectangle2D.Double rect) {
    return rect.height;
  }

  @Override
  protected void setPrimaryBound(Rectangle2D.Double rect, double value) {
    rect.width = value;
  }

  @Override
  protected void setSecondaryBound(Rectangle2D.Double rect, double value) {
    rect.height = value;
  }

  @Override
  protected double getPrimaryStartOffset(
      Rectangle2D.Double layoutBounds, Insets2D.Double layoutInsets) {
    return layoutBounds.x + layoutInsets.left;
  }

  // --- Positionnement des enfants selon l'alignement vertical ---

  @Override
  protected void buildChildBounds(
      Figure child,
      Rectangle2D.Double layoutBounds,
      Insets2D.Double layoutInsets,
      Insets2D.Double insets,
      double primaryOffset,
      double primarySize,
      double secondarySize,
      Alignment alignment) {

    double x = primaryOffset + insets.left;
    switch (alignment) {
      case LEADING:
        child.setBounds(
            new Point2D.Double(x, layoutBounds.y + layoutInsets.top + insets.top),
            new Point2D.Double(
                x + primarySize, layoutBounds.y + layoutInsets.top + insets.top + secondarySize));
        break;
      case TRAILING:
        child.setBounds(
            new Point2D.Double(
                x,
                layoutBounds.y
                    + layoutBounds.height
                    - layoutInsets.bottom
                    - insets.bottom
                    - secondarySize),
            new Point2D.Double(
                x + primarySize,
                layoutBounds.y + layoutBounds.height - layoutInsets.bottom - insets.bottom));
        break;
      case CENTER:
        child.setBounds(
            new Point2D.Double(
                x, layoutBounds.y + layoutInsets.top + (layoutBounds.height - secondarySize) / 2d),
            new Point2D.Double(
                x + primarySize,
                layoutBounds.y + layoutInsets.top + (layoutBounds.height + secondarySize) / 2d));
        break;
      case BLOCK:
      default:
        child.setBounds(
            new Point2D.Double(x, layoutBounds.y + layoutInsets.top + insets.top),
            new Point2D.Double(
                x + primarySize,
                layoutBounds.y + layoutBounds.height - layoutInsets.bottom - insets.bottom));
        break;
    }
  }
}
