package org.jhotdraw.draw;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires pour {@link DefaultDrawingView}.
 * Vérifie les comportements de base : coordonnées, scale, sélection, constrainer, handles.
 *
 * @author Gamal Daoud
 */
public class DefaultDrawingViewTest {

  private DefaultDrawingView view;

  @BeforeEach
  public void setUp() {
    view = new DefaultDrawingView();
  }

  /**
   * Vérifie que drawingToView convertit correctement avec le scale par défaut (1.0).
   * Le point (100, 200) doit rester (100, 200) en coordonnées vue.
   */
  @Test
  public void testDrawingToView_defaultScale() {
    Point result = view.drawingToView(new Point2D.Double(100, 200));
    assertEquals(100, result.x);
    assertEquals(200, result.y);
  }

  /**
   * Vérifie que viewToDrawing convertit correctement avec le scale par défaut (1.0).
   * Le point (100, 200) doit rester (100.0, 200.0) en coordonnées dessin.
   */
  @Test
  public void testViewToDrawing_defaultScale() {
    Point2D.Double result = view.viewToDrawing(new Point(100, 200));
    assertEquals(100.0, result.x, 0.001);
    assertEquals(200.0, result.y, 0.001);
  }

  /**
   * Vérifie que drawingToView puis viewToDrawing est un aller-retour cohérent.
   * Les coordonnées initiales doivent être retrouvées à 1 pixel près.
   */
  @Test
  public void testDrawingToViewAndBack_roundtrip() {
    Point2D.Double original = new Point2D.Double(150, 250);
    Point viewPoint = view.drawingToView(original);
    Point2D.Double back = view.viewToDrawing(viewPoint);
    assertEquals(original.x, back.x, 1.0);
    assertEquals(original.y, back.y, 1.0);
  }

  /**
   * Vérifie que drawingToView applique correctement un facteur d'échelle de 2.
   * Le point (50, 100) doit devenir (100, 200) en vue.
   */
  @Test
  public void testDrawingToView_withScale() {
    view.setScaleFactor(2.0);
    Point result = view.drawingToView(new Point2D.Double(50, 100));
    assertEquals(100, result.x);
    assertEquals(200, result.y);
  }

  /**
   * Vérifie que viewToDrawing divise correctement par le facteur d'échelle.
   * Le point vue (100, 200) doit donner (50.0, 100.0) en dessin avec scale 2.
   */
  @Test
  public void testViewToDrawing_withScale() {
    view.setScaleFactor(2.0);
    Point2D.Double result = view.viewToDrawing(new Point(100, 200));
    assertEquals(50.0, result.x, 0.001);
    assertEquals(100.0, result.y, 0.001);
  }

  /**
   * Vérifie que le scaleFactor vaut 1.0 à la construction.
   * Aucune transformation ne doit être appliquée par défaut.
   */
  @Test
  public void testGetScaleFactor_defaultIsOne() {
    assertEquals(1.0, view.getScaleFactor());
  }

  /**
   * Vérifie que setScaleFactor modifie bien la valeur retournée par getScaleFactor.
   * La nouvelle valeur doit être exactement celle passée en paramètre.
   */
  @Test
  public void testSetScaleFactor_changesValue() {
    view.setScaleFactor(2.0);
    assertEquals(2.0, view.getScaleFactor());
  }

  /**
   * Vérifie que le double buffering est activé par défaut à la construction.
   * La valeur initiale de isDrawingDoubleBuffered doit être true.
   */
  @Test
  public void testIsDrawingDoubleBuffered_defaultTrue() {
    assertTrue(view.isDrawingDoubleBuffered());
  }

  /**
   * Vérifie que setDrawingDoubleBuffered(false) désactive le double buffering.
   * isDrawingDoubleBuffered doit retourner false après l'appel.
   */
  @Test
  public void testSetDrawingDoubleBuffered_false() {
    view.setDrawingDoubleBuffered(false);
    assertFalse(view.isDrawingDoubleBuffered());
  }

  /**
   * Vérifie que le double buffering peut être réactivé après désactivation.
   * isDrawingDoubleBuffered doit retourner true après remise à true.
   */
  @Test
  public void testSetDrawingDoubleBuffered_backToTrue() {
    view.setDrawingDoubleBuffered(false);
    view.setDrawingDoubleBuffered(true);
    assertTrue(view.isDrawingDoubleBuffered());
  }

