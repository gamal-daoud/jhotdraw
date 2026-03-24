package org.jhotdraw.draw.handle;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jhotdraw.utils.util.ReversedList;

/**
 * Gère les handles de sélection et secondaires pour une {@link DrawingView}.
 * Cette classe extrait la logique des handles de {@link DefaultDrawingView}.
 */
public class HandleManager {

  private final List<Handle> selectionHandles = new ArrayList<>();
  private final List<Handle> secondaryHandles = new ArrayList<>();

  private boolean handlesAreValid = true;

  private Handle activeHandle;

  /**
   * Retourne une vue non modifiable des handles de sélection.
   */
  public List<Handle> getSelectionHandles() {
    return Collections.unmodifiableList(selectionHandles);
  }

  /**
   * Retourne une vue non modifiable des handles secondaires.
   */
  public List<Handle> getSecondaryHandles() {
    return Collections.unmodifiableList(secondaryHandles);
  }

  /**
   * Ajoute un handle de sélection.
   */
  public void addSelectionHandle(Handle handle) {
    selectionHandles.add(handle);
  }

  /**
   * Ajoute un handle secondaire.
   */
  public void addSecondaryHandle(Handle handle) {
    secondaryHandles.add(handle);
  }

  /**
   * Supprime tous les handles de sélection et les dispose.
   */
  public void clearSelectionHandles() {
    for (Handle h : selectionHandles) {
      h.dispose();
    }
    selectionHandles.clear();
  }

  /**
   * Supprime tous les handles secondaires et les dispose.
   */
  public void clearSecondaryHandles() {
    for (Handle h : secondaryHandles) {
      h.dispose();
    }
    secondaryHandles.clear();
  }

  /**
   * Indique si les handles sont considérés comme valides.
   */
  public boolean areHandlesValid() {
    return handlesAreValid;
  }

  /**
   * Modifie l'état de validité des handles.
   */
  public void setHandlesValid(boolean valid) {
    this.handlesAreValid = valid;
  }

  /**
   * Retourne le handle actif (celui qui a le focus).
   */
  public Handle getActiveHandle() {
    return activeHandle;
  }

  /**
   * Définit le handle actif.
   */
  public void setActiveHandle(Handle handle) {
    this.activeHandle = handle;
  }

  /**
   * Recherche un handle à une position donnée (coordonnées écran).
   * @return le handle trouvé ou {@code null}.
   */
  public Handle findHandle(Point p) {
    // Les handles secondaires sont prioritaires
    for (Handle handle : new ReversedList<>(secondaryHandles)) {
      if (handle.contains(p)) {
        return handle;
      }
    }
    for (Handle handle : new ReversedList<>(selectionHandles)) {
      if (handle.contains(p)) {
        return handle;
      }
    }
    return null;
  }
}
