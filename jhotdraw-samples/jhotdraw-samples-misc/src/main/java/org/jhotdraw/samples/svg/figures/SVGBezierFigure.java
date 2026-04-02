/*
 * @(#)SVGBezierFigure.java
 *
 * Copyright (c) 2007-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.figures;

import static org.jhotdraw.draw.AttributeKeys.STROKE_CAP;
import static org.jhotdraw.draw.AttributeKeys.STROKE_JOIN;
import static org.jhotdraw.draw.AttributeKeys.STROKE_MITER_LIMIT;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import static org.jhotdraw.draw.AttributeKeys.UNCLOSED_PATH_FILLED;

import java.awt.BasicStroke;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.handle.BezierNodeHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.utils.geom.Geom;
import org.jhotdraw.utils.geom.path.BezierPath;
import org.jhotdraw.utils.util.ResourceBundleUtil;

/**
 * SVGBezierFigure is not an actual SVG element, it is used by SVGPathFigure to represent a single
 * BezierPath segment within an SVG path.
 */
public class SVGBezierFigure extends BezierFigure {

  private static final long serialVersionUID = 1L;
  private transient Rectangle2D.Double cachedDrawingArea;

  public SVGBezierFigure() {
    this(false);
  }

  public SVGBezierFigure(boolean isClosed) {
    super(isClosed);
    attr().set(UNCLOSED_PATH_FILLED, true);
  }

  public Collection<Handle> createHandles(SVGPathFigure pathFigure, int detailLevel) {
    LinkedList<Handle> handles = new LinkedList<Handle>();
    switch (detailLevel % 2) {
      case 0:
        for (int i = 0, n = path.size(); i < n; i++) {
          handles.add(new BezierNodeHandle(this, i, pathFigure));
        }
        break;
      case 1:
        TransformHandleKit.addTransformHandles(this, handles);
        break;
      default:
        break;
    }
    return handles;
  }

  @Override
  public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    if (evt.getClickCount() == 2 /* && view.getHandleDetailLevel() == 0*/) {
      willChange();
      // Apply inverse of transform to point
      if (attr().get(TRANSFORM) != null) {
        try {
          p = (Point2D.Double) attr().get(TRANSFORM).inverseTransform(p, new Point2D.Double());
        } catch (NoninvertibleTransformException ex) {
          System.err.println(
              "Warning: SVGBezierFigure.handleMouseClick. Figure has noninvertible Transform.");
        }
      }
      final OptionalInt index = findSegment(p, 5 / view.getScaleFactor());
      if (index.isPresent()) {
        final int nodeIndex = index.getAsInt() + 1;
        splitSegment(p, 5 / view.getScaleFactor());
        final BezierPath.Node newNode = getNode(nodeIndex);
        fireUndoableEditHappened(new AbstractUndoableEdit() {
          private static final long serialVersionUID = 1L;

          @Override
          public String getPresentationName() {
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
            return labels.getString("edit.bezierPath.splitSegment.text");
          }

          @Override
          public void redo() throws CannotRedoException {
            super.redo();
            willChange();
            addNode(nodeIndex, newNode);
            changed();
          }

          @Override
          public void undo() throws CannotUndoException {
            super.undo();
            willChange();
            removeNode(nodeIndex);
            changed();
          }
        });
        changed();
        evt.consume();
        return true;
      }
    }
    return false;
  }

  @Override
  public void transform(AffineTransform tx) {
    if (attr().get(TRANSFORM) != null
        || (tx.getType() & (AffineTransform.TYPE_TRANSLATION)) != tx.getType()) {
      if (attr().get(TRANSFORM) == null) {
        TRANSFORM.setClone(this, tx);
      } else {
        AffineTransform t = TRANSFORM.getClone(this);
        t.preConcatenate(tx);
        attr().set(TRANSFORM, t);
      }
    } else {
      super.transform(tx);
    }
  }

  @Override
  public Rectangle2D.Double getDrawingArea(double scale) {
    if (cachedDrawingArea == null) {
      if (attr().get(TRANSFORM) == null) {
        cachedDrawingArea = path.getBounds2D();
      } else {
        BezierPath p2 = path.clone();
        p2.transform(attr().get(TRANSFORM));
        cachedDrawingArea = p2.getBounds2D();
      }
      double strokeTotalWidth = AttributeKeys.getStrokeTotalWidth(this, 1.0);
      double width = strokeTotalWidth / 2d;
      if (attr().get(STROKE_JOIN) == BasicStroke.JOIN_MITER) {
        width *= attr().get(STROKE_MITER_LIMIT);
      } else if (attr().get(STROKE_CAP) != BasicStroke.CAP_BUTT) {
        width += strokeTotalWidth * 2;
      }
      Geom.grow(cachedDrawingArea, width, width);
    }
    return (Rectangle2D.Double) cachedDrawingArea.clone();
  }

  @Override
  public OptionalInt findSegment(Point2D.Double find, double tolerance) {
    // Apply inverse of transform to point
    if (attr().get(TRANSFORM) != null) {
      try {
        find = (Point2D.Double) attr().get(TRANSFORM).inverseTransform(find, new Point2D.Double());
      } catch (NoninvertibleTransformException ex) {
        System.err.println(
            "Warning: SVGBezierFigure.findSegment. Figure has noninvertible Transform.");
      }
    }
    return getBezierPath().findSegment(find, tolerance);
  }

  @Override
  public void joinSegments(Point2D.Double join, double tolerance) {
    // Apply inverse of transform to point
    if (attr().get(TRANSFORM) != null) {
      try {
        join = (Point2D.Double) attr().get(TRANSFORM).inverseTransform(join, new Point2D.Double());
      } catch (NoninvertibleTransformException ex) {
        System.err.println(
            "Warning: SVGBezierFigure.findSegment. Figure has noninvertible Transform.");
      }
    }
    OptionalInt i = getBezierPath().findSegment(join, tolerance);
    if (i.isPresent() && i.getAsInt() > 1) {
      removeNode(i.getAsInt());
    }
  }

  @Override
  public void splitSegment(Point2D.Double split, double tolerance) {
    // Apply inverse of transform to point
    if (attr().get(TRANSFORM) != null) {
      try {
        split =
            (Point2D.Double) attr().get(TRANSFORM).inverseTransform(split, new Point2D.Double());
      } catch (NoninvertibleTransformException ex) {
        System.err.println(
            "Warning: SVGBezierFigure.findSegment. Figure has noninvertible Transform.");
      }
    }
    OptionalInt i = getBezierPath().findSegment(split, tolerance);
    if (i.isPresent()) {
      addNode(i.getAsInt() + 1, new BezierPath.Node(split));
    }
  }

  /**
   * Transforms all coords of the figure by the current TRANSFORM attribute and then sets the
   * TRANSFORM attribute to null.
   */
  public void flattenTransform() {
    if (attr().get(TRANSFORM) != null) {
      path.transform(attr().get(TRANSFORM));
      attr().set(TRANSFORM, null);
    }
    invalidate();
  }

  @Override
  public void invalidate() {
    super.invalidate();
    cachedDrawingArea = null;
  }
}
