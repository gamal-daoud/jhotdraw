/*
 * @(#)SlantedLiner.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.liner;

import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.utils.geom.Geom;
import org.jhotdraw.utils.geom.path.BezierPath;

/** SlantedLiner. */
public class SlantedLiner implements Liner {
  private static final double DEFAULT_SLANT_SIZE = 20;
  private static final int PATH_SIZE_TWO_FIGURES = 4;
  private static final int PATH_SIZE_SAME_FIGURE = 5;

  // Node indices
  private static final int START_NODE = 0;
  private static final int FIRST_INTERMEDIATE_NODE = 1;
  private static final int SECOND_INTERMEDIATE_NODE = 2;
  private static final int THIRD_INTERMEDIATE_NODE = 3;
  private static final int INSERT_INDEX = 1; // used when adding a new node

  private double slantSize;

  public SlantedLiner() {
    this(DEFAULT_SLANT_SIZE);
  }

  public SlantedLiner(double slantSize) {
    this.slantSize = slantSize;
  }

  @Override
  public Collection<Handle> createHandles(BezierPath path) {
    return Collections.emptyList();
  }

  @Override
  public void lineout(ConnectionFigure figure) {
    BezierPath path = ((LineConnectionFigure) figure).getBezierPath();
    Connector start = figure.getStartConnector();
    Connector end = figure.getEndConnector();
    if (start == null || end == null || path == null) {
      return;
    }
    // Special treatment if the connection connects the same figure
    if (figure.getStartFigure() == figure.getEndFigure()) {
      // Ensure path has exactly four nodes
      while (path.size() < PATH_SIZE_TWO_FIGURES) {
        path.add(INSERT_INDEX, new BezierPath.Node(0, 0));
      }
      while (path.size() > PATH_SIZE_SAME_FIGURE) {
        path.remove(INSERT_INDEX);
      }
      Point2D.Double sp = start.findStart(figure);
      Point2D.Double ep = end.findEnd(figure);
      Rectangle2D.Double sb = start.getBounds();
      Rectangle2D.Double eb = end.getBounds();
      int soutcode = sb.outcode(sp);
      if (soutcode == 0) {
        soutcode = Geom.outcode(sb, eb);
      }
      int eoutcode = eb.outcode(ep);
      if (eoutcode == 0) {
        eoutcode = Geom.outcode(sb, eb);
      }
      path.nodes().get(START_NODE).moveTo(sp);
      path.nodes().get(path.size() - 1).moveTo(ep);
      switch (soutcode) {
        case Geom.OUT_TOP:
          eoutcode = Geom.OUT_LEFT;
          break;
        case Geom.OUT_RIGHT:
          eoutcode = Geom.OUT_TOP;
          break;
        case Geom.OUT_BOTTOM:
          eoutcode = Geom.OUT_RIGHT;
          break;
        case Geom.OUT_LEFT:
          eoutcode = Geom.OUT_BOTTOM;
          break;
        default:
          eoutcode = Geom.OUT_TOP;
          soutcode = Geom.OUT_RIGHT;
          break;
      }
      path.nodes().get(FIRST_INTERMEDIATE_NODE).moveTo(sp.x + slantSize, sp.y);
      if ((soutcode & Geom.OUT_RIGHT) != 0) {
        path.nodes().get(FIRST_INTERMEDIATE_NODE).moveTo(sp.x + slantSize, sp.y);
      } else if ((soutcode & Geom.OUT_LEFT) != 0) {
        path.nodes().get(FIRST_INTERMEDIATE_NODE).moveTo(sp.x - slantSize, sp.y);
      } else if ((soutcode & Geom.OUT_BOTTOM) != 0) {
        path.nodes().get(FIRST_INTERMEDIATE_NODE).moveTo(sp.x, sp.y + slantSize);
      } else {
        path.nodes().get(FIRST_INTERMEDIATE_NODE).moveTo(sp.x, sp.y - slantSize);
      }
      if ((eoutcode & Geom.OUT_RIGHT) != 0) {
        path.nodes().get(THIRD_INTERMEDIATE_NODE).moveTo(ep.x + slantSize, ep.y);
      } else if ((eoutcode & Geom.OUT_LEFT) != 0) {
        path.nodes().get(THIRD_INTERMEDIATE_NODE).moveTo(ep.x - slantSize, ep.y);
      } else if ((eoutcode & Geom.OUT_BOTTOM) != 0) {
        path.nodes().get(THIRD_INTERMEDIATE_NODE).moveTo(ep.x, ep.y + slantSize);
      } else {
        path.nodes().get(THIRD_INTERMEDIATE_NODE).moveTo(ep.x, ep.y - slantSize);
      }
      switch (soutcode) {
        case Geom.OUT_RIGHT:
          path.nodes()
              .get(SECOND_INTERMEDIATE_NODE)
              .moveTo(
                  path.nodes().get(FIRST_INTERMEDIATE_NODE).x[0],
                  path.nodes().get(THIRD_INTERMEDIATE_NODE).y[0]);
          break;
        case Geom.OUT_TOP:
          path.nodes()
              .get(SECOND_INTERMEDIATE_NODE)
              .moveTo(
                  path.nodes().get(FIRST_INTERMEDIATE_NODE).y[0],
                  path.nodes().get(THIRD_INTERMEDIATE_NODE).x[0]);
          break;
        case Geom.OUT_LEFT:
          path.nodes()
              .get(SECOND_INTERMEDIATE_NODE)
              .moveTo(
                  path.nodes().get(FIRST_INTERMEDIATE_NODE).x[0],
                  path.nodes().get(THIRD_INTERMEDIATE_NODE).y[0]);
          break;
        case Geom.OUT_BOTTOM:
        default:
          path.nodes()
              .get(SECOND_INTERMEDIATE_NODE)
              .moveTo(
                  path.nodes().get(FIRST_INTERMEDIATE_NODE).y[0],
                  path.nodes().get(THIRD_INTERMEDIATE_NODE).x[0]);
          break;
      }
      // Regular treatment if the connection connects to two different figures
    } else {
      // Ensure path has exactly four nodes
      while (path.size() < PATH_SIZE_TWO_FIGURES) {
        path.add(INSERT_INDEX, new BezierPath.Node(0, 0));
      }
      while (path.size() > PATH_SIZE_TWO_FIGURES) {
        path.remove(INSERT_INDEX);
      }
      Point2D.Double sp = start.findStart(figure);
      Point2D.Double ep = end.findEnd(figure);
      Rectangle2D.Double sb = start.getBounds();
      Rectangle2D.Double eb = end.getBounds();
      int soutcode = sb.outcode(sp);
      if (soutcode == 0) {
        if (sp.x <= sb.x) {
          soutcode = Geom.OUT_LEFT;
        } else if (sp.y <= sb.y) {
          soutcode = Geom.OUT_TOP;
        } else if (sp.x >= sb.x + sb.width) {
          soutcode = Geom.OUT_RIGHT;
        } else if (sp.y >= sb.y + sb.height) {
          soutcode = Geom.OUT_BOTTOM;
        } else {
          soutcode = Geom.outcode(sb, eb);
        }
      }
      int eoutcode = eb.outcode(ep);
      if (eoutcode == 0) {
        if (ep.x <= eb.x) {
          eoutcode = Geom.OUT_LEFT;
        } else if (ep.y <= eb.y) {
          eoutcode = Geom.OUT_TOP;
        } else if (ep.x >= eb.x + eb.width) {
          eoutcode = Geom.OUT_RIGHT;
        } else if (ep.y >= eb.y + eb.height) {
          eoutcode = Geom.OUT_BOTTOM;
        } else {
          eoutcode = Geom.outcode(sb, eb);
        }
      }
      path.nodes().get(START_NODE).moveTo(sp);
      path.nodes().get(path.size() - 1).moveTo(ep);
      if ((soutcode & Geom.OUT_RIGHT) != 0) {
        path.nodes().get(FIRST_INTERMEDIATE_NODE).moveTo(sp.x + slantSize, sp.y);
      } else if ((soutcode & Geom.OUT_LEFT) != 0) {
        path.nodes().get(FIRST_INTERMEDIATE_NODE).moveTo(sp.x - slantSize, sp.y);
      } else if ((soutcode & Geom.OUT_BOTTOM) != 0) {
        path.nodes().get(FIRST_INTERMEDIATE_NODE).moveTo(sp.x, sp.y + slantSize);
      } else {
        path.nodes().get(FIRST_INTERMEDIATE_NODE).moveTo(sp.x, sp.y - slantSize);
      }
      if ((eoutcode & Geom.OUT_RIGHT) != 0) {
        path.nodes().get(SECOND_INTERMEDIATE_NODE).moveTo(ep.x + slantSize, ep.y);
      } else if ((eoutcode & Geom.OUT_LEFT) != 0) {
        path.nodes().get(SECOND_INTERMEDIATE_NODE).moveTo(ep.x - slantSize, ep.y);
      } else if ((eoutcode & Geom.OUT_BOTTOM) != 0) {
        path.nodes().get(SECOND_INTERMEDIATE_NODE).moveTo(ep.x, ep.y + slantSize);
      } else {
        path.nodes().get(SECOND_INTERMEDIATE_NODE).moveTo(ep.x, ep.y - slantSize);
      }
    }
    // Ensure all path nodes are straight
    for (BezierPath.Node node : path.nodes()) {
      node.setMask(BezierPath.C0_MASK);
    }
    path.invalidatePath();
  }

  //  @Override
  //  public void read(DOMInput in) {
  //    slantSize = in.getAttribute("slant", 20d);
  //  }
  //
  //  @Override
  //  public void write(DOMOutput out) {
  //    out.addAttribute("slant", slantSize);
  //  }

  @Override
  public Liner clone() {
    try {
      return (Liner) super.clone();
    } catch (CloneNotSupportedException ex) {
      InternalError error = new InternalError(ex.getMessage());
      error.initCause(ex);
      throw error;
    }
  }
}
