/*
 * @(#)QuadTreeDrawing.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.utils.geom.QuadTree;
import org.jhotdraw.utils.util.*;

/**
 * An implementation of {@link Drawing} which uses a {@link org.jhotdraw.utils.geom.QuadTree} to provide a
 * good responsiveness for drawings which contain many figures.
 */
public class QuadTreeDrawing extends AbstractDrawing {

  private static final long serialVersionUID = 1L;
  private QuadTree<Figure> quadTree = new QuadTree<>();
  private boolean needsSorting = false;
  private static final double FIND_FIGURE_PIXEL_TOLERANCE = 10.0;

  @Override
  public int indexOf(Figure figure) {
    return children.indexOf(figure);
  }

  @Override
  public void basicAdd(int index, Figure figure) {
    super.basicAdd(index, figure);
    quadTree.add(figure, figure.getDrawingArea());
    needsSorting = true;
  }

  @Override
  public Figure basicRemoveChild(int index) {
    Figure figure = getChild(index);
    quadTree.remove(figure);
    needsSorting = true;
    super.basicRemoveChild(index);
    return figure;
  }

  @Override
  public void draw(Graphics2D g) {
    Rectangle2D clipBounds = g.getClipBounds();
    if (clipBounds != null) {
      draw(g, sort(quadTree.findIntersects(clipBounds)));
    } else {
      draw(g, children);
    }
  }

  /** Implementation note: Sorting can not be done for orphaned children. */
  @Override
  public List<Figure> sort(Collection<? extends Figure> c) {
    List<Figure> sorted = new ArrayList<>(c);
    Collections.sort(sorted, Comparator.comparing(Figure::getLayer));
    return sorted;
  }

  public void draw(Graphics2D g, Collection<Figure> c) {
    for (Figure f : c) {
      if (f.isVisible()) {
        f.draw(g);
      }
    }
  }

  @Override
  public Figure findFigureInside(Point2D.Double p) {
    Collection<Figure> c = quadTree.findContains(p);
    for (Figure f : getFiguresFrontToBack(c)) {
      if (c.contains(f) && f.contains(p)) {
        return f.findFigureInside(p);
      }
    }
    return null;
  }

  /** Returns an iterator to iterate in Z-order front to back over the children. */
  @Override
  public List<Figure> getFiguresFrontToBack() {
    ensureSorted();
    return new ReversedList<>(children);
  }

  protected List<Figure> getFiguresFrontToBack(Collection<Figure> smallCollection) {
    List<Figure> list = new ArrayList<>(smallCollection);
    Collections.sort(list, Comparator.comparing(Figure::getLayer).reversed());
    return list;
  }

  @Override
  public Figure findFigure(Point2D.Double p) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1:
        Figure f = c.iterator().next();
        return (f.contains(p)) ? f : null;
      default:
        for (Figure f2 : getFiguresFrontToBack(c)) {
          if (f2.contains(p)) {
            return f2;
          }
        }
        return null;
    }
  }

  @Override
  public Figure findFigure(Point2D.Double p, double scaleDenominator) {
    double tolerance = FIND_FIGURE_PIXEL_TOLERANCE / 2.0 / scaleDenominator;
    Rectangle2D.Double rect =
        new Rectangle2D.Double(p.x - tolerance, p.y - tolerance, 2 * tolerance, 2 * tolerance);
    for (Figure figure : findFigures(rect)) {
      if (figure.isVisible() && figure.contains(p, scaleDenominator)) {
        return figure;
      }
    }
    return null;
  }

  @Override
  public List<Figure> findFigures(Point2D.Double p) {
    return quadTree.findContains(p).stream().filter(f -> f.contains(p)).toList();
  }

  @Override
  public List<Figure> findFigures(Point2D.Double p, double scaleDenominator) {
    double tolerance = FIND_FIGURE_PIXEL_TOLERANCE / 2.0 / scaleDenominator;
    Rectangle2D.Double rect =
        new Rectangle2D.Double(p.x - tolerance, p.y - tolerance, 2 * tolerance, 2 * tolerance);
    return findFigures(rect).stream()
        .filter(figure -> figure.isVisible() && figure.contains(p, scaleDenominator))
        .toList();
  }

  @Override
  public Figure findFigureExcept(Point2D.Double p, Figure ignore) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1:
        Figure f = c.iterator().next();
        return (f == ignore || !f.contains(p)) ? null : f;
      default:
        for (Figure f2 : getFiguresFrontToBack(c)) {
          if (f2 != ignore && f2.contains(p)) {
            return f2;
          }
        }
        return null;
    }
  }

  @Override
  public Figure findFigureExcept(Point2D.Double p, Collection<? extends Figure> ignore) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1:
        Figure f = c.iterator().next();
        return (!ignore.contains(f) || !f.contains(p)) ? null : f;
      default:
        for (Figure f2 : getFiguresFrontToBack(c)) {
          if (!ignore.contains(f2) && f2.contains(p)) {
            return f2;
          }
        }
        return null;
    }
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
  public List<Figure> findFigures(Rectangle2D.Double r) {
    List<Figure> c = new ArrayList<>(quadTree.findIntersects(r));
    switch (c.size()) {
      case 0:
        // fall through
      case 1:
        return c;
      default:
        return getFiguresFrontToBack(c);
    }
  }

  @Override
  public void bringToFront(Figure figure) {
    if (children.remove(figure)) {
      var maxLayer = children.stream().mapToInt(f -> f.getLayer()).max().orElse(0) + 1;

      children.add(figure);
      needsSorting = true;
      fireDrawingChanged(figure.getDrawingArea());
    }
  }

  @Override
  public void sendToBack(Figure figure) {
    if (children.remove(figure)) {
      children.add(0, figure);
      needsSorting = true;
      fireDrawingChanged(figure.getDrawingArea());
    }
  }

  /** Ensures that the children are sorted in z-order sequence. */
  private void ensureSorted() {
    if (needsSorting) {
      Collections.sort(children, Comparator.comparing(Figure::getLayer));
      needsSorting = false;
    }
  }

  @Override
  public QuadTreeDrawing clone() {
    QuadTreeDrawing that = (QuadTreeDrawing) super.clone();
    that.quadTree = new QuadTree<>();
    for (Figure f : getChildren()) {
      quadTree.add(f, f.getDrawingArea());
    }
    return that;
  }

  @Override
  protected EventHandler createEventHandler() {
    return new QuadTreeEventHandler();
  }

  /** Handles all figure events fired by Figures contained in the Drawing. */
  protected class QuadTreeEventHandler extends AbstractDrawing.EventHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public void figureChanged(FigureEvent e) {
      fireFigureChanged(e.getFigure(), 0);
      if (!isChanging()) {
        quadTree.remove(e.getFigure());
        quadTree.add(e.getFigure(), e.getFigure().getDrawingArea());
        needsSorting = true;
        invalidate();
        fireDrawingChanged(e.getInvalidatedArea());
      }
    }
  }
}