  /**
   * Vérifie que la sélection est vide à la construction de la vue.
   * isSelectionEmpty doit retourner true sans aucune figure ajoutée.
   */
  @Test
  public void testIsSelectionEmpty_initially() {
    assertTrue(view.isSelectionEmpty());
  }

  /**
   * Vérifie que le nombre de figures sélectionnées est zéro à la construction.
   * getSelectionCount doit retourner 0 sans aucune sélection.
   */
  @Test
  public void testGetSelectionCount_initially_zero() {
    assertEquals(0, view.getSelectionCount());
  }

  /**
   * Vérifie que le constrainer est invisible par défaut à la construction.
   * isConstrainerVisible doit retourner false sans modification.
   */
  @Test
  public void testIsConstrainerVisible_defaultFalse() {
    assertFalse(view.isConstrainerVisible());
  }

  /**
   * Vérifie que setConstrainerVisible(true) rend le constrainer visible.
   * isConstrainerVisible doit retourner true après l'appel.
   */
  @Test
  public void testSetConstrainerVisible_true() {
    view.setConstrainerVisible(true);
    assertTrue(view.isConstrainerVisible());
  }

  /**
   * Vérifie que le constrainer peut être caché après avoir été rendu visible.
   * isConstrainerVisible doit retourner false après remise à false.
   */
  @Test
  public void testSetConstrainerVisible_falseAfterTrue() {
    view.setConstrainerVisible(true);
    view.setConstrainerVisible(false);
    assertFalse(view.isConstrainerVisible());
  }

  /**
   * Vérifie que le drawing est null à la construction de la vue.
   * Aucun drawing ne doit être associé sans appel à setDrawing.
   */
  @Test
  public void testGetDrawing_initiallyNull() {
    assertNull(view.getDrawing());
  }

  /**
   * Vérifie que le niveau de détail des handles vaut 0 par défaut.
   * getHandleDetailLevel doit retourner 0 sans modification.
   */
  @Test
  public void testGetHandleDetailLevel_defaultZero() {
    assertEquals(0, view.getHandleDetailLevel());
  }

  /**
   * Vérifie que setHandleDetailLevel modifie bien le niveau de détail.
   * La valeur passée doit être retrouvée par getHandleDetailLevel.
   */
  @Test
  public void testSetHandleDetailLevel_changesValue() {
    view.setHandleDetailLevel(2);
    assertEquals(2, view.getHandleDetailLevel());
  }

  /**
   * Vérifie que le message de drawing vide est null à la construction.
   * getEmptyDrawingMessage doit retourner null sans appel à setEmptyDrawingMessage.
   */
  @Test
  public void testGetEmptyDrawingMessage_initiallyNull() {
    assertNull(view.getEmptyDrawingMessage());
  }

  /**
   * Vérifie que setEmptyDrawingMessage enregistre correctement le message.
   * getEmptyDrawingMessage doit retourner exactement la valeur définie.
   */
  @Test
  public void testSetEmptyDrawingMessage() {
    view.setEmptyDrawingMessage("No drawing");
    assertEquals("No drawing", view.getEmptyDrawingMessage());
  }

  /**
   * Vérifie que setEmptyDrawingMessage(null) efface le message existant.
   * getEmptyDrawingMessage doit retourner null après un appel avec null.
   */
  @Test
  public void testSetEmptyDrawingMessage_null() {
    view.setEmptyDrawingMessage("No drawing");
    view.setEmptyDrawingMessage(null);
    assertNull(view.getEmptyDrawingMessage());
  }

  /**
   * Vérifie que le handle actif est null à la construction de la vue.
   * getActiveHandle doit retourner null sans sélection ni interaction.
   */
  @Test
  public void testGetActiveHandle_initiallyNull() {
    assertNull(view.getActiveHandle());
  }

  /**
   * Vérifie que drawingToView convertit un Rectangle2D avec scale 1.0.
   * Les coordonnées et dimensions du rectangle doivent être inchangées.
   */
  @Test
  public void testDrawingToView_rectangle_defaultScale() {
    Rectangle result = view.drawingToView(new java.awt.geom.Rectangle2D.Double(10, 20, 100, 200));
    assertEquals(10, result.x);
    assertEquals(20, result.y);
    assertEquals(100, result.width);
    assertEquals(200, result.height);
  }
}
