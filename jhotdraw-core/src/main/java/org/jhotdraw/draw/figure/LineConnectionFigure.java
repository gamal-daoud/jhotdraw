/*
 * @(#)BezierBezierLineConnection.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListenerAdapter;
import org.jhotdraw.draw.handle.BezierNodeHandle;
import org.jhotdraw.draw.handle.BezierOutlineHandle;
import org.jhotdraw.draw.handle.ConnectionEndHandle;
import org.jhotdraw.draw.handle.ConnectionStartHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.liner.Liner;
import org.jhotdraw.utils.geom.path.BezierPath;

/**
 * A {@link ConnectionFigure} which connects two figures using a bezier path.
 *
 * <p>The bezier path can be laid out manually using bezier handles provided by this figure, or
 * automatically using a {@link Liner} which can be set using the JavaBeans property {@code liner}.
 */
public class LineConnectionFigure extends LineFigure implements ConnectionFigure {

  private static final long serialVersionUID = 1L;

  /** The name of the JaveBeans property {@code liner}. */
  public static final String LINER_PROPERTY = "liner";

  /** Tolerance for splitting a segment on double click (same as in BezierFigure). */
  private static final float SPLIT_TOLERANCE = 5f;

  private Connector startConnector;
  private Connector endConnector;
  private Liner liner;

  /** Handles figure changes in the start and the end figure. */
  private ConnectionHandler connectionHandler = new ConnectionHandler(this);

  private static class ConnectionHandler extends FigureListenerAdapter implements Serializable {

    private static final long serialVersionUID = 1L;
    private LineConnectionFigure owner;

    private ConnectionHandler(LineConnectionFigure owner) {
      this.owner = owner;
    }

    @Override
    public void figureRemoved(FigureEvent evt) {
      owner.fireFigureRequestRemove();
    }

    @Override
    public void figureChanged(FigureEvent e) {
      if (!owner.isChanging()) {
        if (e.getSource() == owner.getStartFigure() || e.getSource() == owner.getEndFigure()) {
          owner.willChange();
          owner.updateConnection();
          owner.changed();
        }
      }
    }
  }
  ;

  public LineConnectionFigure() {}

  // DRAWING
  // SHAPE AND BOUNDS
  /** Ensures that a connection is updated if the connection was moved. */
  @Override
  public void transform(AffineTransform tx) {
    super.transform(tx);
    updateConnection(); // make sure that we are still connected
  }

