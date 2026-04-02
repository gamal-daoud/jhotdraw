/*
 * @(#)ChopDelegatingConnector.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import java.awt.geom.Point2D;
import java.util.function.BiFunction;
import org.jhotdraw.draw.figure.Figure;

/**
 * Un {@link Connector} générique qui délègue le calcul du point de « chop » à
 * une fonction
 * fournie à la construction (pattern Strategy via {@link BiFunction}).
 *
 * <p>
 * <b>Grande modification GM2 — Fusion de classes :</b><br>
 * {@code ChopBezierConnector} et {@code ChopTriangleConnector} étaient deux
 * classes distinctes
 * dont le corps était strictement identique — seul le type casté différait. Ces
 * deux classes ont
 * été fusionnées dans {@code ChopDelegatingConnector}, qui accepte en paramètre
 * une stratégie de
 * « chop » applicable à n'importe quelle figure.
 *
 * <p>
 * Les classes d'origine ({@code ChopBezierConnector},
 * {@code ChopTriangleConnector}) sont
 * conservées comme façades dépréciées ({@code @Deprecated}) pour maintenir la
 * compatibilité
 * binaire avec le code existant.
 *
 * @see ChopBezierConnector
 * @see ChopTriangleConnector
 */
public class ChopDelegatingConnector extends ChopRectangleConnector {

  private static final long serialVersionUID = 1L;

  /**
   * Fonction de « chop » déléguée (pattern Strategy). Elle reçoit la figure cible
   * (après résolution
   * via {@link #getConnectorTarget}) et le point source, et retourne le point de
   * connexion.
   *
   * <p>
   * Non-final pour permettre la sérialisation (constructeur vide requis par
   * certains
   * frameworks). Peut être {@code null} uniquement si on utilise le comportement
   * hérité de
   * {@link ChopRectangleConnector}.
   */
  private BiFunction<Figure, Point2D.Double, Point2D.Double> chopFunction;

  /**
   * Constructeur sans-argument requis pour la désérialisation. Utilise le
   * comportement
   * {@code chop} hérité de {@link ChopRectangleConnector}.
   */
  public ChopDelegatingConnector() {
    this.chopFunction = null;
  }

  /**
   * Construit un connecteur sans propriétaire avec une stratégie de chop
   * personnalisée.
   *
   * @param chopFunction la stratégie de calcul du point de connexion
   */
  public ChopDelegatingConnector(BiFunction<Figure, Point2D.Double, Point2D.Double> chopFunction) {
    this.chopFunction = chopFunction;
  }

  /**
   * Construit un connecteur avec un propriétaire et une stratégie de chop personnalisée.
   *
   * @param owner        la figure propriétaire de ce connecteur
   * @param chopFunction la stratégie de calcul du point de connexion
   */
  public ChopDelegatingConnector(
      Figure owner, BiFunction<Figure, Point2D.Double, Point2D.Double> chopFunction) {
    super(owner);
    this.chopFunction = chopFunction;
  }

  /**
   * Délègue le calcul du point de connexion à la {@code chopFunction} fournie à la construction.
   * Si aucune fonction n'est définie (constructeur vide), délègue au comportement
   * de {@link ChopRectangleConnector}.
   */
  @Override
  protected Point2D.Double chop(Figure target, Point2D.Double from) {
    if (chopFunction != null) {
      return chopFunction.apply(getConnectorTarget(target), from);
    }
    return super.chop(target, from);
  }
}
