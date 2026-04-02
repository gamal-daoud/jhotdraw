/*
 * @(#)AbstractLinearLayouter.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.layouter;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.geom.*;
import org.jhotdraw.draw.AttributeKeys.Alignment;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.geom.Dimension2DDouble;
import org.jhotdraw.utils.geom.Insets2D;

/**
 * Superclasse abstraite commune à {@link HorizontalLayouter} et {@link VerticalLayouter}.
 *
 * <p>Elle factorise le calcul des dimensions agrégées (calculateLayout) et le positionnement des
 * enfants selon l'alignement (layout). Les sous-classes définissent uniquement la direction de
 * disposition (axe principal et axe secondaire) via les méthodes abstraites {@link
 * #getPrimaryPreferredSize}, {@link #getSecondaryPreferredSize}, {@link #buildChildBounds} et
 * {@link #advancePrimaryOffset}.
 *
 * <p>Grande modification GM1 : ajout d'une super-classe pour supprimer des méthodes dupliquées
 * entre HorizontalLayouter et VerticalLayouter.
 */
public abstract class AbstractLinearLayouter extends AbstractLayouter {

  /**
   * Retourne la dimension principale (largeur pour horizontal, hauteur pour vertical) de la taille
   * préférée d'un enfant.
   */
  protected abstract double getPrimaryPreferredSize(Dimension2DDouble size);

  /**
   * Retourne la dimension secondaire (hauteur pour horizontal, largeur pour vertical) de la taille
   * préférée d'un enfant.
   */
  protected abstract double getSecondaryPreferredSize(Dimension2DDouble size);

  /**
   * Retourne la marge principale (gauche+droite pour horizontal, haut+bas pour vertical) des
   * insets d'un enfant.
   */
  protected abstract double getPrimaryInsets(Insets2D.Double insets);

  /**
   * Retourne la marge secondaire (haut+bas pour horizontal, gauche+droite pour vertical) des
   * insets d'un enfant.
   */
  protected abstract double getSecondaryInsets(Insets2D.Double insets);

  /** Retourne la marge principale de départ (left pour horizontal, top pour vertical). */
  protected abstract double getPrimaryLayoutInsetsStart(Insets2D.Double layoutInsets);

  /** Retourne la marge principale de fin (right pour horizontal, bottom pour vertical). */
  protected abstract double getPrimaryLayoutInsetsEnd(Insets2D.Double layoutInsets);

  /** Retourne la marge secondaire de début (top pour horizontal, left pour vertical). */
  protected abstract double getSecondaryLayoutInsetsStart(Insets2D.Double layoutInsets);

  /**
   * Calcule les bornes à assigner à un enfant selon l'alignement et la position courante sur l'axe
   * principal.
   *
   * @param child l'enfant à positionner
   * @param layoutBounds les bornes globales calculées
   * @param layoutInsets les insets globaux
   * @param insets les insets de l'enfant
   * @param primaryOffset la position courante sur l'axe principal
   * @param primarySize la taille sur l'axe principal
   * @param secondarySize la taille sur l'axe secondaire
   * @param alignment l'alignement composite
   */
  protected abstract void buildChildBounds(
      Figure child,
      Rectangle2D.Double layoutBounds,
      Insets2D.Double layoutInsets,
      Insets2D.Double insets,
      double primaryOffset,
      double primarySize,
      double secondarySize,
      Alignment alignment);

  // -------------------------------------------------------------------------
  // Implémentation commune de calculateLayout
  // -------------------------------------------------------------------------

  @Override
  public Rectangle2D.Double calculateLayout(
      CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead, double scale) {

    Insets2D.Double layoutInsets = compositeFigure.attr().get(LAYOUT_INSETS);
    if (layoutInsets == null) {
      layoutInsets = new Insets2D.Double(0, 0, 0, 0);
    }

    Rectangle2D.Double layoutBounds = new Rectangle2D.Double(anchor.x, anchor.y, 0, 0);

    for (Figure child : compositeFigure.getChildren()) {
      if (child.isVisible()) {
        Dimension2DDouble preferredSize = child.getPreferredSize(scale);
        Insets2D.Double ins = getInsets(child);

        // L'axe secondaire prend le max, l'axe principal s'accumule
        double newSecondary = Math.max(
            getSecondaryBound(layoutBounds),
            getSecondaryPreferredSize(preferredSize) + getSecondaryInsets(ins));
        setSecondaryBound(layoutBounds, newSecondary);

        setPrimaryBound(
            layoutBounds,
            getPrimaryBound(layoutBounds)
                + getPrimaryPreferredSize(preferredSize)
                + getPrimaryInsets(ins));
      }
    }

    setPrimaryBound(
        layoutBounds,
        getPrimaryBound(layoutBounds)
            + getPrimaryLayoutInsetsStart(layoutInsets)
            + getPrimaryLayoutInsetsEnd(layoutInsets));
    setSecondaryBound(
        layoutBounds,
        getSecondaryBound(layoutBounds)
            + getSecondaryLayoutInsetsStart(layoutInsets)
            + getSecondaryLayoutInsetsEnd(layoutInsets));

    return layoutBounds;
  }

  // -------------------------------------------------------------------------
  // Implémentation commune de layout
  // -------------------------------------------------------------------------

  @Override
  public Rectangle2D.Double layout(
      CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead, double scale) {

    Insets2D.Double layoutInsets = compositeFigure.attr().get(LAYOUT_INSETS);
    if (layoutInsets == null) {
      layoutInsets = new Insets2D.Double();
    }
    Alignment compositeAlignment = compositeFigure.attr().get(COMPOSITE_ALIGNMENT);
    Rectangle2D.Double layoutBounds = calculateLayout(compositeFigure, anchor, lead, scale);

    // Recalcul correct de l'offset de départ sur l'axe principal
    double primaryOffset = getPrimaryStartOffset(layoutBounds, layoutInsets);

    for (Figure child : compositeFigure.getChildren()) {
      if (child.isVisible()) {
        Insets2D.Double insets = getInsets(child);
        double primarySize = getPrimaryPreferredSize(child.getPreferredSize(scale));
        double secondarySize = getSecondaryPreferredSize(child.getPreferredSize(scale));

        buildChildBounds(
            child,
            layoutBounds,
            layoutInsets,
            insets,
            primaryOffset,
            primarySize,
            secondarySize,
            compositeAlignment);

        primaryOffset += primarySize + getPrimaryInsets(insets);
      }
    }
    return layoutBounds;
  }

  protected abstract double getPrimaryBound(Rectangle2D.Double rect);

  /** Retourne la valeur de la dimension « secondaire » dans le rectangle (height ou width). */
  protected abstract double getSecondaryBound(Rectangle2D.Double rect);

  /** Affecte la dimension « principale » dans le rectangle. */
  protected abstract void setPrimaryBound(Rectangle2D.Double rect, double value);

  /** Affecte la dimension « secondaire » dans le rectangle. */
  protected abstract void setSecondaryBound(Rectangle2D.Double rect, double value);

  /** Retourne la marge secondaire de fin (bottom pour horizontal, right pour vertical). */
  protected abstract double getSecondaryLayoutInsetsEnd(Insets2D.Double layoutInsets);

  /** Retourne l'offset de départ sur l'axe principal. */
  protected abstract double getPrimaryStartOffset(
      Rectangle2D.Double layoutBounds, Insets2D.Double layoutInsets);
}
