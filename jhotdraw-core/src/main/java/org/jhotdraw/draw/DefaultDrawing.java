/*
 * @(#)DefaultDrawing.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.util.ReversedList;

/**
 * A default implementation of {@link Drawing} useful for drawings which contain only a few figures.
 *
 * <p>For larger drawings, {@link QuadTreeDrawing} is recommended.
 *
 * <p>FIXME - Maybe we should rename this class to SimpleDrawing or we should get rid of this class
 * altogether.
 */
public class DefaultDrawing extends AbstractDrawing {

  private static final long serialVersionUID = 1L;
  private boolean needsSorting = false;

  public DefaultDrawing() {}

  @Override
  public void basicAdd(int index, Figure figure) {
    super.basicAdd(index, figure);
    invalidateSortOrder();
  }

  @Override
  public void draw(Graphics2D g) {
    synchronized (getLock()) {
      ensureSorted();
      List<Figure> toDraw = new ArrayList<>(getChildren().size());
      Rectangle clipRect = g.getClipBounds();
      double scale = AttributeKeys.getScaleFactorFromGraphics(g);
      for (Figure f : getChildren()) {
        if (f.getDrawingArea(scale).intersects(clipRect)) {
          toDraw.add(f);
        }
      }
      draw(g, toDraw);
    }
  }

  public void draw(Graphics2D g, Collection<Figure> children) {
    Rectangle2D clipBounds = g.getClipBounds();
    double scale = AttributeKeys.getScaleFactorFromGraphics(g);
    if (clipBounds != null) {
      for (Figure f : children) {
        if (f.isVisible() && f.getDrawingArea(scale).intersects(clipBounds)) {
          f.draw(g);
        }
      }
    } else {
      for (Figure f : children) {
        if (f.isVisible()) {
          f.draw(g);
        }
      }
    }
  }

  @Override
  public List<Figure> sort(Collection<? extends Figure> c) {
    Set<Figure> unsorted = new HashSet<>();
    unsorted.addAll(c);
    List<Figure> sorted = new ArrayList<>(c.size());
    for (Figure f : getChildren()) {
      if (unsorted.contains(f)) {
        sorted.add(f);
        unsorted.remove(f);
      }
    }
    if (!unsorted.isEmpty()) {
      for (Figure f : c) {
        if (unsorted.contains(f)) {
          sorted.add(f);
          unsorted.remove(f);
        }
      }
    }
    return sorted;
  }

  @Override
  public Figure findFigure(Point2D.Double p) {
    for (Figure f : getFiguresFrontToBack()) {
      if (f.isVisible() && f.contains(p)) {
        return f;
      }
    }
    return null;
  }

  @Override
  public Figure findFigure(Point2D.Double p, double scaleDenominator) {
    for (Figure f : getFiguresFrontToBack()) {
      if (f.isVisible() && f.contains(p, scaleDenominator)) {
        return f;
      }
    }
    return null;
  }

  @Override
  public List<Figure> findFigures(Point2D.Double p) {
    return getFiguresFrontToBack().stream()
        .filter(f -> f.isVisible() && f.contains(p))
        .toList();
  }

  @Override
  public List<Figure> findFigures(Point2D.Double p, double scaleDenominator) {
    return getFiguresFrontToBack().stream()
        .filter(f -> f.isVisible() && f.contains(p, scaleDenominator))
        .toList();
  }

  @Override
  public Figure findFigureExcept(Point2D.Double p, Figure ignore) {
    for (Figure f : getFiguresFrontToBack()) {
      if (f != ignore && f.isVisible() && f.contains(p)) {
        return f;
      }
    }
    return null;
  }

  @Override
  public Figure findFigureBehind(
      Point2D.Double p, double scaleDenominator, Figure figure, Predicate<Figure> filter) {
    boolean isBehind = false;
    for (Figure f : getFiguresFrontToBack()) {
      if (isBehind) {
        if (f.isVisible()
            && f.contains(p, scaleDenominator)
            && (filter == null || filter.test(f))) {
          return f;
        }
      } else {
        isBehind = figure == f;
      }
    }
    return null;
  }

  @Override
  public Figure findFigureBehind(
      Point2D.Double p,
      double scaleDenominator,
      Collection<? extends Figure> children,
      Predicate<Figure> filter) {
    int inFrontOf = children.size();
    for (Figure f : getFiguresFrontToBack()) {
      if (inFrontOf == 0) {
        if (f.isVisible()
            && f.contains(p, scaleDenominator)
            && (filter == null || filter.test(f))) {
          return f;
        }
      } else {
        if (children.contains(f)) {
          inFrontOf--;
        }
      }
    }
    return null;
  }

  @Override
  public Figure findFigureExcept(Point2D.Double p, Collection<? extends Figure> ignore) {
    for (Figure f : getFiguresFrontToBack()) {
      if (!ignore.contains(f) && f.isVisible() && f.contains(p)) {
        return f;
      }
    }
    return null;
  }

  @Override
  public List<Figure> findFigures(Rectangle2D.Double bounds) {
    List<Figure> intersection = new ArrayList<>();
    double scale = AttributeKeys.scaleFromContext(this);
    for (Figure f : getChildren()) {
      if (f.isVisible() && f.getBounds(scale).intersects(bounds)) {
        intersection.add(f);
      }
    }
    return intersection;
  }

  @Override
  public Figure findFigureInside(Point2D.Double p) {
    Figure f = findFigure(p);
    return (f == null) ? null : f.findFigureInside(p);
  }

  /** Returns an iterator to iterate in Z-order front to back over the children. */
  @Override
  public List<Figure> getFiguresFrontToBack() {
    ensureSorted();
    return new ReversedList<>(getChildren());
  }

  /** Invalidates the sort order. */
  private void invalidateSortOrder() {
    needsSorting = true;
  }

  /** Ensures that the children are sorted in z-order sequence from back to front. */
  private void ensureSorted() {
    if (needsSorting) {
      Collections.sort(children, Comparator.comparing(Figure::getLayer));
      needsSorting = false;
    }
  }

  @Override
  public int indexOf(Figure figure) {
    return children.indexOf(figure);
  }
}