  // ATTRIBUTES
  // EDITING
  /**
   * Gets the handles of the figure. It returns the normal PolylineHandles but adds
   * ChangeConnectionHandles at the start and end.
   */
  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    ArrayList<Handle> handles = new ArrayList<>(getNodeCount());
    switch (detailLevel) {
      case -1: // Mouse hover handles
        handles.add(new BezierOutlineHandle(this, true));
        break;
      case 0:
        handles.add(new BezierOutlineHandle(this));
        if (getLiner() == null) {
          for (int i = 1, n = getNodeCount() - 1; i < n; i++) {
            handles.add(new BezierNodeHandle(this, i));
          }
        }
        handles.add(new ConnectionStartHandle(this));
        handles.add(new ConnectionEndHandle(this));
        break;
    }
    return handles;
  }

  // CONNECTING

  /** ConnectionFigures cannot be connected and always sets connectable to false. */
  @Override
  public void setConnectable(boolean newValue) {
    super.setConnectable(false);
  }

  @Override
  public void updateConnection() {
    willChange();
    if (getStartConnector() != null) {
      Point2D.Double start = getStartConnector().findStart(this);
      if (start != null) {
        setStartPoint(start);
      }
    }
    if (getEndConnector() != null) {
      Point2D.Double end = getEndConnector().findEnd(this);
      if (end != null) {
        setEndPoint(end);
      }
    }
    changed();
  }

  @Override
  public void validate() {
    super.validate();
    lineout();
  }

  @Override
  public boolean canConnect(Connector start, Connector end) {
    return start.getOwner().isConnectable() && end.getOwner().isConnectable();
  }

  @Override
  public Connector getEndConnector() {
    return endConnector;
  }

  @Override
  public Figure getEndFigure() {
    return (endConnector == null) ? null : endConnector.getOwner();
  }

  @Override
  public Connector getStartConnector() {
    return startConnector;
  }

  @Override
  public Figure getStartFigure() {
    return (startConnector == null) ? null : startConnector.getOwner();
  }

  @Override
  public void setEndConnector(Connector newEnd) {
    if (newEnd != endConnector) {
      if (endConnector != null) {
        getEndFigure().removeFigureListener(connectionHandler);
        if (getStartFigure() != null) {
          if (getDrawing() != null) {
            handleDisconnect(getStartConnector(), getEndConnector());
          }
        }
      }
      endConnector = newEnd;
      if (endConnector != null) {
        getEndFigure().addFigureListener(connectionHandler);
        if (getStartFigure() != null && getEndFigure() != null) {
          if (getDrawing() != null) {
            handleConnect(getStartConnector(), getEndConnector());
            updateConnection();
          }
        }
      }
    }
  }

  @Override
  public void setStartConnector(Connector newStart) {
    if (newStart != startConnector) {
      if (startConnector != null) {
        getStartFigure().removeFigureListener(connectionHandler);
        if (getEndFigure() != null) {
          handleDisconnect(getStartConnector(), getEndConnector());
        }
      }
      startConnector = newStart;
      if (startConnector != null) {
        getStartFigure().addFigureListener(connectionHandler);
        if (getStartFigure() != null && getEndFigure() != null) {
          handleConnect(getStartConnector(), getEndConnector());
          updateConnection();
        }
      }
    }
  }

  // COMPOSITE FIGURES
  // CLONING
  // EVENT HANDLING
  /**
   * This method is invoked, when the Figure is being removed from a Drawing. This method invokes
   * handleConnect, if the Figure is connected.
   *
   * @see #handleConnect
   */
  @Override
  public void addNotify(Drawing drawing) {
    super.addNotify(drawing);
    if (getStartConnector() != null && getEndConnector() != null) {
      handleConnect(getStartConnector(), getEndConnector());
      updateConnection();
    }
  }

  /**
   * This method is invoked, when the Figure is being removed from a Drawing. This method invokes
   * handleDisconnect, if the Figure is connected.
   *
   * @see #handleDisconnect
   */
  @Override
  public void removeNotify(Drawing drawing) {
    if (getStartConnector() != null && getEndConnector() != null) {
      handleDisconnect(getStartConnector(), getEndConnector());
    }
    // Note: we do not set the connectors to null here, because we
    // need them when we are added back to a drawing again. For example,
    // when an undo is performed, after the LineConnection has been
    // deleted.
    super.removeNotify(drawing);
  }

  /**
   * Handles the disconnection of a connection. Override this method to handle this event.
   *
   * <p>Note: This method is only invoked, when the Figure is part of a Drawing. If the Figure is
   * removed from a Drawing, this method is invoked on behalf of the removeNotify call to the
   * Figure.
   *
   * @see #removeNotify
   */
  protected void handleDisconnect(Connector start, Connector end) {}

  /**
   * Handles the connection of a connection. Override this method to handle this event.
   *
   * <p>Note: This method is only invoked, when the Figure is part of a Drawing. If the Figure is
   * added to a Drawing this method is invoked on behalf of the addNotify call to the Figure.
   */
  protected void handleConnect(Connector start, Connector end) {}

  @Override
  public LineConnectionFigure clone() {
    LineConnectionFigure that = (LineConnectionFigure) super.clone();
    that.connectionHandler = new ConnectionHandler(that);
    if (this.liner != null) {
      that.liner = this.liner.clone();
    }
    // To work properly, that must be registered as a figure listener
    // to the connected figures.
    if (this.startConnector != null) {
      that.startConnector = (Connector) this.startConnector.clone();
      that.getStartFigure().addFigureListener(that.connectionHandler);
    }
    if (this.endConnector != null) {
      that.endConnector = (Connector) this.endConnector.clone();
      that.getEndFigure().addFigureListener(that.connectionHandler);
    }
    if (that.startConnector != null && that.endConnector != null) {
      that.updateConnection();
    }
    return that;
  }

  @Override
  public void remap(Map<Figure, Figure> oldToNew, boolean disconnectIfNotInMap) {
    willChange();
    super.remap(oldToNew, disconnectIfNotInMap);
    Figure newStartFigure = null;
    Figure newEndFigure = null;
    if (getStartFigure() != null) {
      newStartFigure = oldToNew.get(getStartFigure());
      if (newStartFigure == null && !disconnectIfNotInMap) {
        newStartFigure = getStartFigure();
      }
    }
    if (getEndFigure() != null) {
      newEndFigure = oldToNew.get(getEndFigure());
      if (newEndFigure == null && !disconnectIfNotInMap) {
        newEndFigure = getEndFigure();
      }
    }
    if (newStartFigure != null) {
      setStartConnector(newStartFigure.findCompatibleConnector(getStartConnector(), true));
    } else {
      if (disconnectIfNotInMap) {
        setStartConnector(null);
      }
    }
    if (newEndFigure != null) {
      setEndConnector(newEndFigure.findCompatibleConnector(getEndConnector(), false));
    } else {
      if (disconnectIfNotInMap) {
        setEndConnector(null);
      }
    }
    updateConnection();
    changed();
  }

  @Override
  public boolean canConnect(Connector start) {
    return start.getOwner().isConnectable();
  }

  /** Handles a mouse click. */
  @Override
  public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    if (getLiner() == null && evt.getClickCount() == 2) {
      if (splitSegmentAt(p, SPLIT_TOLERANCE / view.getScaleFactor())) {
        evt.consume();
        return true;
      }
    }
    return false;
  }

  @Override
  public void setLiner(Liner newValue) {
    Liner oldValue = liner;
    this.liner = newValue;
  }

  @Override
  public void setNode(int index, BezierPath.Node p) {
    if (index != 0 && index != getNodeCount() - 1) {
      if (getStartConnector() != null) {
        Point2D.Double start = getStartConnector().findStart(this);
        if (start != null) {
          setStartPoint(start);
        }
      }
      if (getEndConnector() != null) {
        Point2D.Double end = getEndConnector().findEnd(this);
        if (end != null) {
          setEndPoint(end);
        }
      }
    }
    super.setNode(index, p);
  }

  @Override
  public void lineout() {
    if (liner != null) {
      liner.lineout(this);
    }
  }

  @Override
  public BezierPath getBezierPath() {
    return path;
  }

  @Override
  public Liner getLiner() {
    return liner;
  }

  @Override
  public void setStartPoint(Point2D.Double p) {
    setPoint(0, p);
  }

  @Override
  public void setPoint(int index, Point2D.Double p) {
    setPoint(index, 0, p);
  }

  @Override
  public void setEndPoint(Point2D.Double p) {
    setPoint(getNodeCount() - 1, p);
  }

  public void reverseConnection() {
    if (startConnector != null && endConnector != null) {
      handleDisconnect(startConnector, endConnector);
      Connector oldStartConnector = startConnector;
      startConnector = endConnector;
      endConnector = oldStartConnector;
      Point2D.Double oldStartPoint = getStartPoint();
      setStartPoint(getEndPoint());
      setEndPoint(oldStartPoint);
      handleConnect(startConnector, endConnector);
      updateConnection();
    }
  }
}
